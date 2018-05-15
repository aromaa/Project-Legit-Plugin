package net.goldtreeservers.projectlegitplugin.net.communication.outgoing;

import org.bukkit.util.Vector;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.AllArgsConstructor;
import net.goldtreeservers.projectlegitplugin.net.communication.OutgoingPacket;
import net.goldtreeservers.projectlegitplugin.utils.ByteBufUtils;

@AllArgsConstructor
public class ServerAckOutgoingPacket implements OutgoingPacket
{
	private static final byte HEADER_ID = 0;
	
	private byte id;
	
	private Vector position;
	private float yaw;
	private float pitch;
	
	@Override
	public ByteBuf getBytes()
	{
		ByteBuf buf = Unpooled.buffer();
		buf.writeByte(ServerAckOutgoingPacket.HEADER_ID);
		
		buf.writeByte(this.id);
		
		ByteBufUtils.writeVector(buf, this.position);
		buf.writeFloat(this.yaw);
		buf.writeFloat(this.pitch);
		
		return buf;
	}
}
