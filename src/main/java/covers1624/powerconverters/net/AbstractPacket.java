package covers1624.powerconverters.net;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;

public abstract class AbstractPacket {
    public abstract void encodeInto(ChannelHandlerContext var1, ByteBuf var2);

    public abstract void decodeInto(ChannelHandlerContext var1, ByteBuf var2);

    public abstract void handleClientSide(EntityPlayer var1);

    public abstract void handleServerSide(EntityPlayer var1);
}
