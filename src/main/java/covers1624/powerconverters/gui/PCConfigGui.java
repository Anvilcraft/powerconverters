package covers1624.powerconverters.gui;

import covers1624.powerconverters.handler.ConfigurationHandler;
import cpw.mods.fml.client.config.GuiConfig;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;

public class PCConfigGui extends GuiConfig {
    public PCConfigGui(GuiScreen parent) {
        super(
            parent,
            (new ConfigElement(
                 ConfigurationHandler.INSTANCE.configuration.getCategory("basic")
             ))
                .getChildElements(),
            "PowerConverters3",
            false,
            true,
            GuiConfig.getAbridgedConfigPath(
                ConfigurationHandler.INSTANCE.configuration.toString()
            )
        );
    }
}
