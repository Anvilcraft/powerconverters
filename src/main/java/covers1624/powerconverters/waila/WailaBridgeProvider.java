package covers1624.powerconverters.waila;

import java.util.List;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class WailaBridgeProvider implements IWailaDataProvider {
    public ItemStack
    getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return null;
    }

    public List getWailaHead(
        ItemStack itemStack,
        List currenttip,
        IWailaDataAccessor accessor,
        IWailaConfigHandler config
    ) {
        return currenttip;
    }

    public List getWailaBody(
        ItemStack itemStack,
        List currenttip,
        IWailaDataAccessor accessor,
        IWailaConfigHandler config
    ) {
        return currenttip;
    }

    public List getWailaTail(
        ItemStack itemStack,
        List currenttip,
        IWailaDataAccessor accessor,
        IWailaConfigHandler config
    ) {
        return currenttip;
    }

    public NBTTagCompound getNBTData(
        EntityPlayerMP player,
        TileEntity te,
        NBTTagCompound tag,
        World world,
        int x,
        int y,
        int z
    ) {
        return tag;
    }
}
