package covers1624.powerconverters.handler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import covers1624.powerconverters.PowerConverters;
import covers1624.powerconverters.init.Recipes;
import covers1624.powerconverters.net.PacketPipeline;
import covers1624.powerconverters.net.RecipeSyncPacket;
import covers1624.powerconverters.util.IRecipeHandler;
import covers1624.powerconverters.util.LogHelper;
import covers1624.powerconverters.util.RecipeRemover;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fluids.FluidRegistry;

public class PCEventHandler {
    public static int ticksInGame = 0;

    @SubscribeEvent
    public void onFluidRegisterEvent(FluidRegistry.FluidRegisterEvent event) {
        LogHelper.info(event.fluidName);
        if (event.fluidName.equals("Steam")) {
            PowerConverters.steamId = event.fluidID;
        } else if (event.fluidName.equals("steam") && PowerConverters.steamId == -1) {
            PowerConverters.steamId = event.fluidID;
        }
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.modID.equalsIgnoreCase("PowerConverters3")) {
            ConfigurationHandler.INSTANCE.loadConfiguration();
        }
    }

    @SideOnly(Side.SERVER)
    public void onClientJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!ConfigurationHandler.sendRecipesToClient) {
            LogHelper.trace("Recipe Sending is turned off.");
        } else {
            NBTTagCompound tag = new NBTTagCompound();
            List recipes = Recipes.getCurrentRecipes();
            NBTTagList tagList = new NBTTagList();
            Iterator i$ = recipes.iterator();

            while (i$.hasNext()) {
                IRecipe recipe = (IRecipe) i$.next();
                NBTTagCompound tagCompound = IRecipeHandler.writeIRecipeToTag(recipe);
                tagList.appendTag(tagCompound);
            }

            tag.setTag("Recipes", tagList);
            RecipeSyncPacket syncPacket = new RecipeSyncPacket(tag);
            PacketPipeline.INSTANCE.sendTo(syncPacket, (EntityPlayerMP) event.player);
        }
    }

    @SideOnly(Side.CLIENT)
    public void
    onClientDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        List currentOutputs = new ArrayList();
        Iterator i$ = Recipes.getCurrentRecipes().iterator();

        IRecipe recipe;
        while (i$.hasNext()) {
            recipe = (IRecipe) i$.next();
            ItemStack stack = recipe.getRecipeOutput();
            currentOutputs.add(stack);
        }

        RecipeRemover.removeAnyRecipes(currentOutputs);
        i$ = Recipes.getDefaultRecipes().iterator();

        while (i$.hasNext()) {
            recipe = (IRecipe) i$.next();
            CraftingManager.getInstance().getRecipeList().add(recipe);
        }
    }
}
