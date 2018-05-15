package net.goldtreeservers.projectlegitplugin.net;

import java.net.InetSocketAddress;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Getter;
import net.goldtreeservers.projectlegitplugin.net.communication.OutgoingPacket;
import net.goldtreeservers.projectlegitplugin.utils.NettyUtils;

public class UdpNetworkManager
{
	private EventLoopGroup bossGroup;
	
	@Getter private UdpPacketManager packetManager;
	
	private Channel channel;
	@Getter private InetSocketAddress boundTo;
	
	public UdpNetworkManager()
	{
		this.bossGroup = NettyUtils.createEventLoopGroup();
		
		this.packetManager = new UdpPacketManager();
	}
	
	public void start(String ip)
	{
		Bootstrap boostrap = new Bootstrap()
			.group(this.bossGroup)
			.channel(NettyUtils.getDatagramChannel())
			.option(ChannelOption.SO_BROADCAST, true)
			.handler(new UdpPacketHandler(this));
		
		boostrap.bind(ip, 0 /* Bind to free port */).addListener(new GenericFutureListener<ChannelFuture>()
		{
			@Override
			public void operationComplete(ChannelFuture future) throws Exception
			{
				Channel channel = future.channel();
				InetSocketAddress localAddress = (InetSocketAddress)channel.localAddress();
				
				UdpNetworkManager.this.channel = channel;
				UdpNetworkManager.this.boundTo = localAddress;
				
				System.out.println("Authoractive UDP server was bound to " + localAddress.getHostString() + ":" + localAddress.getPort());
			}
		}).syncUninterruptibly();
	}
	
	public void sendPacketUnreliable(OutgoingPacket packet, InetSocketAddress receiver)
	{
		ByteBuf buf = Unpooled.buffer();
		buf.writeByte(0); //Packet type
		buf.writeBytes(packet.getBytes());
		
		this.channel.writeAndFlush(new DatagramPacket(buf, receiver));
	}
	
	public void stop()
	{
		this.boundTo = null;
		
		this.bossGroup.shutdownGracefully();
	}
}
