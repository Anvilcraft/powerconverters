package covers1624.powerconverters.tile.main;

import covers1624.powerconverters.api.registry.PowerSystemRegistry;

public abstract class TileEntityEnergyProducer extends TileEntityBridgeComponent {
    public TileEntityEnergyProducer(
        PowerSystemRegistry.PowerSystem powerSystem,
        int voltageNameIndex,
        Class adjacentClass
    ) {
        super(powerSystem, voltageNameIndex, adjacentClass);
        super.type = "Producer";
    }

    public abstract double produceEnergy(double var1);
}
