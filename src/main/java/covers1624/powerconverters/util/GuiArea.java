package covers1624.powerconverters.util;

import net.minecraftforge.common.util.ForgeDirection;

public class GuiArea {
    public int xTop;
    public int yTop;
    public int xBottom;
    public int yBottom;
    public ForgeDirection direction;

    public GuiArea(int xTop, int yTop, int xBottom, int yBottom, ForgeDirection dir) {
        this.xTop = xTop;
        this.yTop = yTop;
        this.xBottom = xBottom;
        this.yBottom = yBottom;
        this.direction = dir;
    }

    public void scaleValues(
        int xTopScale, int yTopScale, int xBottomScale, int yBottomScale, boolean type
    ) {
        if (type) {
            this.xTop += xTopScale;
            this.yTop += yTopScale;
            this.xBottom += xBottomScale;
            this.yBottom += yBottomScale;
        } else {
            this.xTop -= xTopScale;
            this.yTop -= yTopScale;
            this.xBottom -= xBottomScale;
            this.yBottom -= yBottomScale;
        }
    }

    public boolean isMouseInArea(int mouseX, int mouseY) {
        return this.xTop <= mouseX && this.xBottom >= mouseX && this.yTop <= mouseY
            && this.yBottom >= mouseY;
    }

    public boolean isMouseInArea(int mouseX, int mouseY, int xScale, int yScale) {
        this.scaleValues(xScale, yScale, xScale, yScale, true);
        boolean awnser = this.isMouseInArea(mouseX, mouseY);
        this.scaleValues(xScale, yScale, xScale, yScale, false);
        return awnser;
    }
}
