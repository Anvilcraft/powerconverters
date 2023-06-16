package covers1624.powerconverters.container;

import covers1624.powerconverters.net.PacketPipeline;
import covers1624.powerconverters.tile.main.TileEntityEnergyBridge;
import covers1624.powerconverters.util.InventoryUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;

public class ContainerEnergyBridge extends Container {
    private TileEntityEnergyBridge energyBridge;
    private EntityPlayer player;

    public ContainerEnergyBridge(
        TileEntityEnergyBridge bridge, InventoryPlayer inventoryPlayer
    ) {
        this.player = inventoryPlayer.player;
        this.energyBridge = bridge;
        InventoryUtils.bindPlayerInventory(this, inventoryPlayer, 8, 113);
    }

    public boolean canInteractWith(EntityPlayer var1) {
        return true;
    }

    public void updateProgressBar(int var, int value) {}

    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        PacketPipeline.INSTANCE.sendTo(
            this.energyBridge.getNetPacket(), (EntityPlayerMP) this.player
        );
    }

    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2) {
        return null;
    }
}
