package covers1624.powerconverters.gui;

import java.util.Set;

import cpw.mods.fml.client.IModGuiFactory;
import net.minecraft.client.Minecraft;

public class PCGuiFactory implements IModGuiFactory {
    public void initialize(Minecraft minecraftInstance) {}

    public Class mainConfigGuiClass() {
        return PCConfigGui.class;
    }

    public Set runtimeGuiCategories() {
        return null;
    }

    public IModGuiFactory.RuntimeOptionGuiHandler
    getHandlerFor(IModGuiFactory.RuntimeOptionCategoryElement element) {
        return null;
    }
}
