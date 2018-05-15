package net.goldtreeservers.projectlegitplugin.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRegisterChannelEvent;
import org.bukkit.metadata.FixedMetadataValue;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.goldtreeservers.projectlegitplugin.ProjectLegitPlugin;
import net.goldtreeservers.projectlegitplugin.utils.ByteBufUtils;
import net.goldtreeservers.projectlegitplugin.utils.MetadataUtils;

public class PluginMessageListener implements Listener
{
	private final ProjectLegitPlugin plugin;
	
	public PluginMessageListener(ProjectLegitPlugin plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerRegisterChannelEvent(PlayerRegisterChannelEvent event)
	{
		String channel = event.getChannel();
		if (channel.equals("ProjectLegitClient"))
		{
			if (this.plugin.getUdpNetworkManager().getBoundTo() == null)
			{
				return;
			}
			
			Player player = event.getPlayer();
			
			String authenicationToken = this.plugin.getSessionManager().generateAuthenicationToken(player.getUniqueId(), player.getAddress().getAddress());
			if (authenicationToken == null)
			{
				return; //Oof
			}
			
			ByteBuf buf = Unpooled.buffer();
			buf.writeByte(0); //Id
			
			ByteBufUtils.writeVarInt(buf, 0); //Version
			
			ByteBufUtils.writeInetAddress(buf, this.plugin.getUdpNetworkManager().getBoundTo().getAddress());
			buf.writeShort(this.plugin.getUdpNetworkManager().getBoundTo().getPort());
			
			ByteBufUtils.writeString(buf, authenicationToken);
			
			byte[] bytes = new byte[buf.readableBytes()];
			buf.readBytes(bytes);
			
			player.sendPluginMessage(this.plugin, "ProjectLegitClient", bytes);
			player.setMetadata(MetadataUtils.AUTHENICATION_TOKEN, new FixedMetadataValue(this.plugin, authenicationToken));
		}
	}
}
