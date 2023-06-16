package covers1624.powerconverters.container;

import covers1624.powerconverters.slot.ChargerInputSlot;
import covers1624.powerconverters.slot.ChargerOutputSlot;
import covers1624.powerconverters.tile.main.TileEntityCharger;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerUniversalCharger extends Container {
    private InventoryPlayer playerInventory;
    private TileEntityCharger tileCharger;

    public ContainerUniversalCharger(
        InventoryPlayer playerInventory, TileEntityCharger charger
    ) {
        this.playerInventory = playerInventory;
        this.tileCharger = charger;
        int slot = 0;

        int i;
        int j;
        int x;
        int y;
        for (i = 0; i < 4; ++i) {
            for (j = 0; j < 4; ++j) {
                x = 8 + j * 18;
                y = 18 + i * 18;
                this.addSlotToContainer(new ChargerInputSlot(this.tileCharger, slot, x, y)
                );
                ++slot;
            }
        }

        for (i = 0; i < 4; ++i) {
            for (j = 0; j < 4; ++j) {
                x = 98 + j * 18;
                y = 18 + i * 18;
                this.addSlotToContainer(
                    new ChargerOutputSlot(this.tileCharger, slot, x, y)
                );
                ++slot;
            }
        }

        this.bindPlayerInventory(playerInventory);
    }

    public boolean canInteractWith(EntityPlayer p_75145_1_) {
        return true;
    }

    public ItemStack transferStackInSlot(EntityPlayer player, int inventorySlot) {
        ItemStack itemStack = null;
        Slot slot = (Slot) super.inventorySlots.get(inventorySlot);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();
            if (inventorySlot > 15
                && this.tileCharger.isItemValidForSlot(0, itemStack2)) {
                if (!this.mergeItemStack(itemStack2, 0, 15, false)) {
                    return null;
                }

                slot.onSlotChange(itemStack2, itemStack);
            } else if (slot instanceof ChargerOutputSlot && !this.mergeItemStack(itemStack2, 32, super.inventorySlots.size(), false)) {
                return null;
            }

            if (itemStack2.stackSize == 0) {
                slot.putStack((ItemStack) null);
            } else {
                slot.onSlotChanged();
            }

            if (itemStack2.stackSize == itemStack.stackSize) {
                return null;
            }

            slot.onPickupFromSlot(player, itemStack2);
        }

        return itemStack;
    }

    protected void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
        int i;
        for (i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                int slot = j + i * 9 + 9;
                int x = 8 + j * 18;
                int y = 104 + i * 18;
                this.addSlotToContainer(new Slot(inventoryPlayer, slot, x, y));
            }
        }

        for (i = 0; i < 9; ++i) {
            this.addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 162));
        }
    }
}
