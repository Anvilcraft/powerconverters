package covers1624.powerconverters.api.charge;

import covers1624.powerconverters.api.registry.PowerSystemRegistry;
import net.minecraft.item.ItemStack;

public interface IChargeHandler {
    PowerSystemRegistry.PowerSystem getPowerSystem();

    boolean canHandle(ItemStack var1);

    double charge(ItemStack var1, double var2);

    double discharge(ItemStack var1, double var2);

    boolean isItemCharged(ItemStack var1);

    String name();
}
