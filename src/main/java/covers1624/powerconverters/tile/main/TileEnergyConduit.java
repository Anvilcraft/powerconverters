package covers1624.powerconverters.tile.main;

import java.util.List;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyConnection;
import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import covers1624.powerconverters.grid.GridTickHandler;
import covers1624.powerconverters.grid.IGridController;
import covers1624.powerconverters.grid.INode;
import covers1624.powerconverters.util.BlockPosition;
import covers1624.powerconverters.util.IAdvancedLogTile;
import covers1624.powerconverters.util.IUpdateTileWithCords;
import covers1624.powerconverters.util.LogHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEnergyConduit extends TileEntity
    implements INode, IEnergyHandler, IUpdateTileWithCords, IAdvancedLogTile {
    private IEnergyReceiver[] receiverCache = null;
    private IEnergyProvider[] providerCache = null;
    private boolean readFromNBT = false;
    private boolean deadCache = false;
    boolean isNode = false;
    int energyForGrid = 0;
    EnergyNetwork grid;

    public TileEnergyConduit() {
        if (this.grid == null) {
            this.validate();
        }
    }

    public boolean isNotValid() {
        return super.tileEntityInvalid;
    }

    public void validate() {
        super.validate();
        this.deadCache = true;
        this.receiverCache = null;
        this.providerCache = null;
        if (super.worldObj != null && !super.worldObj.isRemote) {
            GridTickHandler.energy.addConduitForTick(this);
        }
    }

    public void invalidate() {
        super.invalidate();
        if (this.grid != null) {
            this.grid.removeConduit(this);
            this.grid.storage.modifyEnergyStored(-this.energyForGrid);
            this.grid.regenerate();
            this.deadCache = true;
            this.grid = null;
        }
    }

    private void addCache(TileEntity tile) {
        if (tile != null) {
            int x = tile.xCoord;
            int y = tile.yCoord;
            int z = tile.yCoord;
            if (x < super.xCoord) {
                this.addCache(tile, 5);
            } else if (x > super.xCoord) {
                this.addCache(tile, 4);
            } else if (z < super.zCoord) {
                this.addCache(tile, 3);
            } else if (z > super.zCoord) {
                this.addCache(tile, 2);
            } else if (y < super.yCoord) {
                this.addCache(tile, 1);
            } else if (y > super.yCoord) {
                this.addCache(tile, 0);
            }
        }
    }

    private void addCache(TileEntity tile, int side) {
        if (this.receiverCache != null) {
            this.receiverCache[side] = null;
        }

        if (this.providerCache != null) {
            this.providerCache[side] = null;
        }

        if (!(tile instanceof TileEnergyConduit) && tile instanceof IEnergyConnection
            && ((IEnergyConnection) tile)
                   .canConnectEnergy(ForgeDirection.VALID_DIRECTIONS[side])) {
            if (tile instanceof IEnergyReceiver) {
                if (this.receiverCache == null) {
                    this.receiverCache = new IEnergyReceiver[6];
                }

                this.receiverCache[side] = (IEnergyReceiver) tile;
            }

            if (tile instanceof IEnergyProvider) {
                if (this.providerCache == null) {
                    this.providerCache = new IEnergyProvider[6];
                }

                this.providerCache[side] = (IEnergyProvider) tile;
            }
        }
    }

    private void reCache() {
        if (this.deadCache) {
            ForgeDirection[] arr$ = ForgeDirection.VALID_DIRECTIONS;
            int len$ = arr$.length;

            for (int i$ = 0; i$ < len$; ++i$) {
                ForgeDirection dir = arr$[i$];
                this.addCache(BlockPosition.getAdjacentTileEntity(this, dir));
            }

            this.deadCache = false;
            this.updateInternalTypes(EnergyNetwork.HANDLER);
        }
    }

    private void incorporateTiles() {
        if (this.grid == null) {
            boolean hasGrid = false;
            ForgeDirection[] arr$ = ForgeDirection.VALID_DIRECTIONS;
            int len$ = arr$.length;

            for (int i$ = 0; i$ < len$; ++i$) {
                ForgeDirection dir = arr$[i$];
                if (!this.readFromNBT && BlockPosition.blockExists(this, dir)) {
                    TileEnergyConduit pipe
                        = (TileEnergyConduit) BlockPosition
                              .getAdjacentTileEntity(this, dir, TileEnergyConduit.class);
                    if (pipe != null && pipe.grid != null
                        && pipe.canInterface(this, dir)) {
                        if (hasGrid) {
                            pipe.grid.mergeGrid(this.grid);
                        } else {
                            pipe.grid.addConduit(this);
                            hasGrid = this.grid != null;
                        }
                    }
                }
            }
        }
    }

    public void firstTick(IGridController grid) {
        LogHelper.info("First Grid Tick");
        if (super.worldObj != null && !super.worldObj.isRemote
            && grid == EnergyNetwork.HANDLER) {
            if (this.grid == null) {
                this.incorporateTiles();
                this.setGrid(new EnergyNetwork(this));
            }

            this.readFromNBT = true;
            this.reCache();
            this.markDirty();
        }
    }

    public void updateInternalTypes(IGridController grid) {
        if (!this.deadCache && grid == EnergyNetwork.HANDLER) {
            this.isNode = true;
            if (this.grid != null) {
                this.grid.addConduit(this);
            }
        }
    }

    public boolean canConnectEnergy(ForgeDirection from) {
        return this.grid != null;
    }

    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
        return this.grid != null ? this.grid.storage.receiveEnergy(maxReceive, simulate)
                                 : 0;
    }

    public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
        return 0;
    }

    public int getEnergyStored(ForgeDirection from) {
        return this.grid != null ? this.grid.storage.getEnergyStored() : 0;
    }

    public int getMaxEnergyStored(ForgeDirection from) {
        if (this.grid != null) {
            this.grid.storage.getMaxEnergyStored();
        }

        return 0;
    }

    public void extract(ForgeDirection forgeDirection, EnergyStorage tempStorage) {}

    public int transfer(ForgeDirection forgeDirection, int energy) {
        if (this.deadCache) {
            return 0;
        } else {
            if (this.receiverCache != null) {
                IEnergyReceiver handlerTile
                    = this.receiverCache[forgeDirection.ordinal()];
                if (handlerTile != null) {
                    return handlerTile.receiveEnergy(forgeDirection, energy, false);
                }
            }

            return 0;
        }
    }

    public void setGrid(EnergyNetwork energyNetwork) {
        this.grid = energyNetwork;
    }

    public boolean canInterface(TileEnergyConduit teConduit, ForgeDirection dir) {
        if (this.receiverCache != null) {
            if (this.receiverCache[dir.ordinal()] != null) {
                return true;
            }
        } else if (this.providerCache != null && this.providerCache[dir.ordinal()] != null) {
            return true;
        }

        return false;
    }

    public void onNeighboorChanged(int x, int y, int z) {
        if (!(super.worldObj.isRemote | this.deadCache)) {
            TileEntity tile = super.worldObj.getTileEntity(x, y, z);
            if (x < super.xCoord) {
                this.addCache(tile, 5);
            } else if (x > super.xCoord) {
                this.addCache(tile, 4);
            } else if (z < super.zCoord) {
                this.addCache(tile, 3);
            } else if (z > super.zCoord) {
                this.addCache(tile, 2);
            } else if (y < super.yCoord) {
                this.addCache(tile, 1);
            } else if (y > super.yCoord) {
                this.addCache(tile, 0);
            }
        }
    }

    public void
    getTileInfo(List info, ForgeDirection side, EntityPlayer player, boolean debug) {
        info.add(this.text("-Energy-"));
        if (this.grid != null) {
            if (debug && this.isNode) {
                info.add(this.text("Throughput All: " + this.grid.distribution));
                info.add(this.text("Throughput Side: " + this.grid.distributionSide));
            }

            if (!debug) {
                float sat = 0.0F;
                if (this.grid.getNodeCount() != 0) {
                    sat = (float
                    ) (Math.ceil((double
                       ) ((float) this.grid.storage.getEnergyStored()
                          / (float) this.grid.storage.getMaxEnergyStored() * 1000.0F))
                       / 10.0);
                }

                info.add(this.text("Saturation: " + sat));
            }
        } else if (!debug) {
            info.add(this.text("Null Grid"));
        }

        if (debug) {
            if (this.grid != null) {
                info.add(this.text("Grid:" + this.grid));
                info.add(this.text(
                    "Conduits: " + this.grid.getConduitCount()
                    + ", Nodes: " + this.grid.getNodeCount()
                ));
                info.add(this.text(
                    "Grid Max: " + this.grid.storage.getMaxEnergyStored()
                    + ", Grid Cur: " + this.grid.storage.getEnergyStored()
                ));
            } else {
                info.add(this.text("Null Grid"));
            }

            info.add(this.text("Node: " + this.isNode + ", Energy: " + this.energyForGrid)
            );
        }
    }

    public IChatComponent text(String str) {
        return new ChatComponentText(str);
    }
}
