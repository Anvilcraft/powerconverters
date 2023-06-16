package covers1624.powerconverters.tile.factorization;

import java.util.Iterator;
import java.util.Map;

import covers1624.powerconverters.handler.ConfigurationHandler;
import covers1624.powerconverters.init.PowerSystems;
import covers1624.powerconverters.tile.main.TileEntityEnergyProducer;
import factorization.api.Charge;
import factorization.api.Coord;
import factorization.api.IChargeConductor;

public class TileEntityPowerConverterFactorizationProducer
    extends TileEntityEnergyProducer implements IChargeConductor {
    private Charge _charge = new Charge(this);
    private static final double _maxCG = 2000.0;

    public TileEntityPowerConverterFactorizationProducer() {
        super(PowerSystems.powerSystemFactorization, 0, IChargeConductor.class);
    }

    public double produceEnergy(double energy) {
        if (ConfigurationHandler.dissableFactorizationProducer) {
            return energy;
        } else {
            double CG = energy
                / (double) PowerSystems.powerSystemFactorization.getScaleAmmount();
            Iterator i$ = this.getTiles().entrySet().iterator();

            while (i$.hasNext()) {
                Map.Entry output = (Map.Entry) i$.next();
                IChargeConductor o = (IChargeConductor) output.getValue();
                if (o != null && (double) o.getCharge().getValue() < 2000.0) {
                    double store
                        = Math.min(2000.0 - (double) o.getCharge().getValue(), CG);
                    o.getCharge().addValue((int) store);
                    CG -= store;
                    if (CG <= 0.0) {
                        break;
                    }
                }
            }

            return CG * (double) PowerSystems.powerSystemFactorization.getScaleAmmount();
        }
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
