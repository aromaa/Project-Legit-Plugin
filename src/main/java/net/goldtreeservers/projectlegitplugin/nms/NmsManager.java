package net.goldtreeservers.projectlegitplugin.nms;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import lombok.Getter;

public class NmsManager
{
	@Getter private static Class<? extends INmsPlayerConnection> playerConnectionClass;
	private static Method playerConnectionInjectMethod;
	
	static
	{
		String bukkitClass = Bukkit.getServer().getClass().getPackage().getName();
		
		String version = bukkitClass.substring(bukkitClass.lastIndexOf('.') + 1);
		if(version.equalsIgnoreCase("v1_8_R3"))
		{
			NmsManager.playerConnectionClass = net.goldtreeservers.projectlegitplugin.nms.v1_8_R3.NmsPlayerConnection.class;
		}
		
		try
		{
			NmsManager.playerConnectionInjectMethod = NmsManager.playerConnectionClass.getDeclaredMethod("injectPlayer", Player.class);
		}
		catch (NoSuchMethodException | SecurityException e)
		{
			e.printStackTrace();
		}
	}
	
	public static INmsPlayerConnection injectPlayer(Player player)
	{
		try
		{
			return (INmsPlayerConnection)NmsManager.playerConnectionInjectMethod.invoke(null, player);
		} 
		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
		{
			throw new RuntimeException(e);
		}
	}
}
