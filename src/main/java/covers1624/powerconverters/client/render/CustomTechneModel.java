package covers1624.powerconverters.client.render;

import cpw.mods.fml.common.FMLLog;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;
import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.client.model.ModelFormatException;
import org.lwjgl.opengl.GL11;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class CustomTechneModel extends ModelBase implements IModelCustom {
   public static final List cubeTypes = Arrays.asList("d9e621f7-957f-4b77-b1ae-20dcd0da7751", "de81aa14-bd60-4228-8d8d-5238bcd3caaa");
   private String fileName;
   private Map zipContents = new HashMap();
   private Map parts = new LinkedHashMap();
   private String texture = null;
   private Dimension textureDims = null;
   private DynamicTexture dynamicTexture;
   private boolean textureNameSet = false;

   public CustomTechneModel(ResourceLocation resource) throws ModelFormatException {
      this.fileName = resource.toString();

      try {
         IResource res = Minecraft.getMinecraft().getResourceManager().getResource(resource);
         this.loadTechneModel(res.getInputStream());
      } catch (IOException var3) {
         throw new ModelFormatException("IO Exception reading model format", var3);
      }
   }

   private void loadTechneModel(InputStream stream) throws ModelFormatException {
      try {
         ZipInputStream zipInput = new ZipInputStream(stream);

         ZipEntry entry;
         byte[] data;
         while((entry = zipInput.getNextEntry()) != null) {
            data = new byte[(int)entry.getSize()];

            for(int i = 0; zipInput.available() > 0 && i < data.length; data[i++] = (byte)zipInput.read()) {
            }

            this.zipContents.put(entry.getName(), data);
         }

         data = (byte[])this.zipContents.get("model.xml");
         if (data == null) {
            throw new ModelFormatException("Model " + this.fileName + " contains no model.xml file");
         }

         DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
         DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
         Document document = documentBuilder.parse(new ByteArrayInputStream(data));
         NodeList nodeListTechne = document.getElementsByTagName("Techne");
         if (nodeListTechne.getLength() < 1) {
            throw new ModelFormatException("Model " + this.fileName + " contains no Techne tag");
         }

         NodeList nodeListModel = document.getElementsByTagName("Model");
         if (nodeListModel.getLength() < 1) {
            throw new ModelFormatException("Model " + this.fileName + " contains no Model tag");
         }

         NamedNodeMap modelAttributes = nodeListModel.item(0).getAttributes();
         if (modelAttributes == null) {
            throw new ModelFormatException("Model " + this.fileName + " contains a Model tag with no attributes");
         }

         Node modelTexture = modelAttributes.getNamedItem("texture");
         if (modelTexture != null) {
            this.texture = modelTexture.getTextContent();
         }

         NodeList textureDim = document.getElementsByTagName("TextureSize");
         if (textureDim.getLength() > 0) {
            try {
               String[] tmp = textureDim.item(0).getTextContent().split(",");
               if (tmp.length == 2) {
                  this.textureDims = new Dimension(Integer.parseInt(tmp[0]), Integer.parseInt(tmp[1]));
               }
            } catch (NumberFormatException var32) {
               throw new ModelFormatException("Model " + this.fileName + " contains a TextureSize tag with invalid data");
            }
         }

         NodeList shapes = document.getElementsByTagName("Shape");

         for(int i = 0; i < shapes.getLength(); ++i) {
            Node shape = shapes.item(i);
            NamedNodeMap shapeAttributes = shape.getAttributes();
            if (shapeAttributes == null) {
               throw new ModelFormatException("Shape #" + (i + 1) + " in " + this.fileName + " has no attributes");
            }

            Node name = shapeAttributes.getNamedItem("name");
            String shapeName = null;
            if (name != null) {
               shapeName = name.getNodeValue();
            }

            if (shapeName == null) {
               shapeName = "Shape #" + (i + 1);
            }

            String shapeType = null;
            Node type = shapeAttributes.getNamedItem("type");
            if (type != null) {
               shapeType = type.getNodeValue();
            }

            if (shapeType != null && !cubeTypes.contains(shapeType)) {
               FMLLog.warning("Model shape [" + shapeName + "] in " + this.fileName + " is not a cube, ignoring", new Object[0]);
            } else {
               try {
                  boolean mirrored = false;
                  String[] offset = new String[3];
                  String[] position = new String[3];
                  String[] rotation = new String[3];
                  String[] size = new String[3];
                  String[] textureOffset = new String[2];
                  NodeList shapeChildren = shape.getChildNodes();

                  for(int j = 0; j < shapeChildren.getLength(); ++j) {
                     Node shapeChild = shapeChildren.item(j);
                     String shapeChildName = shapeChild.getNodeName();
                     String shapeChildValue = shapeChild.getTextContent();
                     if (shapeChildValue != null) {
                        shapeChildValue = shapeChildValue.trim();
                        if (shapeChildName.equals("IsMirrored")) {
                           mirrored = !shapeChildValue.equals("False");
                        } else if (shapeChildName.equals("Offset")) {
                           offset = shapeChildValue.split(",");
                        } else if (shapeChildName.equals("Position")) {
                           position = shapeChildValue.split(",");
                        } else if (shapeChildName.equals("Rotation")) {
                           rotation = shapeChildValue.split(",");
                        } else if (shapeChildName.equals("Size")) {
                           size = shapeChildValue.split(",");
                        } else if (shapeChildName.equals("TextureOffset")) {
                           textureOffset = shapeChildValue.split(",");
                        }
                     }
                  }

                  ModelRenderer cube = new ModelRenderer(this, Integer.parseInt(textureOffset[0]), Integer.parseInt(textureOffset[1]));
                  cube.mirror = mirrored;
                  cube.addBox(Float.parseFloat(offset[0]), Float.parseFloat(offset[1]), Float.parseFloat(offset[2]), Integer.parseInt(size[0]), Integer.parseInt(size[1]), Integer.parseInt(size[2]));
                  cube.setRotationPoint(Float.parseFloat(position[0]), Float.parseFloat(position[1]) - 23.4F, Float.parseFloat(position[2]));
                  cube.rotateAngleX = (float)Math.toRadians((double)Float.parseFloat(rotation[0]));
                  cube.rotateAngleY = (float)Math.toRadians((double)Float.parseFloat(rotation[1]));
                  cube.rotateAngleZ = (float)Math.toRadians((double)Float.parseFloat(rotation[2]));
                  if (this.textureDims != null) {
                     cube.setTextureSize((int)this.textureDims.getWidth(), (int)this.textureDims.getHeight());
                  }

                  this.parts.put(shapeName, cube);
               } catch (NumberFormatException var33) {
                  FMLLog.warning("Model shape [" + shapeName + "] in " + this.fileName + " contains malformed integers within its data, ignoring", new Object[0]);
                  var33.printStackTrace();
               }
            }
         }
      } catch (ZipException var34) {
         throw new ModelFormatException("Model " + this.fileName + " is not a valid zip file");
      } catch (IOException var35) {
         throw new ModelFormatException("Model " + this.fileName + " could not be read", var35);
      } catch (ParserConfigurationException var36) {
      } catch (SAXException var37) {
         throw new ModelFormatException("Model " + this.fileName + " contains invalid XML", var37);
      }

   }

   public void bindTexture() {
      if (this.texture != null) {
         if (!this.textureNameSet) {
            try {
               byte[] textureEntry = (byte[])this.zipContents.get(this.texture);
               if (textureEntry == null) {
                  throw new ModelFormatException("Model " + this.fileName + " has no such texture " + this.texture);
               }

               BufferedImage image = ImageIO.read(new ByteArrayInputStream(textureEntry));
               this.dynamicTexture = new DynamicTexture(image);
               ResourceLocation resourceLocation = new ResourceLocation("powerconverters:textures/models/dynamic/" + this.dynamicTexture.getGlTextureId());
               Minecraft.getMinecraft().renderEngine.loadTexture(resourceLocation, this.dynamicTexture);
               Minecraft.getMinecraft().renderEngine.bindTexture(resourceLocation);
               this.textureNameSet = true;
            } catch (ZipException var4) {
               throw new ModelFormatException("Model " + this.fileName + " is not a valid zip file");
            } catch (IOException var5) {
               throw new ModelFormatException("Texture for model " + this.fileName + " could not be read", var5);
            }
         }

         if (this.textureNameSet) {
            GL11.glBindTexture(3553, this.dynamicTexture.getGlTextureId());
         }
      }

   }

   public String getType() {
      return "tcn";
   }

   public void renderAll() {
      Iterator i$ = this.parts.keySet().iterator();

      while(i$.hasNext()) {
         String name = (String)i$.next();
         ModelRenderer part = (ModelRenderer)this.parts.get(name);
         part.render(1.0F);
      }

   }

   public void renderPart(String partName) {
      ModelRenderer part = (ModelRenderer)this.parts.get(partName);
      if (part != null) {
         part.renderWithRotation(1.0F);
      }

   }

   public void renderOnly(String... groupNames) {
      Iterator it$ = this.parts.values().iterator();

      while(it$.hasNext()) {
         ModelRenderer part = (ModelRenderer)it$.next();
         String[] arr$ = groupNames;
         int len$ = groupNames.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            String groupName = arr$[i$];
            if (groupName.equalsIgnoreCase(part.boxName)) {
               part.render(1.0F);
            }
         }
      }

   }

   public void renderAllExcept(String... excludedGroupNames) {
      Iterator it$ = this.parts.values().iterator();

      while(it$.hasNext()) {
         ModelRenderer part = (ModelRenderer)it$.next();
         boolean skipPart = false;
         String[] arr$ = excludedGroupNames;
         int len$ = excludedGroupNames.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            String excludedGroupName = arr$[i$];
            if (excludedGroupName.equalsIgnoreCase(part.boxName)) {
               skipPart = true;
            }
         }

         if (!skipPart) {
            part.render(1.0F);
         }
      }

   }
}
