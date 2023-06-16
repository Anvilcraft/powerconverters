package covers1624.powerconverters.charge;

import cofh.api.energy.IEnergyContainerItem;
import covers1624.powerconverters.api.charge.IChargeHandler;
import covers1624.powerconverters.api.registry.PowerSystemRegistry;
import covers1624.powerconverters.init.PowerSystems;
import net.minecraft.item.ItemStack;

public class ChargeHandlerRedstoneFlux implements IChargeHandler {
    public PowerSystemRegistry.PowerSystem getPowerSystem() {
        return PowerSystems.powerSystemRedstoneFlux;
    }

    public boolean canHandle(ItemStack stack) {
        return stack != null && stack.getItem() instanceof IEnergyContainerItem;
    }

    public double charge(ItemStack stack, double energyInput) {
        double RF = energyInput / (double) this.getPowerSystem().getScaleAmmount();
        RF -= (double) ((IEnergyContainerItem) stack.getItem())
                  .receiveEnergy(stack, (int) RF, false);
        return RF * (double) this.getPowerSystem().getScaleAmmount();
    }

    public double discharge(ItemStack stack, double energyRequest) {
        IEnergyContainerItem cell = (IEnergyContainerItem) stack.getItem();
        return (double
        ) (cell.extractEnergy(
               stack,
               (int) (energyRequest / (double) this.getPowerSystem().getScaleAmmount()),
               false
           )
           * this.getPowerSystem().getScaleAmmount());
    }

    public String name() {
        return "Redstone Flux";
    }

    public boolean isItemCharged(ItemStack stack) {
        if (this.canHandle(stack)) {
            IEnergyContainerItem item = (IEnergyContainerItem) stack.getItem();
            if (item.getEnergyStored(stack) == item.getMaxEnergyStored(stack)) {
                return true;
            }
        }

        return false;
    }
}
