package net.goldtreeservers.projectlegitplugin.net.communication;

import io.netty.buffer.ByteBuf;

public interface OutgoingPacket
{
	ByteBuf getBytes();
}
