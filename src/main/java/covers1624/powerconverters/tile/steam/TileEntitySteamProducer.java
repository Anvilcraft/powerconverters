package covers1624.powerconverters.tile.steam;

import covers1624.powerconverters.PowerConverters;
import covers1624.powerconverters.handler.ConfigurationHandler;
import covers1624.powerconverters.init.PowerSystems;
import covers1624.powerconverters.tile.main.TileEntityEnergyProducer;
import covers1624.powerconverters.util.BlockPosition;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TileEntitySteamProducer
    extends TileEntityEnergyProducer implements IFluidHandler {
    private FluidTank _tank = new FluidTank(10000);

    public TileEntitySteamProducer() {
        super(PowerSystems.powerSystemSteam, 0, IFluidHandler.class);
    }

    public double produceEnergy(double energy) {
        if (PowerConverters.steamId != -1
            && !ConfigurationHandler.dissableSteamProducer) {
            energy /= (double) PowerSystems.powerSystemSteam.getScaleAmmount();

            for (int i = 0; i < 6; ++i) {
                BlockPosition bp = new BlockPosition(this);
                bp.orientation = ForgeDirection.getOrientation(i);
                bp.moveForwards(1);
                TileEntity te = super.worldObj.getTileEntity(bp.x, bp.y, bp.z);
                if (te != null && te instanceof IFluidHandler) {
                    energy -= (double) ((IFluidHandler) te)
                                  .fill(
                                      bp.orientation.getOpposite(),
                                      new FluidStack(
                                          FluidRegistry.getFluid(PowerConverters.steamId),
                                          (int) energy
                                      ),
                                      true
                                  );
                }

                if (energy <= 0.0) {
                    return 0.0;
                }
            }

            return energy * (double) PowerSystems.powerSystemSteam.getScaleAmmount();
        } else {
            return energy;
        }
    }

    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        return 0;
    }

    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        return null;
    }

    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return false;
    }

    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        return null;
    }

    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return true;
    }

    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        return new FluidTankInfo[] { this._tank.getInfo() };
    }
}
