package covers1624.powerconverters.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class ProxyServer implements IPCProxy {
    public void initRendering() {}

    public EntityPlayer getClientPlayer() {
        return null;
    }

    public World getClientWorld() {
        return null;
    }
}
