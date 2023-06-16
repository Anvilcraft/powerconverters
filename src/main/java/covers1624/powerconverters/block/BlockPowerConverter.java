package covers1624.powerconverters.block;

import java.util.Random;

import covers1624.powerconverters.PowerConverters;
import covers1624.powerconverters.item.DebugItem;
import covers1624.powerconverters.net.PacketPipeline;
import covers1624.powerconverters.tile.main.TileEntityBridgeComponent;
import covers1624.powerconverters.tile.main.TileEntityEnergyBridge;
import covers1624.powerconverters.util.INeighboorUpdateTile;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPowerConverter extends BlockContainer {
    protected IIcon[] _icons;
    protected boolean isGettingRedstone;

    public BlockPowerConverter(int metaCount) {
        super(Material.iron);
        this.setHardness(1.0F);
        this._icons = new IIcon[metaCount * 2];
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        int offset = ((TileEntityBridgeComponent) world.getTileEntity(x, y, z))
                         .isSideConnectedClient(side)
            ? 1
            : 0;
        return this._icons[world.getBlockMetadata(x, y, z) * 2 + offset];
    }

    public IIcon getIcon(int side, int metadata) {
        return this._icons[metadata * 2];
    }

    public TileEntity createNewTileEntity(World world, int par1) {
        return null;
    }

    public int damageDropped(int par1) {
        return par1;
    }

    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        this.checkRedstone(world, x, y, z);
        TileEntity te = world.getTileEntity(x, y, z);
        if (te != null && te instanceof INeighboorUpdateTile) {
            ((INeighboorUpdateTile) te).onNeighboorChanged();
            world.markBlockForUpdate(x, y, z);
        }
    }

    public boolean onBlockActivated(
        World world,
        int x,
        int y,
        int z,
        EntityPlayer player,
        int par6,
        float par7,
        float par8,
        float par9
    ) {
        try {
            if (player.getHeldItem().getItem() instanceof DebugItem) {
                return false;
            }
        } catch (Exception var12) {}

        TileEntity te = world.getTileEntity(x, y, z);
        if (te != null && te instanceof TileEntityBridgeComponent) {
            TileEntityEnergyBridge bridge
                = ((TileEntityBridgeComponent) te).getFirstBridge();
            if (bridge != null) {
                if (!world.isRemote) {
                    PacketPipeline.INSTANCE.sendTo(
                        bridge.getNetPacket(), (EntityPlayerMP) player
                    );
                }

                player.openGui(
                    PowerConverters.instance,
                    0,
                    world,
                    bridge.xCoord,
                    bridge.yCoord,
                    bridge.zCoord
                );
            }
        }

        return true;
    }

    public boolean
    shouldCheckWeakPower(IBlockAccess world, int x, int y, int z, int side) {
        return true;
    }

    public boolean getWeakChanges(IBlockAccess world, int x, int y, int z) {
        return true;
    }

    public void updateTick(World world, int x, int y, int z, Random random) {
        this.checkRedstone(world, x, y, z);
    }

    public boolean isGettingRedstone() {
        return this.isGettingRedstone;
    }

    private void checkRedstone(World world, int x, int y, int z) {
        if (world.isBlockIndirectlyGettingPowered(x, y, z)) {
            this.isGettingRedstone = true;
        } else {
            this.isGettingRedstone = false;
        }
    }
}
