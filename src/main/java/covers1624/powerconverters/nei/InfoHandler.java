package covers1624.powerconverters.nei;

import java.util.ArrayList;
import java.util.List;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.PositionedStack;
import codechicken.nei.api.IOverlayHandler;
import codechicken.nei.api.IRecipeOverlayRenderer;
import codechicken.nei.recipe.GuiRecipe;
import codechicken.nei.recipe.ICraftingHandler;
import codechicken.nei.recipe.IUsageHandler;
import covers1624.powerconverters.util.LogHelper;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class InfoHandler implements IUsageHandler, ICraftingHandler {
    public static FontRenderer fontRenderer;
    public static int color;
    ItemStack displayItem;
    boolean precise = false;
    String id;
    String name;
    String[] info;
    public boolean checkedOrder = false;
    int noLinesPerPage = 12;
    public final String suffix = ".documentation";

    public InfoHandler() {
        this.displayItem = null;
    }

    public InfoHandler(ItemStack item) {
        if (!StatCollector.canTranslate(item.getUnlocalizedName() + ".documentation")
            && !StatCollector.canTranslate(
                item.getUnlocalizedName() + ".documentation"
                + ".0"
            )) {
            this.id = item.getItem().getUnlocalizedName();
            this.name
                = StatCollector.translateToLocal(item.getItem().getUnlocalizedName());
            this.precise = false;
        } else {
            this.id = item.getUnlocalizedName();
            this.name = StatCollector.translateToLocal(item.getUnlocalizedName());
            this.precise = true;
        }

        if (StatCollector.canTranslate(this.id + ".documentation")) {
            List list = this.splitString(
                StatCollector.translateToLocal(this.id + ".documentation")
            );
            this.info = (String[]) ((String[]) list.toArray(new String[list.size()]));
        } else {
            ArrayList temp = new ArrayList();

            for (int i = 0; StatCollector.canTranslate(
                     this.id + ".documentation"
                     + "." + i
                 );
                 ++i) {
                String a = StatCollector.translateToLocal(
                    this.id + ".documentation"
                    + "." + i
                );
                temp.addAll(this.splitString(a));
            }

            this.info = (String[]) ((String[]) temp.toArray(new String[temp.size()]));
        }

        this.displayItem = item.copy();
        this.displayItem.stackSize = 1;
    }

    public void drawBackground(int arg0) {}

    public void drawForeground(int recipe) {
        List text = fontRenderer.listFormattedStringToWidth(
            this.info[recipe], this.getWidth() - 8
        );

        for (int i = 0; i < text.size(); ++i) {
            String toDraw = (String) text.get(i);
            GuiDraw.drawString(
                toDraw,
                this.getWidth() / 2 - GuiDraw.getStringWidth(toDraw) / 2,
                18 + i * 8,
                color,
                false
            );
        }
    }

    public List splitString(String input) {
        ArrayList list = new ArrayList();
        List page = fontRenderer.listFormattedStringToWidth(input, this.getWidth() - 8);
        if (page.size() < this.noLinesPerPage) {
            list.add(input);
        } else {
            String temp = "";

            for (int i = 0; i < page.size(); ++i) {
                temp = temp + (String) page.get(i) + " ";
                if (i > 0 && i % this.noLinesPerPage == 0) {
                    String temp2 = temp.trim();
                    list.add(temp2);
                    temp = "";
                }
            }

            temp = temp.trim();
            if (!"".equals(temp)) {
                list.add(temp);
            }
        }

        return list;
    }

    public int getWidth() {
        return 166;
    }

    public List getIngredientStacks(int arg0) {
        return new ArrayList();
    }

    public List getOtherStacks(int arg0) {
        return new ArrayList();
    }

    public IOverlayHandler getOverlayHandler(GuiContainer arg0, int arg1) {
        return null;
    }

    public IRecipeOverlayRenderer getOverlayRenderer(GuiContainer gui, int arg1) {
        return null;
    }

    public String getRecipeName() {
        if (this.displayItem == null) {
            return "Documentation";
        } else {
            String s = Item.itemRegistry.getNameForObject(this.displayItem.getItem());
            String modid = s.split(":")[0];
            if ("minecraft".equals(modid)) {
                return "Minecraft";
            } else {
                ModContainer selectMod
                    = (ModContainer) Loader.instance().getIndexedModList().get(modid);
                return selectMod == null ? modid
                                         : (!selectMod.getMetadata().autogenerated
                                                ? selectMod.getMetadata().name
                                                : selectMod.getName());
            }
        }
    }

    public PositionedStack getResultStack(int arg0) {
        return new PositionedStack(this.displayItem, this.getWidth() / 2 - 9, 0, false);
    }

    public List
    handleItemTooltip(GuiRecipe gui, ItemStack stack, List currenttip, int recipe) {
        return currenttip;
    }

    public List handleTooltip(GuiRecipe gui, List currenttip, int recipe) {
        return currenttip;
    }

    public boolean hasOverlay(GuiContainer gui, Container container, int recipe) {
        return false;
    }

    public boolean keyTyped(GuiRecipe gui, char keyChar, int keyCode, int recipe) {
        return false;
    }

    public boolean mouseClicked(GuiRecipe gui, int button, int recipe) {
        return false;
    }

    public int numRecipes() {
        return this.displayItem != null && this.info != null ? this.info.length : 0;
    }

    public void onUpdate() {}

    public int recipiesPerPage() {
        return 1;
    }

    public boolean isValidItem(ItemStack item) {
        boolean flag = false;
        LogHelper.info(item.getUnlocalizedName() + ".documentation");
        if (StatCollector.canTranslate(item.getUnlocalizedName() + ".documentation")) {
            flag = true;
        } else if (StatCollector.canTranslate(
                       item.getItem().getUnlocalizedName() + ".documentation"
                   )) {
            flag = true;
        } else if (StatCollector.canTranslate(
                       item.getUnlocalizedName() + ".documentation"
                       + ".0"
                   )) {
            flag = true;
        } else if (StatCollector.canTranslate(
                       item.getItem().getUnlocalizedName() + ".documentation"
                       + ".0"
                   )) {
            flag = true;
        }

        return flag;
    }

    public IUsageHandler getUsageHandler(String inputId, Object... ingredients) {
        if (!inputId.equals("item")) {
            return this;
        } else {
            Object[] ingredientsArray = ingredients;
            int ingredientsLength = ingredients.length;

            for (int i = 0; i < ingredientsLength; ++i) {
                Object ingredient = ingredientsArray[i];
                if (ingredient instanceof ItemStack
                    && this.isValidItem((ItemStack) ingredient)) {
                    return new InfoHandler((ItemStack) ingredient);
                }
            }

            return this;
        }
    }

    public ICraftingHandler getRecipeHandler(String outputId, Object... results) {
        if (!outputId.equals("item")) {
            return this;
        } else {
            Object[] ingredientsArray = results;
            int ingredientsLength = results.length;

            for (int i = 0; i < ingredientsLength; ++i) {
                Object ingredient = ingredientsArray[i];
                if (ingredient instanceof ItemStack
                    && this.isValidItem((ItemStack) ingredient)) {
                    return new InfoHandler((ItemStack) ingredient);
                }
            }

            return this;
        }
    }

    static {
        fontRenderer = Minecraft.getMinecraft().fontRenderer;
        color = -12566464;
    }
}
