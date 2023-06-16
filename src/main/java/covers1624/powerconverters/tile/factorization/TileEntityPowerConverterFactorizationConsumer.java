package covers1624.powerconverters.tile.factorization;

import covers1624.powerconverters.handler.ConfigurationHandler;
import covers1624.powerconverters.init.PowerSystems;
import covers1624.powerconverters.tile.main.TileEntityEnergyConsumer;
import factorization.api.Charge;
import factorization.api.Coord;
import factorization.api.IChargeConductor;
import net.minecraft.util.MathHelper;

public class TileEntityPowerConverterFactorizationConsumer
    extends TileEntityEnergyConsumer implements IChargeConductor {
    private Charge _charge = new Charge(this);
    private double _chargeLastTick = 0.0;
    private static final int _maxCG = 2000;

    public TileEntityPowerConverterFactorizationConsumer() {
        super(PowerSystems.powerSystemFactorization, 0, IChargeConductor.class);
    }

    public void updateEntity() {
        super.updateEntity();
        if (!super.worldObj.isRemote) {
            if (!ConfigurationHandler.dissableFactorizationConsumer) {
                if (this._charge.getValue() < 2000) {
                    this._charge.update();
                }

                if (this._charge.getValue() > 0) {
                    double used = (double) this._charge.tryTake(this._charge.getValue());
                    this._chargeLastTick = (double) MathHelper.floor_double(used);
                    this.storeEnergy(
                        (double) ((int
                        ) (used
                           * (double
                           ) PowerSystems.powerSystemFactorization.getScaleAmmount())),
                        false
                    );
                } else {
                    this._chargeLastTick = 0.0;
                }
            }
        }
    }

    public double getInputRate() {
        return this._chargeLastTick;
    }

    public Charge getCharge() {
        return this._charge;
    }

    public String getInfo() {
        return null;
    }

    public Coord getCoord() {
        return new Coord(this);
    }
}
