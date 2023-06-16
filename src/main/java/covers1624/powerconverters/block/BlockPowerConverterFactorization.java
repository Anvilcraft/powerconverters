package covers1624.powerconverters.block;

import covers1624.powerconverters.gui.PCCreativeTab;
import covers1624.powerconverters.tile.factorization.TileEntityPowerConverterFactorizationConsumer;
import covers1624.powerconverters.tile.factorization.TileEntityPowerConverterFactorizationProducer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockPowerConverterFactorization extends BlockPowerConverter {
    public BlockPowerConverterFactorization() {
        super(2);
        this.setBlockName("powerconverters.fz");
        this.setCreativeTab(PCCreativeTab.tab);
    }

    public TileEntity createTileEntity(World world, int metadata) {
        if (metadata == 0) {
            return new TileEntityPowerConverterFactorizationConsumer();
        } else {
            return metadata == 1 ? new TileEntityPowerConverterFactorizationProducer()
                                 : null;
        }
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister ir) {
        super._icons[0] = ir.registerIcon(
            "powerconverters:" + this.getUnlocalizedName() + ".consumer.off"
        );
        super._icons[1] = ir.registerIcon(
            "powerconverters:" + this.getUnlocalizedName() + ".consumer.on"
        );
        super._icons[2] = ir.registerIcon(
            "powerconverters:" + this.getUnlocalizedName() + ".producer.off"
        );
        super._icons[3] = ir.registerIcon(
            "powerconverters:" + this.getUnlocalizedName() + ".producer.on"
        );
    }
}
