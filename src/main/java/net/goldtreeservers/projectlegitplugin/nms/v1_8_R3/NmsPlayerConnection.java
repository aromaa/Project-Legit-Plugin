package net.goldtreeservers.projectlegitplugin.nms.v1_8_R3;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;

import lombok.experimental.Delegate;
import net.goldtreeservers.projectlegitplugin.data.PlayerInput;
import net.goldtreeservers.projectlegitplugin.data.PlayerInputData;
import net.goldtreeservers.projectlegitplugin.nms.INmsPlayerConnection;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import net.minecraft.server.v1_8_R3.PlayerConnection;

public class NmsPlayerConnection extends NmsPlayerConnectionBase implements INmsPlayerConnection
{
	public NmsPlayerConnection(PlayerConnection playerConnection)
	{
		super(playerConnection);
	}

	@Override
    public void a(PacketPlayInFlying packetplayinflying)
	{
    	//Skip it!
    }

	@Override
	public void handleInput(PlayerInputData input)
	{
		float forward = 0F;
		float strafe = 0F;
		
		if (input.getPlayerInput().contains(PlayerInput.FORWARD))
		{
			forward = 1F;
		}
		else if (input.getPlayerInput().contains(PlayerInput.BACKWARDS))
		{
			forward = -1F;
		}
		
		if (input.getPlayerInput().contains(PlayerInput.LEFT))
		{
			strafe = 1F;
		}
		else if (input.getPlayerInput().contains(PlayerInput.RIGHT))
		{
			strafe = -1F;
		}

		if (input.getPlayerInput().contains(PlayerInput.SNEAK))
		{
			forward *= 0.3F;
			strafe *= 0.3F;
		}

		this.player.yaw = input.getYaw();
		this.player.pitch = input.getPitch();

		this.player.i(input.getPlayerInput().contains(PlayerInput.JUMP));

		this.player.g(strafe, forward);
	}

	public static INmsPlayerConnection injectPlayer(org.bukkit.entity.Player player)
	{
		EntityPlayer nmsPlayer = ((CraftPlayer)player).getHandle();
		
		NmsPlayerConnection connection = new NmsPlayerConnection(nmsPlayer.playerConnection);
		
		nmsPlayer.playerConnection = connection;
		
		return connection;
	}

	@Override
	public org.bukkit.Location getLocation()
	{
		return this.getPlayer().getLocation();
	}
}

class NmsPlayerConnectionBase extends PlayerConnection
{
	@Delegate
	private final PlayerConnection playerConnection;

	NmsPlayerConnectionBase(PlayerConnection playerConnection)
	{
		super(playerConnection.player.server, playerConnection.networkManager, playerConnection.player);
		
		this.playerConnection = playerConnection;
	}
}