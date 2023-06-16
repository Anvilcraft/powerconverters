package covers1624.powerconverters.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockPowerConverterRedstoneFlux extends ItemBlock {
    public ItemBlockPowerConverterRedstoneFlux(Block block) {
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
            return "powerconverters.rf.consumer";
        } else {
            return md == 1 ? "powerconverters.rf.producer" : "unknown";
        }
    }

    public void
    addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean par4) {
        int md = itemstack.getItemDamage();
        if (md == 0) {
            list.add("Consumes RedstoneFlux");
        } else if (md == 1) {
            list.add("Produces RedstoneFlux");
        } else {
            list.add("ERROR in tool tips contact covers1624");
        }
    }

    public void getSubItems(Item item, CreativeTabs creativeTab, List subTypes) {
        for (int i = 0; i <= 1; ++i) {
            subTypes.add(new ItemStack(item, 1, i));
        }
    }
}
