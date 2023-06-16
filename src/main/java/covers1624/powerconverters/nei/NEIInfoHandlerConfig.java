package covers1624.powerconverters.nei;

import java.util.Iterator;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import codechicken.nei.recipe.GuiCraftingRecipe;
import codechicken.nei.recipe.ICraftingHandler;
import codechicken.nei.recipe.ShapedRecipeHandler;
import codechicken.nei.recipe.ShapelessRecipeHandler;
import covers1624.powerconverters.init.Recipes;
import covers1624.powerconverters.util.LogHelper;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;

public class NEIInfoHandlerConfig implements IConfigureNEI {
    public void loadConfig() {
        LogHelper.info("NEI Has called us to init.");
        API.registerUsageHandler(new InfoHandler());
    }

    public String getName() {
        return "PowerConverters: Nei Integration";
    }

    public String getVersion() {
        return "1";
    }

    public static void addRecipesToNEI() {
        Iterator i$ = GuiCraftingRecipe.craftinghandlers.iterator();

        while (true) {
            ICraftingHandler handler;
            IRecipe recipe;
            do {
                if (!i$.hasNext()) {
                    return;
                }

                handler = (ICraftingHandler) i$.next();
                if (handler.getClass().getName() == ShapedRecipeHandler.class.getName()) {
                    ShapedRecipeHandler shapedRecipeHandler = (ShapedRecipeHandler) handler;
                    i$ = Recipes.getCurrentRecipes().iterator();

                    while (i$.hasNext()) {
                        recipe = (IRecipe) i$.next();
                        if (recipe instanceof ShapedRecipes) {
                            shapedRecipeHandler.getClass();
                            ShapedRecipeHandler.CachedShapedRecipe shapedRecipe = shapedRecipeHandler.new CachedShapedRecipe(
                                    (ShapedRecipes) recipe);
                            shapedRecipe.computeVisuals();
                            shapedRecipeHandler.arecipes.add(shapedRecipe);
                        }
                    }
                }
            } while (handler.getClass().getName() != ShapelessRecipeHandler.class.getName());

            ShapelessRecipeHandler shapelessRecipeHandler = (ShapelessRecipeHandler) handler;
            i$ = Recipes.getCurrentRecipes().iterator();

            while (i$.hasNext()) {
                recipe = (IRecipe) i$.next();
                if (recipe instanceof ShapelessRecipes) {
                    ShapelessRecipes shapelessRecipe = (ShapelessRecipes) recipe;
                    shapelessRecipeHandler.getClass();
                    ShapelessRecipeHandler.CachedShapelessRecipe cachedShapelessRecipe = shapelessRecipeHandler.new CachedShapelessRecipe(
                            shapelessRecipe.recipeItems, shapelessRecipe.getRecipeOutput());
                    shapelessRecipeHandler.arecipes.add(cachedShapelessRecipe);
                }
            }
        }
    }
}
