package covers1624.powerconverters.proxy;

import covers1624.powerconverters.client.render.TileUniversalConduitRender;
import covers1624.powerconverters.tile.main.TileEnergyConduit;
import cpw.mods.fml.client.registry.ClientRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class ProxyClient implements IPCProxy {
    public void initRendering() {
        ClientRegistry.bindTileEntitySpecialRenderer(
            TileEnergyConduit.class, new TileUniversalConduitRender()
        );
    }

    public EntityPlayer getClientPlayer() {
        return Minecraft.getMinecraft().thePlayer;
    }

    public World getClientWorld() {
        return Minecraft.getMinecraft().theWorld;
    }
}
