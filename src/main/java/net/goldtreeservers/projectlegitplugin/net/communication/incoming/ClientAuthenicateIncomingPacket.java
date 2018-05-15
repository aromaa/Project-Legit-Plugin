package net.goldtreeservers.projectlegitplugin.net.communication.incoming;

import io.netty.buffer.ByteBuf;
import io.netty.channel.socket.DatagramPacket;
import net.goldtreeservers.projectlegitplugin.ProjectLegitPlugin;
import net.goldtreeservers.projectlegitplugin.net.communication.IncomingPacket;
import net.goldtreeservers.projectlegitplugin.utils.ByteBufUtils;

public class ClientAuthenicateIncomingPacket implements IncomingPacket
{
	@Override
	public void handle(DatagramPacket msg)
	{
		ByteBuf content = msg.content();
		
		String authenicationToken = ByteBufUtils.readString(content);
		
		ProjectLegitPlugin.getPlugin().getSessionManager().authenicateUdpConnection(authenicationToken, msg);
	}
}
