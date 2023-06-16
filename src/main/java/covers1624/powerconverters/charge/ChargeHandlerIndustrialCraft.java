package covers1624.powerconverters.charge;

import covers1624.powerconverters.api.charge.IChargeHandler;
import covers1624.powerconverters.api.registry.PowerSystemRegistry;
import covers1624.powerconverters.init.PowerSystems;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import net.minecraft.item.ItemStack;

public class ChargeHandlerIndustrialCraft implements IChargeHandler {
    public PowerSystemRegistry.PowerSystem getPowerSystem() {
        return PowerSystems.powerSystemIndustrialCraft;
    }

    public boolean canHandle(ItemStack stack) {
        return stack != null && stack.getItem() instanceof IElectricItem;
    }

    public double charge(ItemStack stack, double energyInput) {
        double eu = energyInput
            / (double) PowerSystems.powerSystemIndustrialCraft.getScaleAmmount();
        eu -= ElectricItem.manager.charge(
            stack, eu, ((IElectricItem) stack.getItem()).getTier(stack), false, false
        );
        return eu * (double) PowerSystems.powerSystemIndustrialCraft.getScaleAmmount();
    }

    public double discharge(ItemStack stack, double energyRequest) {
        double eu = energyRequest
            / (double) PowerSystems.powerSystemIndustrialCraft.getScaleAmmount();
        eu = ElectricItem.manager.discharge(
            stack,
            eu,
            ((IElectricItem) stack.getItem()).getTier(stack),
            false,
            false,
            false
        );
        return eu * (double) PowerSystems.powerSystemIndustrialCraft.getScaleAmmount();
    }

    public String name() {
        return "Industrial Craft";
    }

    public boolean isItemCharged(ItemStack stack) {
        if (this.canHandle(stack)) {
            IElectricItem item = (IElectricItem) stack.getItem();
            if (item.getMaxCharge(stack) == ElectricItem.manager.getCharge(stack)) {
                return true;
            }
        }

        return false;
    }
}
