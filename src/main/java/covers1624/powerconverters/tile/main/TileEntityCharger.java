package covers1624.powerconverters.tile.main;

import java.util.Iterator;
import java.util.List;

import covers1624.powerconverters.api.charge.IChargeHandler;
import covers1624.powerconverters.api.registry.UniversalChargerRegistry;
import covers1624.powerconverters.handler.ConfigurationHandler;
import covers1624.powerconverters.init.PowerSystems;
import covers1624.powerconverters.util.BlockPosition;
import covers1624.powerconverters.util.IAdvancedLogTile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityCharger
    extends TileEntityEnergyProducer implements IAdvancedLogTile, ISidedInventory {
    private TileEntity[] sideCache = new TileEntity[6];
    private ItemStack[] slots = new ItemStack[32];

    public TileEntityCharger() {
        super(PowerSystems.powerSystemRedstoneFlux, 0, IInventory.class);
    }

    public void updateEntity() {
        super.updateEntity();
        this.searchForTiles();
        this.validateSlots();
    }

    public double produceEnergy(double energy) {
        if (energy == 0.0) {
            return 0.0;
        } else if (ConfigurationHandler.dissableUniversalCharger) {
            return energy;
        } else {
            double energyRemaining = energy;

            for (int i = 0; i < 16; ++i) {
                ItemStack stack = this.slots[i];
                if (stack != null) {
                    Iterator i$ = UniversalChargerRegistry.getChargeHandlers().iterator();

                    while (i$.hasNext()) {
                        IChargeHandler handler = (IChargeHandler) i$.next();
                        if (handler.canHandle(stack)) {
                            energyRemaining = handler.charge(stack, energyRemaining);
                            if (energyRemaining == 0.0) {
                                return 0.0;
                            }
                        }
                    }
                }
            }

            energyRemaining = this.powerTiles(energyRemaining);
            return energyRemaining;
        }
    }

    private void searchForTiles() {
        ForgeDirection[] arr$ = ForgeDirection.VALID_DIRECTIONS;
        int len$ = arr$.length;

        for (int i$ = 0; i$ < len$; ++i$) {
            ForgeDirection dir = arr$[i$];
            TileEntity tileEntity = BlockPosition.getAdjacentTileEntity(this, dir);
            if (tileEntity != null && tileEntity instanceof IInventory) {
                this.sideCache[dir.ordinal()] = tileEntity;
            }

            if (tileEntity == null) {
                this.sideCache[dir.ordinal()] = null;
            }
        }
    }

    private void validateSlots() {
    label46:
        for (int i = 0; i < 16; ++i) {
            if (this.slots[i] != null) {
                Iterator i$ = UniversalChargerRegistry.getChargeHandlers().iterator();

                while (true) {
                    while (true) {
                        IChargeHandler handler;
                        do {
                            do {
                                if (!i$.hasNext()) {
                                    continue label46;
                                }

                                handler = (IChargeHandler) i$.next();
                            } while (!handler.canHandle(this.slots[i]));
                        } while (!handler.isItemCharged(this.slots[i]));

                        for (int j = 16; j < 32; ++j) {
                            if (this.slots[j] == null) {
                                this.slots[j] = this.slots[i];
                                this.slots[i] = null;
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public double powerTiles(double energyRemaining) {
        TileEntity[] arr$ = this.sideCache;
        int len$ = arr$.length;

        for (int i$ = 0; i$ < len$; ++i$) {
            TileEntity tileEntity = arr$[i$];
            if (tileEntity != null) {
                IInventory iInventory = (IInventory) tileEntity;

                for (int i = 0; i < iInventory.getSizeInventory(); ++i) {
                    ItemStack itemStack = iInventory.getStackInSlot(i);
                    if (itemStack != null) {
                        Iterator it$
                            = UniversalChargerRegistry.getChargeHandlers().iterator();

                        while (it$.hasNext()) {
                            IChargeHandler handler = (IChargeHandler) it$.next();
                            if (handler.canHandle(itemStack)) {
                                energyRemaining
                                    = handler.charge(itemStack, energyRemaining);
                                if (energyRemaining == 0.0) {
                                    return 0.0;
                                }
                            }
                        }
                    }
                }
            }
        }

        return energyRemaining;
    }

    public void
    getTileInfo(List info, ForgeDirection side, EntityPlayer player, boolean debug) {
        info.add(this.text("-SideCache-"));

        for (int i = 0; i < this.sideCache.length; ++i) {
            String data = this.sideCache[i] != null
                ? this.sideCache[i].getClass().getName()
                : "Null";
            info.add(this.text(String.format(
                "Side: %s, Data: %s", ForgeDirection.VALID_DIRECTIONS[i], data
            )));
        }
    }

    private ChatComponentText text(String string) {
        return new ChatComponentText(string);
    }

    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < this.slots.length; ++i) {
            if (this.slots[i] != null) {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setInteger("Slot", i);
                this.slots[i].writeToNBT(nbttagcompound1);
                nbttaglist.appendTag(nbttagcompound1);
            }
        }

        tagCompound.setTag("Items", nbttaglist);
    }

    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        NBTTagList nbttaglist = tagCompound.getTagList("Items", 10);
        this.slots = new ItemStack[this.getSizeInventory()];

        for (int i = 0; i < nbttaglist.tagCount(); ++i) {
            NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
            int j = nbttagcompound1.getInteger("Slot");
            if (j >= 0 && j < this.slots.length) {
                this.slots[j] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
            }
        }
    }

    public int getSizeInventory() {
        return this.slots.length;
    }

    public ItemStack getStackInSlot(int slot) {
        return this.slots[slot];
    }

    public ItemStack decrStackSize(int slot, int ammount) {
        if (this.slots[slot] != null) {
            ItemStack itemStack;
            if (this.slots[slot].stackSize <= ammount) {
                itemStack = this.slots[slot];
                this.slots[slot] = null;
                return itemStack;
            } else {
                itemStack = this.slots[slot].splitStack(ammount);
                if (this.slots[slot].stackSize == 0) {
                    this.slots[slot] = null;
                }

                return itemStack;
            }
        } else {
            return null;
        }
    }

    public ItemStack getStackInSlotOnClosing(int slot) {
        return this.slots[slot];
    }

    public void setInventorySlotContents(int slot, ItemStack stack) {
        this.slots[slot] = stack;
        if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
            stack.stackSize = this.getInventoryStackLimit();
        }
    }

    public String getInventoryName() {
        return "Universal Charger";
    }

    public boolean hasCustomInventoryName() {
        return true;
    }

    public int getInventoryStackLimit() {
        return 64;
    }

    public boolean isUseableByPlayer(EntityPlayer p_70300_1_) {
        return true;
    }

    public void openInventory() {}

    public void closeInventory() {}

    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        if (slot < 16) {
            Iterator i$ = UniversalChargerRegistry.getChargeHandlers().iterator();

            while (i$.hasNext()) {
                IChargeHandler chargeHandler = (IChargeHandler) i$.next();
                if (chargeHandler.canHandle(stack)) {
                    return true;
                }
            }
        }

        return false;
    }

    public int[] getAccessibleSlotsFromSide(int side) {
        return new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };
    }

    public boolean canInsertItem(int slot, ItemStack stack, int side) {
        return this.isItemValidForSlot(slot, stack);
    }

    public boolean canExtractItem(int slot, ItemStack stack, int side) {
        return slot > 16;
    }
}
