package covers1624.powerconverters.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockPowerConverterFactorization extends ItemBlock {
    public ItemBlockPowerConverterFactorization(Block block) {
        super(block);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
    }

    public int getMetadata(int i) {
        return i;
    }

    public String getUnlocalizedName(ItemStack itemstack) {
        int md = itemstack.getItemDamage();
        if (md == 0) {
            return "powerconverters.fz.consumer";
        } else {
            return md == 1 ? "powerconverters.fz.producer" : "unknown";
        }
    }

    public void getSubItems(Item item, CreativeTabs creativeTab, List subTypes) {
        for (int i = 0; i <= 1; ++i) {
            subTypes.add(new ItemStack(item, 1, i));
        }
    }
}
