package covers1624.powerconverters.gui;

import covers1624.powerconverters.init.ModBlocks;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class PCCreativeTab extends CreativeTabs {
    public static final PCCreativeTab tab = new PCCreativeTab();

    public PCCreativeTab() {
        super("Power Converters");
    }

    public ItemStack getIconItemStack() {
        return new ItemStack(ModBlocks.converterBlockCommon, 1, 0);
    }

    public String getTranslatedTabLabel() {
        return this.getTabLabel();
    }

    public Item getTabIconItem() {
        return null;
    }
}
