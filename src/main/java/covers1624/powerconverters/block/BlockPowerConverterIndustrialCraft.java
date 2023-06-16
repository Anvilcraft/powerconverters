package covers1624.powerconverters.block;

import covers1624.powerconverters.gui.PCCreativeTab;
import covers1624.powerconverters.tile.ic2.TileEntityIndustrialCraftConsumer;
import covers1624.powerconverters.tile.ic2.TileEntityIndustrialCraftProducer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockPowerConverterIndustrialCraft extends BlockPowerConverter {
    public BlockPowerConverterIndustrialCraft() {
        super(8);
        this.setBlockName("powerconverters.ic2");
        this.setCreativeTab(PCCreativeTab.tab);
    }

    public TileEntity createTileEntity(World world, int metadata) {
        if (metadata == 0) {
            return new TileEntityIndustrialCraftConsumer(0);
        } else if (metadata == 1) {
            return new TileEntityIndustrialCraftProducer(0);
        } else if (metadata == 2) {
            return new TileEntityIndustrialCraftConsumer(1);
        } else if (metadata == 3) {
            return new TileEntityIndustrialCraftProducer(1);
        } else if (metadata == 4) {
            return new TileEntityIndustrialCraftConsumer(2);
        } else if (metadata == 5) {
            return new TileEntityIndustrialCraftProducer(2);
        } else if (metadata == 6) {
            return new TileEntityIndustrialCraftConsumer(3);
        } else {
            return metadata == 7 ? new TileEntityIndustrialCraftProducer(3) : null;
        }
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister ir) {
        super._icons[0] = ir.registerIcon(
            "powerconverters:" + this.getUnlocalizedName() + ".lv.consumer.off"
        );
        super._icons[1] = ir.registerIcon(
            "powerconverters:" + this.getUnlocalizedName() + ".lv.consumer.on"
        );
        super._icons[2] = ir.registerIcon(
            "powerconverters:" + this.getUnlocalizedName() + ".lv.producer.off"
        );
        super._icons[3] = ir.registerIcon(
            "powerconverters:" + this.getUnlocalizedName() + ".lv.producer.on"
        );
        super._icons[4] = ir.registerIcon(
            "powerconverters:" + this.getUnlocalizedName() + ".mv.consumer.off"
        );
        super._icons[5] = ir.registerIcon(
            "powerconverters:" + this.getUnlocalizedName() + ".mv.consumer.on"
        );
        super._icons[6] = ir.registerIcon(
            "powerconverters:" + this.getUnlocalizedName() + ".mv.producer.off"
        );
        super._icons[7] = ir.registerIcon(
            "powerconverters:" + this.getUnlocalizedName() + ".mv.producer.on"
        );
        super._icons[8] = ir.registerIcon(
            "powerconverters:" + this.getUnlocalizedName() + ".hv.consumer.off"
        );
        super._icons[9] = ir.registerIcon(
            "powerconverters:" + this.getUnlocalizedName() + ".hv.consumer.on"
        );
        super._icons[10] = ir.registerIcon(
            "powerconverters:" + this.getUnlocalizedName() + ".hv.producer.off"
        );
        super._icons[11] = ir.registerIcon(
            "powerconverters:" + this.getUnlocalizedName() + ".hv.producer.on"
        );
        super._icons[12] = ir.registerIcon(
            "powerconverters:" + this.getUnlocalizedName() + ".ev.consumer.off"
        );
        super._icons[13] = ir.registerIcon(
            "powerconverters:" + this.getUnlocalizedName() + ".ev.consumer.on"
        );
        super._icons[14] = ir.registerIcon(
            "powerconverters:" + this.getUnlocalizedName() + ".ev.producer.off"
        );
        super._icons[15] = ir.registerIcon(
            "powerconverters:" + this.getUnlocalizedName() + ".ev.producer.on"
        );
    }
}
