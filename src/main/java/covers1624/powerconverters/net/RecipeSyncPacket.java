package covers1624.powerconverters.net;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import covers1624.powerconverters.handler.ConfigurationHandler;
import covers1624.powerconverters.init.Recipes;
import covers1624.powerconverters.nei.NEIInfoHandlerConfig;
import covers1624.powerconverters.util.IRecipeHandler;
import covers1624.powerconverters.util.LogHelper;
import covers1624.powerconverters.util.RecipeRemover;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class RecipeSyncPacket extends AbstractPacket {
    private NBTTagCompound tagCompound;

    public RecipeSyncPacket() {}

    public RecipeSyncPacket(NBTTagCompound tag) {
        this.tagCompound = tag;
    }

    public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
        ByteBufUtils.writeTag(buffer, this.tagCompound);
    }

    public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
        this.tagCompound = ByteBufUtils.readTag(buffer);
    }

    public void handleClientSide(EntityPlayer player) {
        if (ConfigurationHandler.ignoreRecipesFromServer) {
            LogHelper.trace("Ignoring recipe packet from server.");
        } else {
            NBTTagList tagList = this.tagCompound.getTagList("Recipes", 10);
            List recipes = new ArrayList();

            for (int i = 0; i < tagList.tagCount(); ++i) {
                IRecipe recipe
                    = IRecipeHandler.readIRecipeFromTag(tagList.getCompoundTagAt(i));
                if (recipe != null) {
                    recipes.add(recipe);
                }
            }

            List currentOutputs = new ArrayList();
            Iterator i$ = Recipes.getCurrentRecipes().iterator();

            IRecipe recipe;
            while (i$.hasNext()) {
                recipe = (IRecipe) i$.next();
                ItemStack stack = recipe.getRecipeOutput();
                currentOutputs.add(stack);
            }

            RecipeRemover.removeAnyRecipes(currentOutputs);
            i$ = recipes.iterator();

            while (i$.hasNext()) {
                recipe = (IRecipe) i$.next();
                CraftingManager.getInstance().getRecipeList().add(recipe);
            }

            if (Loader.isModLoaded("NotEnoughItems")) {
                NEIInfoHandlerConfig.addRecipesToNEI();
            }
        }
    }

    public void handleServerSide(EntityPlayer player) {}
}
