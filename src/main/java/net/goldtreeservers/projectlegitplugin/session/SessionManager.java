package net.goldtreeservers.projectlegitplugin.session;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import io.netty.channel.socket.DatagramPacket;
import net.goldtreeservers.projectlegitplugin.ProjectLegitPlugin;
import net.goldtreeservers.projectlegitplugin.nms.INmsPlayerConnection;
import net.goldtreeservers.projectlegitplugin.nms.NmsManager;
import net.goldtreeservers.projectlegitplugin.session.authenication.AuthenicationData;
import net.goldtreeservers.projectlegitplugin.utils.MetadataUtils;

public class SessionManager
{
	private static final String AUTHENICATION_TOKEN_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890!#%&/()=?½§@£$€{[]}";
	private static final int AUTHENICATION_TOKEN_LENGTH = 32;
	
	private final ProjectLegitPlugin plugin;
	
	private Cache<String, AuthenicationData> pendingSessions;
	private Map<InetSocketAddress, UdpSession> activeSessions;
	
	public SessionManager(ProjectLegitPlugin plugin)
	{
		this.plugin = plugin;
		
		this.pendingSessions = CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.SECONDS).build();
		this.activeSessions = new ConcurrentHashMap<>();
	}
	
	public String generateAuthenicationToken(UUID uniqueId, InetAddress address)
	{
		while (true)
		{
			String authenicationToken = this.generateRandomString();
			if (authenicationToken == null)
			{
				break;
			}
			
			//Make sure we are not overriding someones session just to be safe
			AuthenicationData data = this.pendingSessions.getIfPresent(authenicationToken);
			if (data == null)
			{
				this.pendingSessions.put(authenicationToken, new AuthenicationData(uniqueId, address));
				
				return authenicationToken;
			}
		}
		
		return null;
	}
	
	public void authenicateUdpConnection(String authenicationToken, DatagramPacket packet)
	{
		AuthenicationData data = this.pendingSessions.getIfPresent(authenicationToken);
		if (data != null)
		{
			this.pendingSessions.invalidate(authenicationToken);
			
			//Make sure its from same IP to be safe
			if (data.getAddress().equals(packet.sender().getAddress()))
			{
				Player player = this.plugin.getServer().getPlayer(data.getUniqueId());
				if (!player.hasMetadata(MetadataUtils.AUTHENICATION_TOKEN))
				{
					return;
				}
				
				player.getMetadata(MetadataUtils.AUTHENICATION_TOKEN).stream().filter((v) -> v.getOwningPlugin() == this.plugin).findFirst().ifPresent((v) ->
				{
					player.removeMetadata(MetadataUtils.AUTHENICATION_TOKEN, this.plugin);
					player.setMetadata(MetadataUtils.UDP_SESSION, new FixedMetadataValue(this.plugin, packet.sender()));
					
					player.sendPluginMessage(this.plugin, "ProjectLegitClient", new byte[] { (byte)1 });
					
					System.out.println("We have successfully authenicated udp protocol, inject bukkit");
					
					INmsPlayerConnection playerConnection = NmsManager.injectPlayer(player);

					this.activeSessions.put(packet.sender(), new UdpSession(data.getUniqueId(), packet.sender(), playerConnection));
					
					if (!player.isOnline())
					{
						this.terminate(player);
					}
				});
			}
		}
	}
	
	public void tick()
	{
		this.activeSessions.values().forEach((s) -> s.tick());
	}
	
	public UdpSession getSession(InetSocketAddress sender)
	{
		return this.activeSessions.get(sender);
	}
	
	private String generateRandomString()
	{
		try
		{
			SecureRandom random = SecureRandom.getInstanceStrong();
			
	        StringBuilder stringBuilder = new StringBuilder();
	        while (stringBuilder.length() < SessionManager.AUTHENICATION_TOKEN_LENGTH)
	        {
	            int index = (int)(random.nextFloat() * AUTHENICATION_TOKEN_CHARS.length());
	            
	            stringBuilder.append(AUTHENICATION_TOKEN_CHARS.charAt(index));
	        }
	        
	        return stringBuilder.toString();
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
			
			return null;
		}
	}

	public void terminate(Player player)
	{
		if (player.hasMetadata(MetadataUtils.AUTHENICATION_TOKEN))
		{
			player.getMetadata(MetadataUtils.AUTHENICATION_TOKEN).stream().filter((v) -> v.getOwningPlugin() == this.plugin).findFirst().ifPresent((v) ->
			{
				this.pendingSessions.invalidate(v.value());
			});
		}
		
		if (player.hasMetadata(MetadataUtils.UDP_SESSION))
		{
			player.getMetadata(MetadataUtils.UDP_SESSION).stream().filter((v) -> v.getOwningPlugin() == this.plugin).findFirst().ifPresent((v) ->
			{
				this.activeSessions.remove(v.value());
			});
		}
	}
}
