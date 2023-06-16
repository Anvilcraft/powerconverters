package covers1624.powerconverters.util;

public class RFHelper {
    public static boolean iEnergyHandlerExists = false;
    public static boolean iEnergyContainerItemExists = false;

    public static void init() {
        try {
            Class.forName("cofh.api.energy.IEnergyHandler");
            iEnergyHandlerExists = true;
            LogHelper.trace("IEnergyHandler Exists!");
        } catch (ClassNotFoundException var2) {
            LogHelper.trace("Failed To Find IEnergy Handler, Not Enableing RF Support.");
        }

        try {
            Class.forName("cofh.api.energy.IEnergyContainerItem");
            iEnergyContainerItemExists = true;
        } catch (ClassNotFoundException var1) {
            LogHelper.trace(
                "Failed to find IEnergyContainerItem, Not registering Chargehandler for RF."
            );
        }
    }
}
