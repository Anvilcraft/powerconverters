package covers1624.powerconverters.tile.redstoneflux;

import java.util.Iterator;
import java.util.List;

import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyProvider;
import covers1624.powerconverters.handler.ConfigurationHandler;
import covers1624.powerconverters.init.PowerSystems;
import covers1624.powerconverters.tile.main.TileEntityEnergyBridge;
import covers1624.powerconverters.tile.main.TileEntityEnergyProducer;
import covers1624.powerconverters.util.BlockPosition;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityRedstoneFluxProducer
    extends TileEntityEnergyProducer implements IEnergyProvider {
    public TileEntityRedstoneFluxProducer() {
        super(PowerSystems.powerSystemRedstoneFlux, 0, IEnergyProvider.class);
    }

    public boolean canConnectEnergy(ForgeDirection arg0) {
        return true;
    }

    public int extractEnergy(ForgeDirection arg0, int arg1, boolean arg2) {
        return 0;
    }

    public int getEnergyStored(ForgeDirection arg0) {
        TileEntityEnergyBridge bridge = this.getFirstBridge();
        return bridge == null ? 0
                              : (int
                              ) (bridge.getEnergyStored()
                                 / (double) this.getPowerSystem().getScaleAmmount());
    }

    public int getMaxEnergyStored(ForgeDirection arg0) {
        TileEntityEnergyBridge bridge = this.getFirstBridge();
        return bridge == null ? 0
                              : (int
                              ) (bridge.getEnergyStoredMax()
                                 / (double) this.getPowerSystem().getScaleAmmount());
    }

    public double produceEnergy(double energy) {
        if (ConfigurationHandler.dissableRFProducer) {
            return energy;
        } else {
            double toUseRF = energy / (double) this.getPowerSystem().getScaleAmmount();
            if (toUseRF > 0.0) {
                List pos = (new BlockPosition(super.xCoord, super.yCoord, super.zCoord))
                               .getAdjacent(true);
                Iterator i$ = pos.iterator();

                while (i$.hasNext()) {
                    BlockPosition p = (BlockPosition) i$.next();
                    TileEntity te = super.worldObj.getTileEntity(p.x, p.y, p.z);
                    if (te instanceof IEnergyHandler
                        && !(te instanceof TileEntityRedstoneFluxConsumer)
                        && !(te instanceof TileEntityEnergyBridge)) {
                        IEnergyHandler handler = (IEnergyHandler) te;
                        double RF = (double) handler.receiveEnergy(
                            p.orientation.getOpposite(), (int) toUseRF, false
                        );
                        energy -= RF * (double) this.getPowerSystem().getScaleAmmount();
                        if (energy <= 0.0) {
                            break;
                        }
                    }
                }
            }

            return energy;
        }
    }
}
