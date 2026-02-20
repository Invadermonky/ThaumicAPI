package com.invadermonky.thaumicapi.network.messages;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.lib.events.HudHandler;
import thaumcraft.common.world.aura.AuraChunk;

public class MessageAuraToClient implements IMessage {
    public short base;
    public float vis;
    public float flux;

    public MessageAuraToClient(AuraChunk auraChunk) {
        this.base = auraChunk.getBase();
        this.vis = auraChunk.getVis();
        this.flux = auraChunk.getFlux();
    }

    public MessageAuraToClient() {
        this.base = 0;
        this.vis = 0;
        this.flux = 0;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.base = buf.readShort();
        this.vis = buf.readFloat();
        this.flux = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeShort(this.base);
        buf.writeFloat(this.vis);
        buf.writeFloat(this.flux);
    }

    public static class MessageHandler implements IMessageHandler<MessageAuraToClient, IMessage> {
        @SideOnly(Side.CLIENT)
        @Override
        public IMessage onMessage(MessageAuraToClient message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                HudHandler.currentAura = new AuraChunk(null, message.base, message.vis, message.flux);
            });
            return null;
        }
    }
}
