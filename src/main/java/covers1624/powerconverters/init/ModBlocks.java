package covers1624.powerconverters.init;

import covers1624.powerconverters.block.BlockEnergyConduit;
import covers1624.powerconverters.block.BlockPowerConverterCommon;
import covers1624.powerconverters.block.BlockPowerConverterFactorization;
import covers1624.powerconverters.block.BlockPowerConverterIndustrialCraft;
import covers1624.powerconverters.block.BlockPowerConverterRedstoneFlux;
import covers1624.powerconverters.block.BlockPowerConverterSteam;
import covers1624.powerconverters.item.ItemBlockPowerConverterCommon;
import covers1624.powerconverters.item.ItemBlockPowerConverterFactorization;
import covers1624.powerconverters.item.ItemBlockPowerConverterIndustrialCraft;
import covers1624.powerconverters.item.ItemBlockPowerConverterRedstoneFlux;
import covers1624.powerconverters.item.ItemBlockPowerConverterSteam;
import covers1624.powerconverters.tile.factorization.TileEntityPowerConverterFactorizationConsumer;
import covers1624.powerconverters.tile.factorization.TileEntityPowerConverterFactorizationProducer;
import covers1624.powerconverters.tile.ic2.TileEntityIndustrialCraftConsumer;
import covers1624.powerconverters.tile.ic2.TileEntityIndustrialCraftProducer;
import covers1624.powerconverters.tile.main.TileEnergyConduit;
import covers1624.powerconverters.tile.main.TileEntityCharger;
import covers1624.powerconverters.tile.main.TileEntityEnergyBridge;
import covers1624.powerconverters.tile.redstoneflux.TileEntityRedstoneFluxConsumer;
import covers1624.powerconverters.tile.redstoneflux.TileEntityRedstoneFluxProducer;
import covers1624.powerconverters.tile.steam.TileEntitySteamConsumer;
import covers1624.powerconverters.tile.steam.TileEntitySteamProducer;
import covers1624.powerconverters.util.RFHelper;
import covers1624.powerconverters.util.TextureUtils;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;

public class ModBlocks {
    public static Block conduitBlock;
    public static Block converterBlockCommon;
    public static Block converterBlockIndustrialCraft;
    public static Block converterBlockSteam;
    public static Block converterBlockFactorization;
    public static Block converterBlockRedstoneFlux;

    public static void init() {
        conduitBlock = new BlockEnergyConduit();
        registerBlock(conduitBlock, conduitBlock.getUnlocalizedName());
        GameRegistry.registerTileEntity(TileEnergyConduit.class, "powerConverterConduit");
        converterBlockCommon = new BlockPowerConverterCommon();
        registerBlock(
            converterBlockCommon,
            ItemBlockPowerConverterCommon.class,
            converterBlockCommon.getUnlocalizedName()
        );
        GameRegistry.registerTileEntity(TileEntityEnergyBridge.class, "EnergyBridge");
        GameRegistry.registerTileEntity(TileEntityCharger.class, "UniversalCharger");
        if (Loader.isModLoaded("ThermalExpansion") || Loader.isModLoaded("BuildCraft")
            || RFHelper.iEnergyHandlerExists) {
            converterBlockRedstoneFlux = new BlockPowerConverterRedstoneFlux();
            registerBlock(
                converterBlockRedstoneFlux,
                ItemBlockPowerConverterRedstoneFlux.class,
                converterBlockRedstoneFlux.getUnlocalizedName()
            );
            GameRegistry.registerTileEntity(
                TileEntityRedstoneFluxConsumer.class, "RFConsumer"
            );
            GameRegistry.registerTileEntity(
                TileEntityRedstoneFluxProducer.class, "RFProducer"
            );
        }

        if (Loader.isModLoaded("IC2")) {
            converterBlockIndustrialCraft = new BlockPowerConverterIndustrialCraft();
            registerBlock(
                converterBlockIndustrialCraft,
                ItemBlockPowerConverterIndustrialCraft.class,
                converterBlockIndustrialCraft.getUnlocalizedName()
            );
            GameRegistry.registerTileEntity(
                TileEntityIndustrialCraftConsumer.class, "IC2Consumer"
            );
            GameRegistry.registerTileEntity(
                TileEntityIndustrialCraftProducer.class, "IC2Producer"
            );
        }

        converterBlockSteam = new BlockPowerConverterSteam();
        registerBlock(
            converterBlockSteam,
            ItemBlockPowerConverterSteam.class,
            converterBlockSteam.getUnlocalizedName()
        );
        GameRegistry.registerTileEntity(TileEntitySteamConsumer.class, "SteamConsumer");
        GameRegistry.registerTileEntity(TileEntitySteamProducer.class, "SteamProducer");
        if (Loader.isModLoaded("factorization")) {
            converterBlockFactorization = new BlockPowerConverterFactorization();
            registerBlock(
                converterBlockFactorization,
                ItemBlockPowerConverterFactorization.class,
                converterBlockFactorization.getUnlocalizedName()
            );
            GameRegistry.registerTileEntity(
                TileEntityPowerConverterFactorizationConsumer.class, "FZConsumer"
            );
            GameRegistry.registerTileEntity(
                TileEntityPowerConverterFactorizationProducer.class, "FZProducer"
            );
        }
    }

    private static void registerBlock(Block block, Class itemclass, String name) {
        GameRegistry.registerBlock(
            block, itemclass, TextureUtils.stripUnlocalizedName(name)
        );
    }

    private static void registerBlock(Block block, String name) {
        GameRegistry.registerBlock(block, TextureUtils.stripUnlocalizedName(name));
    }
}
