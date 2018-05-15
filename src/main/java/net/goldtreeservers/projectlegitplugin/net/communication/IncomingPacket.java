package net.goldtreeservers.projectlegitplugin.net.communication;

import io.netty.channel.socket.DatagramPacket;

public interface IncomingPacket
{
	void handle(DatagramPacket msg);
}
