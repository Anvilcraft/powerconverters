package covers1624.powerconverters.block;

import covers1624.powerconverters.PowerConverters;
import covers1624.powerconverters.gui.PCCreativeTab;
import covers1624.powerconverters.item.DebugItem;
import covers1624.powerconverters.tile.main.TileEntityBridgeComponent;
import covers1624.powerconverters.tile.main.TileEntityCharger;
import covers1624.powerconverters.tile.main.TileEntityEnergyBridge;
import covers1624.powerconverters.util.INeighboorUpdateTile;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPowerConverterCommon extends BlockContainer {
    private IIcon _iconBridge;
    private IIcon _iconChargerOn;
    private IIcon _iconChargerOff;

    public BlockPowerConverterCommon() {
        super(Material.iron);
        this.setHardness(1.0F);
        this.setBlockName("powerconverters.common");
        this.setCreativeTab(PCCreativeTab.tab);
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister ir) {
        this._iconBridge
            = ir.registerIcon("powerconverters:" + this.getUnlocalizedName() + ".bridge");
        this._iconChargerOn = ir.registerIcon(
            "powerconverters:" + this.getUnlocalizedName() + ".charger.on"
        );
        this._iconChargerOff = ir.registerIcon(
            "powerconverters:" + this.getUnlocalizedName() + ".charger.off"
        );
    }

    public IIcon getIcon(int side, int meta) {
        if (meta == 0) {
            return this._iconBridge;
        } else {
            return meta == 2 ? this._iconChargerOff : null;
        }
    }

    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        int meta = world.getBlockMetadata(x, y, z);
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileEntityBridgeComponent) {
            if (meta == 0) {
                return this._iconBridge;
            }

            if (meta == 2) {
                boolean isConnected
                    = ((TileEntityBridgeComponent) te).isSideConnectedClient(side);
                if (isConnected) {
                    return this._iconChargerOn;
                }

                return this._iconChargerOff;
            }
        }

        return this.getIcon(side, meta);
    }

    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te != null && te instanceof INeighboorUpdateTile) {
            ((INeighboorUpdateTile) te).onNeighboorChanged();
        }
    }

    public TileEntity createNewTileEntity(World world, int md) {
        if (md == 0) {
            return new TileEntityEnergyBridge();
        } else {
            return md == 2 ? new TileEntityCharger() : null;
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
        if (te != null && te instanceof TileEntityCharger) {
            player.openGui(PowerConverters.instance, 1, world, x, y, z);
            return true;
        } else {
            if (te != null && te instanceof TileEntityBridgeComponent) {
                TileEntityEnergyBridge bridge
                    = ((TileEntityBridgeComponent) te).getFirstBridge();
                if (bridge != null) {
                    player.openGui(
                        PowerConverters.instance,
                        0,
                        world,
                        bridge.xCoord,
                        bridge.yCoord,
                        bridge.zCoord
                    );
                }
            } else if (te != null && te instanceof TileEntityEnergyBridge) {
                player.openGui(PowerConverters.instance, 0, world, x, y, z);
            }

            return true;
        }
    }

    public int damageDropped(int i) {
        return i;
    }

    public AxisAlignedBB
    getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta == 2) {
            float shrinkAmount = 0.125F;
            return AxisAlignedBB.getBoundingBox(
                (double) x,
                (double) y,
                (double) z,
                (double) (x + 1),
                (double) ((float) (y + 1) - shrinkAmount),
                (double) (z + 1)
            );
        } else {
            return super.getCollisionBoundingBoxFromPool(world, x, y, z);
        }
    }

    public void
    onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {}
}
