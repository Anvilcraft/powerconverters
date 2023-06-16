package covers1624.powerconverters.tile.main;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import covers1624.powerconverters.api.bridge.BridgeSideData;
import covers1624.powerconverters.handler.ConfigurationHandler;
import covers1624.powerconverters.net.EnergyBridgeSyncPacket;
import covers1624.powerconverters.util.BlockPosition;
import covers1624.powerconverters.util.INeighboorUpdateTile;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityEnergyBridge extends TileEntity implements INeighboorUpdateTile {
    private double _energyStored;
    private double _energyStoredMax;
    private double _energyScaledClient;
    private double _energyStoredLast;
    private boolean _isInputLimited;
    private Map _producerTiles;
    private Map clientSideData;
    private Map _producerOutputRates;
    private boolean _initialized;

    public TileEntityEnergyBridge() {
        this._energyStoredMax = (double) ConfigurationHandler.bridgeBufferSize;
        this._producerTiles = new HashMap();
        this.clientSideData = new HashMap();
        this._producerOutputRates = new HashMap();
        ForgeDirection[] arr$ = ForgeDirection.VALID_DIRECTIONS;
        int len$ = arr$.length;

        for (int i$ = 0; i$ < len$; ++i$) {
            ForgeDirection d = arr$[i$];
            this.clientSideData.put(d, new BridgeSideData());
            this._producerOutputRates.put(d, 0.0);
        }
    }

    public double getEnergyStored() {
        return this._energyStored;
    }

    public double getEnergyStoredMax() {
        return this._energyStoredMax;
    }

    public double storeEnergy(double energy, boolean simulate) {
        double toStore = Math.min(energy, this._energyStoredMax - this._energyStored);
        if (simulate) {
            return energy - toStore;
        } else {
            this._energyStored += toStore;
            return energy - toStore;
        }
    }

    public void updateEntity() {
        super.updateEntity();
        if (!this._initialized) {
            this.onNeighboorChanged();
            this._initialized = true;
        }

        if (!super.worldObj.isRemote) {
            double energyRemaining = Math.min(this._energyStored, this._energyStoredMax);
            double energyNotProduced = 0.0;
            Iterator i$ = this._producerTiles.entrySet().iterator();

            while (i$.hasNext()) {
                Map.Entry prod = (Map.Entry) i$.next();
                if (!((TileEntityEnergyProducer) prod.getValue()).isGettingRedstone()) {
                    if (energyRemaining > 0.0) {
                        energyNotProduced = ((TileEntityEnergyProducer) prod.getValue())
                                                .produceEnergy(energyRemaining);
                        if (energyNotProduced > energyRemaining) {
                            energyNotProduced = energyRemaining;
                        }

                        this._producerOutputRates.put(
                            prod.getKey(),
                            (energyRemaining - energyNotProduced)
                                / (double) ((TileEntityEnergyProducer) prod.getValue())
                                      .getPowerSystem()
                                      .getScaleAmmount()
                        );
                        energyRemaining = energyNotProduced;
                    } else {
                        ((TileEntityEnergyProducer) prod.getValue()).produceEnergy(0.0);
                        this._producerOutputRates.put(prod.getKey(), 0.0);
                    }
                }
            }

            this._energyStored = Math.max(0.0, energyRemaining);
            if ((this._energyStored != this._energyStoredLast
                 || this._energyStored != this._energyStoredMax)
                && !(this._energyStored > this._energyStoredLast)) {
                this._isInputLimited = true;
            } else {
                this._isInputLimited = false;
            }

            this._energyStoredLast = this._energyStored;
        }
    }

    public void onNeighboorChanged() {
        Map producerTiles = new HashMap();
        ForgeDirection[] arr$ = ForgeDirection.VALID_DIRECTIONS;
        int len$ = arr$.length;

        for (int i$ = 0; i$ < len$; ++i$) {
            ForgeDirection d = arr$[i$];
            BlockPosition p = new BlockPosition(this);
            p.orientation = d;
            p.moveForwards(1);
            TileEntity te = super.worldObj.getTileEntity(p.x, p.y, p.z);
            if (te != null && te instanceof TileEntityEnergyProducer) {
                producerTiles.put(d, (TileEntityEnergyProducer) te);
            }
        }

        this._producerTiles = producerTiles;
    }

    public BridgeSideData getDataForSide(ForgeDirection dir) {
        if (!super.worldObj.isRemote) {
            BridgeSideData d = new BridgeSideData();
            BlockPosition p = new BlockPosition(this);
            p.orientation = dir;
            p.moveForwards(1);
            TileEntity te = super.worldObj.getTileEntity(p.x, p.y, p.z);
            if (te != null && te instanceof TileEntityBridgeComponent) {
                if (te instanceof TileEntityEnergyConsumer) {
                    d.isConsumer = true;
                    d.outputRate = ((TileEntityEnergyConsumer) te).getInputRate();
                }

                if (te instanceof TileEntityEnergyProducer) {
                    d.isProducer = true;
                    d.outputRate = (Double) this._producerOutputRates.get(dir);
                }

                TileEntityBridgeComponent c = (TileEntityBridgeComponent) te;
                d.powerSystem = c.getPowerSystem();
                d.isConnected = c.isConnected();
                d.side = dir;
                d.voltageNameIndex = c.getVoltageIndex();
            }

            return d;
        } else {
            return (BridgeSideData) this.clientSideData.get(dir);
        }
    }

    public BridgeSideData[] getClientData() {
        BridgeSideData[] data = new BridgeSideData[6];
        ForgeDirection[] arr$ = ForgeDirection.VALID_DIRECTIONS;
        int len$ = arr$.length;

        for (int i$ = 0; i$ < len$; ++i$) {
            ForgeDirection dir = arr$[i$];
            data[dir.ordinal()] = (BridgeSideData) this.clientSideData.get(dir);
        }

        return data;
    }

    public void setClientDataForSide(ForgeDirection dir, BridgeSideData data) {
        if (this.clientSideData.containsKey(dir)) {
            this.clientSideData.remove(dir);
        }

        this.clientSideData.put(dir, data);
    }

    public boolean isInputLimited() {
        return this._isInputLimited;
    }

    @SideOnly(Side.CLIENT)
    public void setIsInputLimited(boolean isInputLimited) {
        this._isInputLimited = isInputLimited;
    }

    public double getEnergyScaled() {
        return super.worldObj.isRemote
            ? this._energyScaledClient
            : 120.0 * (this._energyStored / this._energyStoredMax);
    }

    public void setEnergyScaled(double scaled) {
        this._energyScaledClient = scaled;
    }

    public void addWailaInfo(List info) {}

    public void writeToNBT(NBTTagCompound par1nbtTagCompound) {
        super.writeToNBT(par1nbtTagCompound);
        par1nbtTagCompound.setDouble("energyStored", this._energyStored);
    }

    public void readFromNBT(NBTTagCompound par1nbtTagCompound) {
        super.readFromNBT(par1nbtTagCompound);
        this._energyStored = par1nbtTagCompound.getDouble("energyStored");
    }

    public EnergyBridgeSyncPacket getNetPacket() {
        BridgeSideData[] bridgeSideData = new BridgeSideData[6];
        ForgeDirection[] arr$ = ForgeDirection.VALID_DIRECTIONS;
        int side = arr$.length;

        for (int i$ = 0; i$ < side; ++i$) {
            ForgeDirection dir = arr$[i$];
            bridgeSideData[dir.ordinal()] = this.getDataForSide(dir);
        }

        NBTTagCompound tagCompound = new NBTTagCompound();

        for (side = 0; side < 6; ++side) {
            BridgeSideData data = bridgeSideData[side];
            NBTTagCompound tag = new NBTTagCompound();
            data.writeToNBT(tag);
            tagCompound.setTag(String.valueOf(side), tag);
        }

        tagCompound.setBoolean("InputLimited", this.isInputLimited());
        tagCompound.setDouble("Energy", this.getEnergyScaled());
        EnergyBridgeSyncPacket syncPacket = new EnergyBridgeSyncPacket(
            tagCompound, super.xCoord, super.yCoord, super.zCoord
        );
        return syncPacket;
    }
}
