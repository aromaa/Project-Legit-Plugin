package net.goldtreeservers.projectlegitplugin.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import net.goldtreeservers.projectlegitplugin.ProjectLegitPlugin;

public class PlayerListener implements Listener
{
	private final ProjectLegitPlugin plugin;
	
	public PlayerListener(ProjectLegitPlugin plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerQuitEvent(PlayerQuitEvent event)
	{
		this.plugin.getSessionManager().terminate(event.getPlayer());
	}
}
