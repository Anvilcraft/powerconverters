package covers1624.powerconverters.net;

import covers1624.powerconverters.PowerConverters;
import covers1624.powerconverters.api.bridge.BridgeSideData;
import covers1624.powerconverters.tile.main.TileEntityEnergyBridge;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class EnergyBridgeSyncPacket extends AbstractPacket {
    private NBTTagCompound tagCompound;

    public EnergyBridgeSyncPacket() {}

    public EnergyBridgeSyncPacket(NBTTagCompound tag, int x, int y, int z) {
        this.tagCompound = tag;
        this.tagCompound.setInteger("X", x);
        this.tagCompound.setInteger("Y", y);
        this.tagCompound.setInteger("Z", z);
    }

    public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
        ByteBufUtils.writeTag(buffer, this.tagCompound);
    }

    public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
        this.tagCompound = ByteBufUtils.readTag(buffer);
    }

    public void handleClientSide(EntityPlayer player) {
        int x = this.tagCompound.getInteger("X");
        int y = this.tagCompound.getInteger("Y");
        int z = this.tagCompound.getInteger("Z");
        TileEntity tileEntity
            = PowerConverters.proxy.getClientWorld().getTileEntity(x, y, z);
        if (!(tileEntity instanceof TileEntityEnergyBridge)) {
            throw new RuntimeException(
                String.format("Tile @ %s, %s, %s, Does not match the server.", x, y, z)
            );
        } else {
            TileEntityEnergyBridge energyBridge = (TileEntityEnergyBridge) tileEntity;
            ForgeDirection[] arr$ = ForgeDirection.VALID_DIRECTIONS;
            int len$ = arr$.length;

            for (int i$ = 0; i$ < len$; ++i$) {
                ForgeDirection dir = arr$[i$];
                NBTTagCompound tag
                    = this.tagCompound.getCompoundTag(String.valueOf(dir.ordinal()));
                BridgeSideData data = new BridgeSideData();
                data.loadFromNBT(tag);
                energyBridge.setClientDataForSide(dir, data);
            }

            energyBridge.setIsInputLimited(this.tagCompound.getBoolean("InputLimited"));
            energyBridge.setEnergyScaled(this.tagCompound.getDouble("Energy"));
        }
    }

    public void handleServerSide(EntityPlayer player) {}
}
