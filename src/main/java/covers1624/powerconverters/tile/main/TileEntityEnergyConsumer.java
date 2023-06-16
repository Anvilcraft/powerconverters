package covers1624.powerconverters.tile.main;

import java.util.Iterator;
import java.util.Map;

import covers1624.powerconverters.api.registry.PowerSystemRegistry;

public abstract class TileEntityEnergyConsumer extends TileEntityBridgeComponent {
    public TileEntityEnergyConsumer(
        PowerSystemRegistry.PowerSystem powerSystem,
        int voltageNameIndex,
        Class adjacentClass
    ) {
        super(powerSystem, voltageNameIndex, adjacentClass);
        super.type = "Consumer";
    }

    protected double storeEnergy(double energy, boolean simulate) {
        Iterator i$ = this.getBridges().entrySet().iterator();

        do {
            if (!i$.hasNext()) {
                return energy;
            }

            Map.Entry bridge = (Map.Entry) i$.next();
            if (!this.isGettingRedstone()) {
                energy = ((TileEntityEnergyBridge) bridge.getValue())
                             .storeEnergy(energy, simulate);
            }
        } while (!(energy <= 0.0));

        return 0.0;
    }

    protected double getTotalEnergyDemand() {
        double demand = 0.0;

        Map.Entry bridge;
        for (Iterator i$ = this.getBridges().entrySet().iterator(); i$.hasNext();
             demand += ((TileEntityEnergyBridge) bridge.getValue()).getEnergyStoredMax()
                 - ((TileEntityEnergyBridge) bridge.getValue()).getEnergyStored()) {
            bridge = (Map.Entry) i$.next();
        }

        return demand;
    }

    public abstract double getInputRate();
}
