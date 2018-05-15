package net.goldtreeservers.projectlegitplugin.net;

import java.util.HashMap;
import java.util.Map;

import net.goldtreeservers.projectlegitplugin.net.communication.IncomingPacket;
import net.goldtreeservers.projectlegitplugin.net.communication.incoming.ClientAuthenicateIncomingPacket;
import net.goldtreeservers.projectlegitplugin.net.communication.incoming.ClientInputIncomingPacket;

public class UdpPacketManager
{
	private Map<Byte, IncomingPacket> incomingPackets;
	
	public UdpPacketManager()
	{
		this.incomingPackets = new HashMap<>();
		this.incomingPackets.put((byte) 0, new ClientAuthenicateIncomingPacket());
		this.incomingPackets.put((byte) 1, new ClientInputIncomingPacket());
	}
	
	public IncomingPacket getIncomingPacket(byte packetId)
	{
		return this.incomingPackets.get(packetId);
	}
}
