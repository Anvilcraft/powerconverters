package covers1624.powerconverters.net;

import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;

import covers1624.powerconverters.PowerConverters;
import covers1624.powerconverters.util.LogHelper;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.FMLOutboundHandler.OutboundTarget;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;

// TODO: _____ ___  ____   ___     __        _______ _____ 
// TODO: |_   _/ _ \|  _ \ / _ \ _  \ \      / /_   _|  ___|
// TODO:   | || | | | | | | | | (_)  \ \ /\ / /  | | | |_   
// TODO:     | || |_| | |_| | |_| |_    \ V  V /   | | |  _|  
// TODO:   |_| \___/|____/ \___/(_)    \_/\_/    |_| |_|

@Sharable
public class PacketPipeline extends MessageToMessageCodec<FMLProxyPacket, AbstractPacket> {
    private EnumMap channels;
    private LinkedList packets = new LinkedList();
    private boolean isPostInitialised = false;
    public static final PacketPipeline INSTANCE = new PacketPipeline();

    public boolean registerPacket(Class clazz) {
        if (this.packets.size() > 256) {
            LogHelper.fatal("Failed to register packet, The array is full.");
            return false;
        } else if (this.packets.contains(clazz)) {
            LogHelper.fatal("Packet Allready Registered: " + clazz.toString());
            return false;
        } else if (this.isPostInitialised) {
            LogHelper.fatal(
                    "Already started PostInit, You are doing it wrong, Add Packets any time before PostInit.");
            return false;
        } else {
            this.packets.add(clazz);
            return true;
        }
    }

    protected void encode(ChannelHandlerContext ctx, AbstractPacket msg, List out)
            throws Exception {
        ByteBuf buffer = Unpooled.buffer();
        Class clazz = msg.getClass();
        if (!this.packets.contains(msg.getClass())) {
            throw new NullPointerException(
                    "No Packet Registered for: " + msg.getClass().getCanonicalName());
        } else {
            byte discriminator = (byte) this.packets.indexOf(clazz);
            buffer.writeByte(discriminator);
            msg.encodeInto(ctx, buffer);
            FMLProxyPacket proxyPacket = new FMLProxyPacket(
                    buffer.copy(),
                    (String) ctx.channel().attr(NetworkRegistry.FML_CHANNEL).get());
            out.add(proxyPacket);
        }
    }

    protected void decode(ChannelHandlerContext ctx, FMLProxyPacket msg, List out)
            throws Exception {
        ByteBuf payload = msg.payload();
        byte discriminator = payload.readByte();
        Class clazz = (Class) this.packets.get(discriminator);
        if (clazz == null) {
            throw new NullPointerException(
                    "No packet registered for discriminator: " + discriminator);
        } else {
            AbstractPacket pkt = (AbstractPacket) clazz.newInstance();
            pkt.decodeInto(ctx, payload.slice());
            switch (FMLCommonHandler.instance().getEffectiveSide()) {
                case CLIENT:
                    EntityPlayer player = this.getClientPlayer();
                    pkt.handleClientSide(player);
                    break;
                case SERVER:
                    INetHandler netHandler = (INetHandler) ctx.channel()
                            .attr(NetworkRegistry.NET_HANDLER)
                            .get();
                    player = ((NetHandlerPlayServer) netHandler).playerEntity;
                    pkt.handleServerSide(player);
            }
        }
    }

    public void initalise() {
        this.channels = NetworkRegistry.INSTANCE.newChannel(
                "Power Converters 3", new ChannelHandler[] { this });
    }

    public void postInitialise() {
        if (!this.isPostInitialised) {
            this.isPostInitialised = true;
            Collections.sort(this.packets, new Comparator<Class<?>>() {
                public int compare(Class clazz1, Class clazz2) {
                    int com = String.CASE_INSENSITIVE_ORDER.compare(
                            clazz1.getCanonicalName(), clazz2.getCanonicalName());
                    if (com == 0) {
                        com = clazz1.getCanonicalName().compareTo(clazz2.getCanonicalName());
                    }

                    return com;
                }
            });
        }
    }

    @SideOnly(Side.CLIENT)
    private EntityPlayer getClientPlayer() {
        return PowerConverters.proxy.getClientPlayer();
    }

    public void sendToAll(AbstractPacket message) {
        ((FMLEmbeddedChannel) this.channels.get(Side.SERVER))
                .attr(FMLOutboundHandler.FML_MESSAGETARGET)
                .set(OutboundTarget.ALL);
        ((FMLEmbeddedChannel) this.channels.get(Side.SERVER)).writeAndFlush(message);
    }

    public void sendTo(AbstractPacket message, EntityPlayerMP player) {
        ((FMLEmbeddedChannel) this.channels.get(Side.SERVER))
                .attr(FMLOutboundHandler.FML_MESSAGETARGET)
                .set(OutboundTarget.PLAYER);
        ((FMLEmbeddedChannel) this.channels.get(Side.SERVER))
                .attr(FMLOutboundHandler.FML_MESSAGETARGETARGS)
                .set(player);
        ((FMLEmbeddedChannel) this.channels.get(Side.SERVER)).writeAndFlush(message);
    }

    public void sendToAllAround(AbstractPacket message, NetworkRegistry.TargetPoint point) {
        ((FMLEmbeddedChannel) this.channels.get(Side.SERVER))
                .attr(FMLOutboundHandler.FML_MESSAGETARGET)
                .set(OutboundTarget.ALLAROUNDPOINT);
        ((FMLEmbeddedChannel) this.channels.get(Side.SERVER))
                .attr(FMLOutboundHandler.FML_MESSAGETARGETARGS)
                .set(point);
        ((FMLEmbeddedChannel) this.channels.get(Side.SERVER)).writeAndFlush(message);
    }

    public void sendToDimension(AbstractPacket message, int dimensionId) {
        ((FMLEmbeddedChannel) this.channels.get(Side.SERVER))
                .attr(FMLOutboundHandler.FML_MESSAGETARGET)
                .set(OutboundTarget.DIMENSION);
        ((FMLEmbeddedChannel) this.channels.get(Side.SERVER))
                .attr(FMLOutboundHandler.FML_MESSAGETARGETARGS)
                .set(dimensionId);
        ((FMLEmbeddedChannel) this.channels.get(Side.SERVER)).writeAndFlush(message);
    }

    public void sendToServer(AbstractPacket message) {
        ((FMLEmbeddedChannel) this.channels.get(Side.CLIENT))
                .attr(FMLOutboundHandler.FML_MESSAGETARGET)
                .set(OutboundTarget.TOSERVER);
        ((FMLEmbeddedChannel) this.channels.get(Side.CLIENT)).writeAndFlush(message);
    }
}
