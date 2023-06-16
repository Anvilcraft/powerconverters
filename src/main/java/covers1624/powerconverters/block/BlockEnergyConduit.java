package covers1624.powerconverters.block;

import covers1624.powerconverters.gui.PCCreativeTab;
import covers1624.powerconverters.tile.main.TileEnergyConduit;
import covers1624.powerconverters.util.IUpdateTileWithCords;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockEnergyConduit extends BlockContainer {
    public BlockEnergyConduit() {
        super(Material.rock);
        this.setBlockName("powerconverters.conduit");
        this.setCreativeTab(PCCreativeTab.tab);
    }

    public boolean renderAsNormalBlock() {
        return false;
    }

    public int getRenderType() {
        return 22;
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public void onNeighborChange(
        IBlockAccess world, int x, int y, int z, int tileX, int tileY, int tileZ
    ) {
        TileEntity te = world instanceof World ? world.getTileEntity(x, y, z)
                                               : world.getTileEntity(x, y, z);
        if (te instanceof IUpdateTileWithCords) {
            ((IUpdateTileWithCords) te).onNeighboorChanged(tileX, tileY, tileZ);
        }
    }

    public boolean hasTileEntity(int metadata) {
        return true;
    }

    public TileEntity createNewTileEntity(World world, int par1) {
        return new TileEnergyConduit();
    }
}
