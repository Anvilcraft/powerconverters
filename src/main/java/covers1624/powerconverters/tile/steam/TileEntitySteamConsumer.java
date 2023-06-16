package covers1624.powerconverters.tile.steam;

import covers1624.powerconverters.PowerConverters;
import covers1624.powerconverters.handler.ConfigurationHandler;
import covers1624.powerconverters.init.PowerSystems;
import covers1624.powerconverters.tile.main.TileEntityEnergyConsumer;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TileEntitySteamConsumer
    extends TileEntityEnergyConsumer implements IFluidHandler {
    private FluidTank _steamTank = new FluidTank(10000);
    private int _mBLastTick;

    public TileEntitySteamConsumer() {
        super(PowerSystems.powerSystemSteam, 0, IFluidHandler.class);
    }

    public void updateEntity() {
        super.updateEntity();
        if (this._steamTank != null && this._steamTank.getFluid() != null) {
            int amount = this._steamTank.getFluid().amount;
            double energy
                = (double) (amount * PowerSystems.powerSystemSteam.getScaleAmmount());
            energy = this.storeEnergy(energy, false);
            int toDrain = amount
                - (int) (energy / (double) PowerSystems.powerSystemSteam.getScaleAmmount()
                );
            this._steamTank.drain(toDrain, true);
            this._mBLastTick = toDrain;
        } else {
            this._mBLastTick = 0;
        }
    }

    public int getVoltageIndex() {
        return 0;
    }

    public double getInputRate() {
        return (double) this._mBLastTick;
    }

    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        return resource != null && resource.getFluidID() == PowerConverters.steamId
                && PowerConverters.steamId != -1
            ? this._steamTank.fill(resource, doFill)
            : 0;
    }

    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        return null;
    }

    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        return null;
    }

    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return fluid != null && fluid.getID() == PowerConverters.steamId
            && !ConfigurationHandler.dissableSteamConsumer;
    }

    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return false;
    }

    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        return new FluidTankInfo[] { this._steamTank.getInfo() };
    }
}
