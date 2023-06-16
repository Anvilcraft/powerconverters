package covers1624.powerconverters.block;

import covers1624.powerconverters.gui.PCCreativeTab;
import covers1624.powerconverters.tile.steam.TileEntitySteamConsumer;
import covers1624.powerconverters.tile.steam.TileEntitySteamProducer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockPowerConverterSteam extends BlockPowerConverter {
    public BlockPowerConverterSteam() {
        super(2);
        this.setBlockName("powerconverters.steam");
        this.setCreativeTab(PCCreativeTab.tab);
    }

    public TileEntity createTileEntity(World world, int metadata) {
        if (metadata == 0) {
            return new TileEntitySteamConsumer();
        } else {
            return metadata == 1 ? new TileEntitySteamProducer() : null;
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
