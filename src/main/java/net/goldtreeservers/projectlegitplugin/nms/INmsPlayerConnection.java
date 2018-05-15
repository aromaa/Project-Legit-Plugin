package net.goldtreeservers.projectlegitplugin.nms;

import org.bukkit.Location;

import net.goldtreeservers.projectlegitplugin.data.PlayerInputData;

public interface INmsPlayerConnection
{
	public void handleInput(PlayerInputData input);

	public Location getLocation();
}
