package covers1624.powerconverters.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockPowerConverterCommon extends ItemBlock {
    public ItemBlockPowerConverterCommon(Block block) {
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
            return "powerconverters.common.bridge";
        } else {
            return md == 2 ? "powerconverters.common.charger" : "unknown";
        }
    }

    public void
    addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean par4) {
        int md = itemstack.getItemDamage();
        if (md == 0) {
            list.add("Common Block in the Power Converter MultiBlock");
        }

        if (md == 2) {
            list.add("A universal Charging Block (WIP)");
        }

        if (md != 0 && md != 2) {
            list.add("ERROR in tool tips contact covers1624");
        }
    }

    public void getSubItems(Item item, CreativeTabs creativeTab, List subTypes) {
        subTypes.add(new ItemStack(item, 1, 0));
        subTypes.add(new ItemStack(item, 1, 2));
    }
}
