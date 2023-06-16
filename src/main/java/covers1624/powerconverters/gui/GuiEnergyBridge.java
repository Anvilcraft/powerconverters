package covers1624.powerconverters.gui;

import covers1624.powerconverters.api.bridge.BridgeSideData;
import covers1624.powerconverters.container.ContainerEnergyBridge;
import covers1624.powerconverters.tile.main.TileEntityEnergyBridge;
import covers1624.powerconverters.util.GuiArea;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;

public class GuiEnergyBridge extends GuiContainer {
    private TileEntityEnergyBridge energyBridge;
    private GuiArea[] guiAreas = new GuiArea[7];

    public GuiEnergyBridge(ContainerEnergyBridge container, TileEntityEnergyBridge te) {
        super(container);
        super.ySize = 195;
        this.energyBridge = te;
    }

    private void initGuiAreas() {
        this.guiAreas[0] = new GuiArea(8, 17, 122, 47, ForgeDirection.UP);
        this.guiAreas[1] = new GuiArea(8, 51, 122, 81, ForgeDirection.NORTH);
        this.guiAreas[2] = new GuiArea(8, 85, 122, 115, ForgeDirection.EAST);
        this.guiAreas[3] = new GuiArea(126, 17, 240, 47, ForgeDirection.DOWN);
        this.guiAreas[4] = new GuiArea(126, 51, 240, 81, ForgeDirection.SOUTH);
        this.guiAreas[5] = new GuiArea(126, 85, 240, 115, ForgeDirection.WEST);
        this.guiAreas[6] = new GuiArea(44, 119, 204, 163, ForgeDirection.UNKNOWN);
    }

    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.drawOldGuiContainerForegroundLayer(mouseX, mouseY);
    }

    protected void
    drawGuiContainerBackgroundLayer(float gameTicks, int mouseX, int mouseY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        super.mc.renderEngine.bindTexture(
            new ResourceLocation("powerconverters:textures/gui/energyBridge.png")
        );
        int x = (super.width - super.xSize) / 2;
        int y = (super.height - super.ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, super.xSize, super.ySize);
        this.drawOldGuiContainerBackgroundLayer(gameTicks, mouseX, mouseY, x, y);
    }

    protected void mouseClicked(int mouseX, int mouseY, int clickedButton) {
        super.mouseClicked(mouseX, mouseY, clickedButton);
    }

    private void drawSlotLighting(int mouseX, int mouseY, int x, int y) {
        GL11.glPushMatrix();
        GuiArea[] arr$ = this.guiAreas;
        int len$ = arr$.length;

        for (int i$ = 0; i$ < len$; ++i$) {
            GuiArea guiArea = arr$[i$];
            if (guiArea.isMouseInArea(mouseX, mouseY, x, y)) {
                GL11.glDisable(2896);
                GL11.glDisable(2929);
                GL11.glColorMask(true, true, true, false);
                this.drawGradientRect(
                    guiArea.xTop + x,
                    guiArea.yTop + y,
                    guiArea.xBottom + x,
                    guiArea.yBottom + y,
                    -2130706433,
                    -2130706433
                );
                GL11.glColorMask(true, true, true, true);
                GL11.glEnable(2896);
                GL11.glEnable(2929);
            }
        }

        GL11.glPopMatrix();
    }

    private String getOutputRateString(BridgeSideData data) {
        if (!data.isConnected) {
            return "NO LINK";
        } else {
            double rate = data.outputRate;
            if (rate > 1000.0) {
                double rateThousand = rate / 1000.0;
                return String.format(
                    "%.1f %s%s", rateThousand, "k", data.powerSystem.getUnit()
                );
            } else {
                return rate + " " + data.powerSystem.getUnit();
            }
        }
    }

    private void drawOldGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.fontRendererObj.drawString("Energy Bridge", 8, 6, 4210752);
        if (this.energyBridge.isInputLimited()) {
            super.fontRendererObj.drawString("INPUT LIMITED", 98, 6, -1);
        } else {
            super.fontRendererObj.drawString("OUTPUT LIMITED", 90, 6, -1);
        }

        for (int i = 0; i < 6; ++i) {
            ForgeDirection dir = ForgeDirection.getOrientation(i);
            super.fontRendererObj.drawString(dir.toString(), 10, 6 + 12 * (i + 1), -1);
            BridgeSideData data = this.energyBridge.getDataForSide(dir);
            if ((data.isConsumer || data.isProducer) && data.powerSystem != null) {
                String name = data.powerSystem.getAbbreviation();
                if (data.powerSystem.getVoltageNames() != null) {
                    name = name + " "
                        + data.powerSystem.getVoltageNames()[data.voltageNameIndex];
                }

                super.fontRendererObj.drawString(name, 49, 6 + 12 * (i + 1), -1);
                super.fontRendererObj.drawString(
                    data.isConsumer ? "IN" : "OUT", 92, 6 + 12 * (i + 1), -1
                );
                super.fontRendererObj.drawString(
                    this.getOutputRateString(data), 119, 6 + 12 * (i + 1), -1
                );
            } else {
                super.fontRendererObj.drawString("<NONE>", 49, 6 + 12 * (i + 1), -1);
            }
        }

        super.fontRendererObj.drawString("% CHG", 10, 90, -1);
        GL11.glDisable(2896);
        drawRect(46, 97, 46 + (int) this.energyBridge.getEnergyScaled(), 89, -16734721);
        GL11.glEnable(2896);
        super.fontRendererObj.drawString(
            StatCollector.translateToLocal("container.inventory"),
            8,
            super.ySize - 96 + 2,
            4210752
        );
    }

    private void drawOldGuiContainerBackgroundLayer(
        float gameTicks, int mouseX, int mouseY, int x, int y
    ) {
        for (int i = 0; i < 6; ++i) {
            ForgeDirection dir = ForgeDirection.getOrientation(i);
            BridgeSideData data = this.energyBridge.getDataForSide(dir);
            if ((data.isConsumer || data.isProducer) && data.powerSystem != null) {
                if (!data.isConnected) {
                    this.drawTexturedModalRect(x + 7, y + 15 + 12 * i, 0, 208, 162, 12);
                } else if (data.outputRate == 0.0) {
                    this.drawTexturedModalRect(x + 7, y + 15 + 12 * i, 0, 234, 162, 12);
                } else {
                    this.drawTexturedModalRect(x + 7, y + 15 + 12 * i, 0, 195, 162, 12);
                }
            } else {
                this.drawTexturedModalRect(x + 7, y + 15 + 12 * i, 0, 221, 162, 12);
            }
        }
    }
}
