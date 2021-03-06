package com.creativemd.creativecore.common.packet;

import com.creativemd.creativecore.common.packet.PacketReciever.PacketKey;
import com.creativemd.creativecore.common.packet.PacketReciever.PacketValue;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SplittedPacketReceiver implements IMessageHandler<CreativeSplittedMessageHandler, IMessage> {
	
	@SideOnly(Side.CLIENT)
	public void executeClient(IMessage message) {
		if (message instanceof CreativeSplittedMessageHandler) {
			CreativeSplittedMessageHandler cm = (CreativeSplittedMessageHandler) message;
			
			PacketKey key = new PacketKey(cm.packetID, cm.uuid);
			PacketValue value = PacketReciever.clientSplittedPackets.get(key);
			
			if (value == null) {
				System.out.println("Something went wrong! Either a packet got lost or the receiving time has expired. " + key);
				return;
			}
			try {
				value.receivePacket(cm.buffer, 0, cm.length);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			
			if (cm.isLast) {
				if (value != null && value.isComplete()) {
					value.packet.readBytes(value.buf);
					
					Minecraft.getMinecraft().addScheduledTask(new Runnable() {
						
						@Override
						public void run() {
							value.packet.executeClient(Minecraft.getMinecraft().player);
						}
					});
					
					PacketReciever.clientSplittedPackets.remove(key);
				} else
					System.out.println("Something went wrong! Either a packet got lost or the receiving time has expired. " + key);
			}
			
			PacketReciever.refreshQueue(false);
			
		}
	}
	
	@Override
	public CreativeMessageHandler onMessage(CreativeSplittedMessageHandler message, MessageContext ctx) {
		if (ctx.side.isClient()) {
			executeClient(message);
		} else {
			if (message instanceof CreativeSplittedMessageHandler) {
				CreativeSplittedMessageHandler cm = (CreativeSplittedMessageHandler) message;
				PacketKey key = new PacketKey(cm.packetID, cm.uuid);
				PacketValue value = PacketReciever.splittedPackets.get(key);
				try {
					if (value == null) {
						System.out.println("Something went wrong! Either a packet got lost or the receiving time has expired. " + key);
						return null;
					}
					value.receivePacket(cm.buffer, 0, cm.length);
					
					if (cm.isLast) {
						if (value != null && value.isComplete()) {
							value.packet.readBytes(value.buf);
							
							ctx.getServerHandler().player.getServer().addScheduledTask(new Runnable() {
								
								@Override
								public void run() {
									value.packet.executeServer(ctx.getServerHandler().player);
								}
							});
							
							PacketReciever.splittedPackets.remove(key);
						} else
							System.out.println("Something went wrong! Either a packet got lost or the receiving time has expired. " + key);
					}
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
				PacketReciever.refreshQueue(true);
			}
		}
		return null;
	}
}
