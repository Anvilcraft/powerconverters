package covers1624.powerconverters.tile.ic2;

import covers1624.powerconverters.handler.ConfigurationHandler;
import covers1624.powerconverters.init.PowerSystems;
import covers1624.powerconverters.tile.main.TileEntityEnergyConsumer;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityIndustrialCraftConsumer
    extends TileEntityEnergyConsumer implements IEnergySink {
    private boolean _isAddedToEnergyNet;
    private boolean _didFirstAddToNet;
    private double _euLastTick;
    private long _lastTickInjected;

    public TileEntityIndustrialCraftConsumer() {
        this(0);
    }

    public TileEntityIndustrialCraftConsumer(int voltageIndex) {
        super(
            PowerSystems.powerSystemIndustrialCraft, voltageIndex, IEnergyEmitter.class
        );
    }

    public void updateEntity() {
        super.updateEntity();
        if (!this._didFirstAddToNet && !super.worldObj.isRemote) {
            MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
            this._didFirstAddToNet = true;
            this._isAddedToEnergyNet = true;
        }

        if (super.worldObj.getWorldTime() - this._lastTickInjected > 2L) {
            this._euLastTick = 0.0;
        }
    }

    public void validate() {
        super.validate();
        if (!this._isAddedToEnergyNet) {
            this._didFirstAddToNet = false;
        }
    }

    public void invalidate() {
        if (this._isAddedToEnergyNet) {
            if (!super.worldObj.isRemote) {
                MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
            }

            this._isAddedToEnergyNet = false;
        }

        super.invalidate();
    }

    public boolean acceptsEnergyFrom(TileEntity emitter, ForgeDirection direction) {
        return !ConfigurationHandler.dissableIC2Consumer;
    }

    public double getDemandedEnergy() {
        return this.getTotalEnergyDemand()
            / (double) PowerSystems.powerSystemIndustrialCraft.getScaleAmmount();
    }

    public double
    injectEnergy(ForgeDirection directionFrom, double realAmount, double voltage) {
        double amount = (double) ((int) Math.floor(realAmount));
        if (amount > (double) this.getSinkTier()) {
            Block block
                = super.worldObj.getBlock(super.xCoord, super.yCoord, super.zCoord);
            int meta = super.worldObj.getBlockMetadata(
                super.xCoord, super.yCoord, super.zCoord
            );
            super.worldObj.setBlockToAir(super.xCoord, super.yCoord, super.zCoord);
            block.dropBlockAsItem(
                super.worldObj, super.xCoord, super.yCoord, super.zCoord, meta, 0
            );
            return amount;
        } else {
            double pcuNotStored = this.storeEnergy(
                amount
                    * (double) PowerSystems.powerSystemIndustrialCraft.getScaleAmmount(),
                false
            );
            double euNotStored = pcuNotStored
                / (double) PowerSystems.powerSystemIndustrialCraft.getScaleAmmount();
            double euThisInjection = amount - euNotStored;
            if (this._lastTickInjected == super.worldObj.getWorldTime()) {
                this._euLastTick += euThisInjection;
            } else {
                this._euLastTick = euThisInjection;
                this._lastTickInjected = super.worldObj.getWorldTime();
            }

            return euNotStored;
        }
    }

    public int getSinkTier() {
        return this.getVoltageIndex() == 3
            ? Integer.MAX_VALUE
            : this.getPowerSystem().getVoltageValues()[this.getVoltageIndex()];
    }

    public double getInputRate() {
        return this._euLastTick;
    }
}
