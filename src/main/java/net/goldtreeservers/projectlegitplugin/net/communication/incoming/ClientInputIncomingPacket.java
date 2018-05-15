package net.goldtreeservers.projectlegitplugin.net.communication.incoming;

import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.socket.DatagramPacket;
import net.goldtreeservers.projectlegitplugin.ProjectLegitPlugin;
import net.goldtreeservers.projectlegitplugin.data.PlayerInputData;
import net.goldtreeservers.projectlegitplugin.net.communication.IncomingPacket;
import net.goldtreeservers.projectlegitplugin.session.UdpSession;

public class ClientInputIncomingPacket implements IncomingPacket
{
	@Override
	public void handle(DatagramPacket msg)
	{
		UdpSession session = ProjectLegitPlugin.getPlugin().getSessionManager().getSession(msg.sender());
		if (session != null)
		{
			ByteBuf buf = msg.content();
			
			List<PlayerInputData> inputs = new ArrayList<>();
			
			PlayerInputData oldInput = null;
			while (buf.readableBytes() > 0)
			{
				inputs.add(oldInput = PlayerInputData.fromBytes(buf, oldInput));
			}
		
			session.addInputs(inputs);
		}
	}
}
