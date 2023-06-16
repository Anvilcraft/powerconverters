package covers1624.powerconverters.tile.ic2;

import covers1624.powerconverters.handler.ConfigurationHandler;
import covers1624.powerconverters.init.PowerSystems;
import covers1624.powerconverters.tile.main.TileEntityEnergyProducer;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergyAcceptor;
import ic2.api.energy.tile.IEnergySource;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityIndustrialCraftProducer
    extends TileEntityEnergyProducer implements IEnergySource {
    private boolean _isAddedToEnergyNet;
    private boolean _didFirstAddToNet;
    private double eu;
    private int _packetCount;

    public TileEntityIndustrialCraftProducer() {
        this(0);
    }

    public TileEntityIndustrialCraftProducer(int voltageIndex) {
        super(
            PowerSystems.powerSystemIndustrialCraft, voltageIndex, IEnergyAcceptor.class
        );
        this._packetCount = 1;
    }

    public void updateEntity() {
        if (!this._didFirstAddToNet && !super.worldObj.isRemote) {
            MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
            this._didFirstAddToNet = true;
            this._isAddedToEnergyNet = true;
        }

        super.updateEntity();
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

    public double produceEnergy(double energy) {
        if (ConfigurationHandler.dissableIC2Producer) {
            return energy;
        } else {
            double eu = energy
                / (double) PowerSystems.powerSystemIndustrialCraft.getScaleAmmount();
            double usedEu = Math.min(eu, this.getMaxEnergyOutput() - this.eu);
            this.eu += usedEu;
            return (eu - usedEu)
                * (double) PowerSystems.powerSystemIndustrialCraft.getScaleAmmount();
        }
    }

    public double getMaxEnergyOutput() {
        return (double) this.getPowerSystem().getVoltageValues()[this.getVoltageIndex()];
    }

    public boolean emitsEnergyTo(TileEntity receiver, ForgeDirection direction) {
        return true;
    }

    public double getOfferedEnergy() {
        return Math.min(this.eu, this.getMaxEnergyOutput());
    }

    public void drawEnergy(double amount) {
        this.eu -= (double) MathHelper.ceiling_double_int(amount);
    }

    public int getSourceTier() {
        return this.getVoltageIndex();
    }
}
