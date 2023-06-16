package covers1624.powerconverters.handler;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class ConfigurationHandler {
    public Configuration configuration;
    public static ConfigurationHandler INSTANCE;
    public static boolean logDebug;
    public static int bridgeBufferSize;
    public static boolean useThermalExpansionRecipes;
    public static boolean useTechRebornRecipes;
    public static boolean doFlatBedrock;
    public static boolean doUpdateCheck;
    public static boolean sendRecipesToClient;
    public static boolean ignoreRecipesFromServer;
    public static boolean dissableRFProducer;
    public static boolean dissableRFConsumer;
    public static boolean dissableIC2Producer;
    public static boolean dissableIC2Consumer;
    public static boolean dissableFactorizationProducer;
    public static boolean dissableFactorizationConsumer;
    public static boolean dissableSteamProducer;
    public static boolean dissableSteamConsumer;
    public static boolean dissableUniversalCharger;
    public static boolean dissableUniversalUnCharger;

    public ConfigurationHandler(File config) {
        INSTANCE = this;
        if (this.configuration == null) {
            this.configuration = new Configuration(config);
        }

        this.loadConfiguration();
    }

    public void loadConfiguration() {
        bridgeBufferSize
            = this.configuration.get("basic", "BridgeBufferSize", 160000000).getInt();
        logDebug = this.configuration
                       .get(
                           "basic",
                           "Log Debug Messages",
                           false,
                           "Set this to true to see all debug messages."
                       )
                       .getBoolean();
        doFlatBedrock = this.configuration
                            .get(
                                "basic",
                                "Do Flat Bedrock",
                                false,
                                "Set this to false for normal Bedrock."
                            )
                            .getBoolean();
        doUpdateCheck
            = this.configuration
                  .get(
                      "basic",
                      "Do Update Check",
                      true,
                      "Set to false and PowerConverters will not check for an update"
                  )
                  .getBoolean();
        sendRecipesToClient
            = this.configuration
                  .get(
                      "basic",
                      "Send Recipes To Client",
                      true,
                      "Setting this to false will dissable recipe syncing with the server"
                  )
                  .getBoolean();
        ignoreRecipesFromServer
            = this.configuration
                  .get(
                      "basic",
                      "Ignore Server Recipes",
                      false,
                      "Setting this to true will dissable the client using the server recipes."
                  )
                  .getBoolean();
        useThermalExpansionRecipes
            = this.configuration
                  .get("Recipes", "Thermal Expansion", false, "Thermal Expansion Recipes")
                  .getBoolean();
        useTechRebornRecipes
            = this.configuration
                  .get("Recipes", "Tech Reborn", false, "Tech Reborn Recipes")
                  .getBoolean();
        dissableRFProducer = this.configuration.getBoolean(
            "Dissable RF Producer", "Devices", false, "Dissables the RF Producer"
        );
        dissableRFConsumer = this.configuration.getBoolean(
            "Dissable RF Consumer", "Devices", false, "Dissables the RF Consumer"
        );
        dissableIC2Producer = this.configuration.getBoolean(
            "Dissable IC2 Producers", "Devices", false, "Dissables the IC2 Producers"
        );
        dissableIC2Consumer = this.configuration.getBoolean(
            "Dissable IC2 Consumers", "Devices", false, "Dissables the IC2 Consumers"
        );
        dissableFactorizationProducer = this.configuration.getBoolean(
            "Dissable Factorization Producer",
            "Devices",
            false,
            "Dissables the Factorization Producer"
        );
        dissableFactorizationConsumer = this.configuration.getBoolean(
            "Dissable Factorization Consumer",
            "Devices",
            false,
            "Dissables the Factorization Consumer"
        );
        dissableSteamProducer = this.configuration.getBoolean(
            "Dissable Steam Producer", "Devices", false, "Dissables the Steam Producer"
        );
        dissableSteamConsumer = this.configuration.getBoolean(
            "Dissable Steam Consumer", "Devices", false, "Dissables the Steam Consumer"
        );
        dissableUniversalCharger = this.configuration.getBoolean(
            "Dissable Universal Charger",
            "Devices",
            false,
            "Dissables the Universal Charger"
        );
        dissableUniversalUnCharger = this.configuration.getBoolean(
            "Dissable Universal Un Charger",
            "Devices",
            false,
            "Dissables the Universal Un Charger"
        );
        this.configuration.save();
    }
}
