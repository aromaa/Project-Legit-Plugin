package net.goldtreeservers.projectlegitplugin.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.bukkit.util.Vector;

import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;

public class ByteBufUtils
{
	public static void writeInetAddress(ByteBuf buf, InetAddress address)
	{
		ByteBufUtils.writeBytes(buf, address.getAddress());
	}
	
	public static void writeBytes(ByteBuf buf, byte[] bytes)
	{
		ByteBufUtils.writeVarInt(buf, bytes.length);
		buf.writeBytes(bytes);
	}
	
	public static void writeVarInt(ByteBuf buf, int value)
    {
        while (true)
        {
            int part = value & 0x7F;

            value >>>= 7;
            if (value != 0)
            {
                part |= 0x80;
            }

            buf.writeByte(part);

            if (value == 0)
            {
                break;
            }
        }
    }
	
	public static void writeString(ByteBuf buf, String value)
	{
		ByteBufUtils.writeBytes(buf, value.getBytes(CharsetUtil.UTF_8));
	}
	
	public static void writeVector(ByteBuf buf, Vector position)
	{
		buf.writeDouble(position.getX());
		buf.writeDouble(position.getY());
		buf.writeDouble(position.getZ());
	}
	
	public static InetAddress readInetAddress(ByteBuf buf)
	{
		try
		{
			return InetAddress.getByAddress(ByteBufUtils.readBytes(buf));
		}
		catch (UnknownHostException e)
		{
			return null;
		}
	}
	
	public static byte[] readBytes(ByteBuf buf)
	{
		int length = ByteBufUtils.readVarInt(buf);
		
		byte[] bytes = new byte[length];
		buf.readBytes(bytes);
		
		return bytes;
	}
	
    public static int readVarInt(ByteBuf buf)
    {
        return ByteBufUtils.readVarInt(buf, 5);
    }

    public static int readVarInt(ByteBuf buf, int maxBytes)
    {
        int out = 0;
        int bytes = 0;

        while (true)
        {
            byte in = buf.readByte();

            out |= (in & 0x7F) << (bytes++ * 7);

            if (bytes > maxBytes)
            {
                throw new RuntimeException("VarInt is too big");
            }

            if ((in & 0x80) != 0x80)
            {
                break;
            }
        }

        return out;
    }
    
    public static String readString(ByteBuf buf)
    {
    	return new String(ByteBufUtils.readBytes(buf), CharsetUtil.UTF_8);
    }
    
    public static Vector readVector(ByteBuf buf)
    {
    	return new Vector(buf.readDouble(), buf.readDouble(), buf.readDouble());
    }
}
