package covers1624.powerconverters.client.render;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class TileUniversalConduitRender extends TileEntitySpecialRenderer {
   private CustomTechneModel model;

   public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partialTicks) {
      if (this.model == null) {
         this.model = new CustomTechneModel(new ResourceLocation("powerconverters:textures/model/universalConduitModel.tcn"));
      }

      CustomTechneModel modelConduit = this.model;
      this.bindTexture(new ResourceLocation("powerconverters:textures/model/universalConduitTextureDebug.png"));
      GL11.glPushMatrix();
      GL11.glEnable(32826);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glTranslatef((float)x, (float)y + 1.0F, (float)z + 1.0F);
      GL11.glScalef(0.06F, 0.06F, 0.06F);
      GL11.glTranslatef(8.35F, -2.0F, -8.35F);
      modelConduit.renderAll();
      GL11.glDisable(32826);
      GL11.glPopMatrix();
   }
}
