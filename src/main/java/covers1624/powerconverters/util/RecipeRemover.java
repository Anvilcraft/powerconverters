package covers1624.powerconverters.util;

import java.util.Iterator;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;

public class RecipeRemover {
    public static void removeAnyRecipes(List removeList) {
        Iterator i$ = removeList.iterator();

        while (i$.hasNext()) {
            ItemStack stack = (ItemStack) i$.next();
            removeAnyRecipe(stack);
        }
    }

    public static void removeShapedRecipes(List removelist) {
        Iterator i$ = removelist.iterator();

        while (i$.hasNext()) {
            ItemStack stack = (ItemStack) i$.next();
            removeShapedRecipe(stack);
        }
    }

    public static void removeAnyRecipe(ItemStack resultItem) {
        List recipes = CraftingManager.getInstance().getRecipeList();

        for (int i = 0; i < recipes.size(); ++i) {
            IRecipe tmpRecipe = (IRecipe) recipes.get(i);
            ItemStack recipeResult = tmpRecipe.getRecipeOutput();
            if (ItemStack.areItemStacksEqual(resultItem, recipeResult)) {
                recipes.remove(i--);
            }
        }
    }

    public static void removeShapedRecipe(ItemStack resultItem) {
        List recipes = CraftingManager.getInstance().getRecipeList();

        for (int i = 0; i < recipes.size(); ++i) {
            IRecipe tmpRecipe = (IRecipe) recipes.get(i);
            if (tmpRecipe instanceof ShapedRecipes) {
                ShapedRecipes recipe = (ShapedRecipes) tmpRecipe;
                ItemStack recipeResult = recipe.getRecipeOutput();
                if (ItemStack.areItemStacksEqual(resultItem, recipeResult)) {
                    recipes.remove(i++);
                }
            }
        }
    }
}
