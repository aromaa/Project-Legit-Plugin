package net.goldtreeservers.projectlegitplugin.session;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;

import lombok.Getter;
import net.goldtreeservers.projectlegitplugin.ProjectLegitPlugin;
import net.goldtreeservers.projectlegitplugin.data.PlayerInputData;
import net.goldtreeservers.projectlegitplugin.net.communication.outgoing.ServerAckOutgoingPacket;
import net.goldtreeservers.projectlegitplugin.nms.INmsPlayerConnection;

public class UdpSession
{
	@Getter private final UUID uniqueId;
	@Getter private final InetSocketAddress address;
	@Getter private final INmsPlayerConnection playerConnection;
	
	@Getter private byte nextId;
	private byte nextWantedId;
	private Map<Byte, PlayerInputData> inputs;
	
	private boolean ranOnce;
	
	@Getter private byte lastAck;
	
	public UdpSession(UUID uniqueId, InetSocketAddress address, INmsPlayerConnection playerConnection)
	{
		this.uniqueId = uniqueId;
		this.address = address;
		this.playerConnection = playerConnection;
		
		this.inputs = new HashMap<>();
	}
	
	public void tick()
	{
		if ((byte)(this.nextWantedId + 1) != this.nextId)
		{
			this.nextWantedId++;
		}
		
		while (this.nextWantedId != this.nextId)
		{
			PlayerInputData input = this.inputs.remove(this.nextId);
			if (input != null)
			{
				this.nextId++;
				
				if (!this.ranOnce)
				{
					this.ranOnce = true;
				}
				
				this.playerConnection.handleInput(input);
			}
			else
			{
				break;
			}
		}
		
		if (!this.ranOnce)
		{
			return;
		}
		
		Location location = this.playerConnection.getLocation();
		
		ProjectLegitPlugin.getPlugin().getUdpNetworkManager().sendPacketUnreliable(new ServerAckOutgoingPacket((byte)(this.nextId - 1), location.toVector(), location.getYaw(), location.getPitch()), this.address);
	}

	public void addInputs(List<PlayerInputData> inputs)
	{
		for(PlayerInputData input : inputs)
		{
			if (input.getId() == this.lastAck)
			{
				this.inputs.put(this.lastAck, input);
				
				this.lastAck++;
			}
		}
	}
}
