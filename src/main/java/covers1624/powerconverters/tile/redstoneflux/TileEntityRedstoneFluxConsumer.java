package covers1624.powerconverters.tile.redstoneflux;

import cofh.api.energy.IEnergyReceiver;
import covers1624.powerconverters.handler.ConfigurationHandler;
import covers1624.powerconverters.init.PowerSystems;
import covers1624.powerconverters.tile.main.TileEntityEnergyBridge;
import covers1624.powerconverters.tile.main.TileEntityEnergyConsumer;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityRedstoneFluxConsumer
    extends TileEntityEnergyConsumer implements IEnergyReceiver {
    private double lastReceivedRF;

    public TileEntityRedstoneFluxConsumer() {
        super(PowerSystems.powerSystemRedstoneFlux, 0, IEnergyReceiver.class);
    }

    public void updateEntity() {
        super.updateEntity();
    }

    public double getInputRate() {
        double last = this.lastReceivedRF;
        this.lastReceivedRF = 0.0;
        return last;
    }

    public boolean canConnectEnergy(ForgeDirection arg0) {
        return !ConfigurationHandler.dissableRFConsumer;
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

    public int receiveEnergy(ForgeDirection from, int receiveMax, boolean simulate) {
        if (this.getFirstBridge() == null) {
            return 0;
        } else {
            int actualRF = this.getPowerSystem().getScaleAmmount() * receiveMax;
            int rfNotStored = (int
            ) ((double) actualRF - this.storeEnergy((double) actualRF, simulate));
            return rfNotStored / this.getPowerSystem().getScaleAmmount();
        }
    }
}
