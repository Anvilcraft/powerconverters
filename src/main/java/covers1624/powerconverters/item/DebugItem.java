package covers1624.powerconverters.item;

import java.util.ArrayList;

import covers1624.powerconverters.tile.main.TileEntityBridgeComponent;
import covers1624.powerconverters.tile.main.TileEntityEnergyBridge;
import covers1624.powerconverters.util.IAdvancedLogTile;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class DebugItem extends Item {
    public DebugItem() {
        this.setUnlocalizedName("pcdebugitem");
        this.setMaxStackSize(1);
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister ir) {
        super.itemIcon = ir.registerIcon("powerconverters:" + this.getUnlocalizedName());
    }

    public boolean onItemUse(
        ItemStack stack,
        EntityPlayer player,
        World world,
        int x,
        int y,
        int z,
        int side,
        float hitx,
        float hity,
        float hitz
    ) {
        boolean success = false;
        TileEntity te = world.getTileEntity(x, y, z);
        TileEntityEnergyBridge teb = null;
        if (world.isRemote) {
            player.addChatMessage(new ChatComponentText("-Client-"));
            return false;
        } else {
            player.addChatMessage(new ChatComponentText("-Server-"));
            if (te instanceof TileEntityEnergyBridge
                || te instanceof TileEntityBridgeComponent) {
                if (te instanceof TileEntityBridgeComponent) {
                    teb = ((TileEntityBridgeComponent) te).getFirstBridge();
                } else {
                    teb = (TileEntityEnergyBridge) te;
                }

                double energyamount
                    = 100.0 * (teb.getEnergyStored() / teb.getEnergyStoredMax());
                String energy = String.valueOf((int) energyamount);
                player.addChatMessage(
                    new ChatComponentText("Energy Bridge Is " + energy + "% Full")
                );
                success = true;
            }

            if (te instanceof IAdvancedLogTile) {
                ArrayList info = new ArrayList();
                ((IAdvancedLogTile) te)
                    .getTileInfo(
                        info,
                        ForgeDirection.VALID_DIRECTIONS[side],
                        player,
                        player.isSneaking()
                    );

                for (int i = 0; i < info.size(); ++i) {
                    player.addChatMessage((IChatComponent) info.get(i));
                }

                success = true;
            } else if (world.getBlock(x, y, z) != null) {
                player.addChatMessage(
                    new ChatComponentText(String.valueOf(world.getBlockMetadata(x, y, z)))
                );
            }

            return success;
        }
    }
}
