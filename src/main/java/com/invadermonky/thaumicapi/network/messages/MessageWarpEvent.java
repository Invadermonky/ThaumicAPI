package com.invadermonky.thaumicapi.network.messages;

import com.invadermonky.thaumicapi.api.warpevent.IWarpEvent;
import com.invadermonky.thaumicapi.warpevents.WarpEventRegistry;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MessageWarpEvent implements IMessage {
    public IWarpEvent warpEvent;
    public int playerWarp;

    public MessageWarpEvent(IWarpEvent warpEvent, int playerWarp) {
        this.warpEvent = warpEvent;
        this.playerWarp = playerWarp;
    }

    public MessageWarpEvent() {
        this(null, 0);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.warpEvent = WarpEventRegistry.getWarpEvent(ByteBufUtils.readUTF8String(buf));
        this.playerWarp = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, this.warpEvent.getEventName().toString());
        buf.writeInt(this.playerWarp);
    }

    public static class MessageHandler implements IMessageHandler<MessageWarpEvent, IMessage> {
        @SideOnly(Side.CLIENT)
        @Override
        public IMessage onMessage(MessageWarpEvent message, MessageContext ctx) {
            if(message.warpEvent != null) {
                Minecraft.getMinecraft().addScheduledTask(() -> {
                    EntityPlayer player = Minecraft.getMinecraft().player;
                    message.warpEvent.playClientEventSound(player, message.playerWarp);
                    message.warpEvent.performWarpEvent(player, message.playerWarp);
                });
            }
            return null;
        }
    }
}
