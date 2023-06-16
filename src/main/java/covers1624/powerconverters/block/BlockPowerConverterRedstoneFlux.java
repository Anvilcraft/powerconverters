package covers1624.powerconverters.block;

import covers1624.powerconverters.gui.PCCreativeTab;
import covers1624.powerconverters.tile.redstoneflux.TileEntityRedstoneFluxConsumer;
import covers1624.powerconverters.tile.redstoneflux.TileEntityRedstoneFluxProducer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockPowerConverterRedstoneFlux extends BlockPowerConverter {
    public BlockPowerConverterRedstoneFlux() {
        super(2);
        this.setBlockName("powerconverters.rf");
        this.setCreativeTab(PCCreativeTab.tab);
    }

    public TileEntity createTileEntity(World world, int meta) {
        if (meta == 0) {
            return new TileEntityRedstoneFluxConsumer();
        } else {
            return meta == 1 ? new TileEntityRedstoneFluxProducer() : null;
        }
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister ir) {
        super._icons[0]
            = ir.registerIcon("powerconverters:tile.powerconverters.rf.consumer.off");
        super._icons[1]
            = ir.registerIcon("powerconverters:tile.powerconverters.rf.consumer.on");
        super._icons[2]
            = ir.registerIcon("powerconverters:tile.powerconverters.rf.producer.off");
        super._icons[3]
            = ir.registerIcon("powerconverters:tile.powerconverters.rf.producer.on");
    }
}
