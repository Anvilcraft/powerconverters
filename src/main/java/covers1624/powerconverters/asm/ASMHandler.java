package covers1624.powerconverters.asm;

import java.util.Map;

import covers1624.powerconverters.util.LogHelper;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import net.minecraft.launchwrapper.Launch;

@MCVersion("1.7.10")
public class ASMHandler implements IFMLLoadingPlugin {
    public String[] getASMTransformerClass() {
        return null;
    }

    public String getModContainerClass() {
        return null;
    }

    public String getSetupClass() {
        return null;
    }

    public void injectData(Map data) {}

    public String getAccessTransformerClass() {
        return null;
    }

    static {
        LogHelper.info("Adding PowerConverters to Transformer Exclusions list.");
        Launch.classLoader.addTransformerExclusion("covers1624.powerconverters.");
    }
}
