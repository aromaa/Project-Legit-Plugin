package net.goldtreeservers.projectlegitplugin;

import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import net.goldtreeservers.projectlegitplugin.listeners.PlayerListener;
import net.goldtreeservers.projectlegitplugin.listeners.PluginMessageListener;
import net.goldtreeservers.projectlegitplugin.net.UdpNetworkManager;
import net.goldtreeservers.projectlegitplugin.runnables.TickRunnable;
import net.goldtreeservers.projectlegitplugin.session.SessionManager;

public class ProjectLegitPlugin extends JavaPlugin
{
	@Getter private static ProjectLegitPlugin plugin;
	
	@Getter private SessionManager sessionManager;
	@Getter private UdpNetworkManager udpNetworkManager;
	
	public ProjectLegitPlugin()
	{
		ProjectLegitPlugin.plugin = this;
	}
	
	@Override
	public void onEnable()
	{
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "ProjectLegitClient");
		
		this.getServer().getPluginManager().registerEvents(new PluginMessageListener(this), this);
		this.getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
		
		this.sessionManager = new SessionManager(this);
		
		this.udpNetworkManager = new UdpNetworkManager();
		this.udpNetworkManager.start(this.getServer().getIp());
		
		this.getServer().getScheduler().runTaskTimer(this, new TickRunnable(this), 1L, 1L);
	}
	
	@Override
	public void onDisable()
	{
		this.udpNetworkManager.stop();
	}
}
