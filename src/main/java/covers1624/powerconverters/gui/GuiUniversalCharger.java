package covers1624.powerconverters.gui;

import covers1624.powerconverters.container.ContainerUniversalCharger;
import covers1624.powerconverters.tile.main.TileEntityCharger;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiUniversalCharger extends GuiContainer {
    public GuiUniversalCharger(
        InventoryPlayer playerInventory, TileEntityCharger charger
    ) {
        super(new ContainerUniversalCharger(playerInventory, charger));
        super.ySize = 186;
    }

    protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
        super.fontRendererObj.drawString("Universal Charger", 40, 6, 4210752);
        super.fontRendererObj.drawString("Inventory", 8, super.ySize - 96 + 2, 4210752);
    }

    protected void
    drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        super.mc.renderEngine.bindTexture(
            new ResourceLocation("powerconverters:textures/gui/universalCharger.png")
        );
        int x = (super.width - super.xSize) / 2;
        int y = (super.height - super.ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, super.xSize, super.ySize);
    }
}
