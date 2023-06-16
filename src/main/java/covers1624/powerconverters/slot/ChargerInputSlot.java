package covers1624.powerconverters.slot;

import java.util.Iterator;

import covers1624.powerconverters.api.charge.IChargeHandler;
import covers1624.powerconverters.api.registry.UniversalChargerRegistry;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ChargerInputSlot extends Slot {
    public ChargerInputSlot(IInventory inventory, int slot, int xPos, int yPos) {
        super(inventory, slot, xPos, yPos);
    }

    public boolean isItemValid(ItemStack stack) {
        Iterator i$ = UniversalChargerRegistry.getChargeHandlers().iterator();

        IChargeHandler handler;
        do {
            if (!i$.hasNext()) {
                return false;
            }

            handler = (IChargeHandler) i$.next();
        } while (!handler.canHandle(stack));

        return true;
    }
}
