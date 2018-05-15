package net.goldtreeservers.projectlegitplugin.runnables;

import net.goldtreeservers.projectlegitplugin.ProjectLegitPlugin;

public class TickRunnable implements Runnable
{
	private ProjectLegitPlugin plugin;
	
	public TickRunnable(ProjectLegitPlugin plugin)
	{
		this.plugin = plugin;
	}
	
	@Override
	public void run()
	{
		this.plugin.getSessionManager().tick();
	}
}
