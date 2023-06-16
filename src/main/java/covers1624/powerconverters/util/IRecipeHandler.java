package covers1624.powerconverters.util;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class IRecipeHandler {
    public static NBTTagCompound writeIRecipeToTag(IRecipe recipe) {
        NBTTagCompound mainTag = new NBTTagCompound();
        NBTTagCompound tag;
        NBTTagList tagList;
        int i;
        NBTTagCompound nbtTagCompound;
        if (recipe instanceof ShapedRecipes) {
            ShapedRecipes shapedRecipes = (ShapedRecipes) recipe;
            tag = new NBTTagCompound();
            shapedRecipes.getRecipeOutput().writeToNBT(tag);
            mainTag.setTag("Output", tag);
            tagList = new NBTTagList();

            for (i = 0; i < shapedRecipes.recipeItems.length; ++i) {
                if (shapedRecipes.recipeItems[i] != null) {
                    nbtTagCompound = new NBTTagCompound();
                    nbtTagCompound.setInteger("Slot", i);
                    shapedRecipes.recipeItems[i].writeToNBT(nbtTagCompound);
                    tagList.appendTag(nbtTagCompound);
                }
            }

            mainTag.setTag("Input", tagList);
            mainTag.setInteger("Height", shapedRecipes.recipeHeight);
            mainTag.setInteger("Width", shapedRecipes.recipeWidth);
            mainTag.setString("Type", "Shaped");
            return mainTag;
        } else if (!(recipe instanceof ShapelessRecipes)) {
            return null;
        } else {
            ShapelessRecipes shapelessRecipes = (ShapelessRecipes) recipe;
            tag = new NBTTagCompound();
            shapelessRecipes.getRecipeOutput().writeToNBT(tag);
            mainTag.setTag("Output", tag);
            tagList = new NBTTagList();

            for (i = 0; i < shapelessRecipes.recipeItems.size(); ++i) {
                nbtTagCompound = new NBTTagCompound();
                ItemStack stack = (ItemStack) shapelessRecipes.recipeItems.get(i);
                stack.writeToNBT(nbtTagCompound);
                tagList.appendTag(nbtTagCompound);
            }

            mainTag.setTag("Input", tagList);
            mainTag.setString("Type", "Shapeless");
            return mainTag;
        }
    }

    public static IRecipe readIRecipeFromTag(NBTTagCompound tagCompound) {
        String type = tagCompound.getString("Type");
        ItemStack output;
        if (type.equals("Shaped")) {
            output = ItemStack.loadItemStackFromNBT((NBTTagCompound
            ) tagCompound.getTag("Output"));
            int height = tagCompound.getInteger("Height");
            int width = tagCompound.getInteger("Width");
            ItemStack[] recipeItems = new ItemStack[9];
            NBTTagList tagList = tagCompound.getTagList("Input", 10);

            for (int i = 0; i < tagList.tagCount(); ++i) {
                NBTTagCompound nbtTagCompound = tagList.getCompoundTagAt(i);
                int slot = nbtTagCompound.getInteger("Slot");
                recipeItems[slot] = ItemStack.loadItemStackFromNBT(nbtTagCompound);
            }

            ShapedRecipes shapedRecipe
                = new ShapedRecipes(width, height, recipeItems, output);
            return shapedRecipe;
        } else if (!type.equals("Shapeless")) {
            return null;
        } else {
            output = ItemStack.loadItemStackFromNBT((NBTTagCompound
            ) tagCompound.getTag("Output"));
            ArrayList recipeItems = new ArrayList();
            NBTTagList tagList = tagCompound.getTagList("Input", 10);

            for (int i = 0; i < tagList.tagCount(); ++i) {
                ItemStack itemStack
                    = ItemStack.loadItemStackFromNBT(tagList.getCompoundTagAt(i));
                recipeItems.add(itemStack);
            }

            ShapelessRecipes shapelessRecipe = new ShapelessRecipes(output, recipeItems);
            return shapelessRecipe;
        }
    }
}
