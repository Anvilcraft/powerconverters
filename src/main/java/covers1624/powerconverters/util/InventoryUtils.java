package covers1624.powerconverters.util;

import covers1624.powerconverters.slot.ChargerOutputSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class InventoryUtils {
    public static void bindPlayerInventory(
        Container container, InventoryPlayer inventoryPlayer, int xStart, int yStart
    ) {
        int i;
        for (i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                int slot = j + i * 9 + 9;
                int x = xStart + j * 18;
                int y = yStart + i * 18;
                addSlotToContainer(container, new Slot(inventoryPlayer, slot, x, y));
            }
        }

        for (i = 0; i < 9; ++i) {
            addSlotToContainer(
                container, new Slot(inventoryPlayer, i, xStart + i * 18, yStart + 58)
            );
        }
    }

    public static Slot addSlotToContainer(Container container, Slot slot) {
        slot.slotNumber = container.inventorySlots.size();
        container.inventorySlots.add(slot);
        container.inventoryItemStacks.add((Object) null);
        return slot;
    }

    public static ItemStack
    transferStackInSlot(Container container, EntityPlayer player, int slotIndex) {
        ItemStack originalStack = null;
        Slot slot = (Slot) container.inventorySlots.get(slotIndex);
        int numSlots = container.inventorySlots.size();
        if (slot != null && slot.getHasStack()) {
            ItemStack stackInSlot = slot.getStack();
            originalStack = stackInSlot.copy();
            if (slotIndex < numSlots - 36
                || !tryShiftItem(container, stackInSlot, numSlots)) {
                if (slotIndex >= numSlots - 36 && slotIndex < numSlots - 9) {
                    if (!shiftItemStack(container, stackInSlot, numSlots - 9, numSlots)) {
                        return null;
                    }
                } else if (slotIndex >= numSlots - 9 && slotIndex < numSlots) {
                    if (!shiftItemStack(
                            container, stackInSlot, numSlots - 36, numSlots - 9
                        )) {
                        return null;
                    }
                } else if (!shiftItemStack(
                               container, stackInSlot, numSlots - 36, numSlots
                           )) {
                    return null;
                }
            }

            slot.onSlotChange(stackInSlot, originalStack);
            if (stackInSlot.stackSize <= 0) {
                slot.putStack((ItemStack) null);
            } else {
                slot.onSlotChanged();
            }

            if (stackInSlot.stackSize == originalStack.stackSize) {
                return null;
            }

            slot.onPickupFromSlot(player, stackInSlot);
        }

        return originalStack;
    }

    public static boolean
    tryShiftItem(Container container, ItemStack stackToShift, int numSlots) {
        for (int machineIndex = 0; machineIndex < numSlots - 36; ++machineIndex) {
            Slot slot = (Slot) container.inventorySlots.get(machineIndex);
            if (!(slot instanceof ChargerOutputSlot) && slot.isItemValid(stackToShift)
                && shiftItemStack(
                    container, stackToShift, machineIndex, machineIndex + 1
                )) {
                return true;
            }
        }

        return false;
    }

    public static boolean
    shiftItemStack(Container container, ItemStack stackToShift, int start, int end) {
        boolean changed = false;
        int slotIndex;
        Slot slot;
        ItemStack stackInSlot;
        if (stackToShift.isStackable()) {
            for (slotIndex = start; stackToShift.stackSize > 0 && slotIndex < end;
                 ++slotIndex) {
                slot = (Slot) container.inventorySlots.get(slotIndex);
                stackInSlot = slot.getStack();
                if (stackInSlot != null && canStacksMerge(stackInSlot, stackToShift)) {
                    int max = stackInSlot.stackSize + stackToShift.stackSize;
                    max = Math.min(
                        stackToShift.getMaxStackSize(), slot.getSlotStackLimit()
                    );
                    if (max <= max) {
                        stackToShift.stackSize = 0;
                        stackInSlot.stackSize = max;
                        slot.onSlotChanged();
                        changed = true;
                    } else if (stackInSlot.stackSize < max) {
                        stackToShift.stackSize -= max - stackInSlot.stackSize;
                        stackInSlot.stackSize = max;
                        slot.onSlotChanged();
                        changed = true;
                    }
                }
            }
        }

        if (stackToShift.stackSize > 0) {
            for (slotIndex = start; stackToShift.stackSize > 0 && slotIndex < end;
                 ++slotIndex) {
                slot = (Slot) container.inventorySlots.get(slotIndex);
                stackInSlot = slot.getStack();
                if (stackInSlot == null) {
                    int max = Math.min(
                        stackToShift.getMaxStackSize(), slot.getSlotStackLimit()
                    );
                    stackInSlot = stackToShift.copy();
                    stackInSlot.stackSize = Math.min(stackToShift.stackSize, max);
                    stackToShift.stackSize -= stackInSlot.stackSize;
                    slot.putStack(stackInSlot);
                    slot.onSlotChanged();
                    changed = true;
                }
            }
        }

        return changed;
    }

    public static boolean canStacksMerge(ItemStack stack1, ItemStack stack2) {
        if (stack1 != null && stack2 != null) {
            if (!stack1.isItemEqual(stack2)) {
                return false;
            } else {
                return ItemStack.areItemStackTagsEqual(stack1, stack2);
            }
        } else {
            return false;
        }
    }
}
