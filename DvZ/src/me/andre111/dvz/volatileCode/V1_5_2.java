package me.andre111.dvz.volatileCode;

import java.io.File;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_5_R2.CraftServer;
import org.bukkit.craftbukkit.v1_5_R2.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import net.minecraft.server.v1_5_R2.MinecraftServer;
import net.minecraft.server.v1_5_R2.RegionFile;
import net.minecraft.server.v1_5_R2.RegionFileCache;
import net.minecraft.server.v1_5_R2.WorldData;
import net.minecraft.server.v1_5_R2.WorldServer;

public class V1_5_2 extends VolatileCodeHandler {
	
	public V1_5_2(Plugin plugin) {
		super(plugin);
	}

	public void changeSeed(World w, long newSeed) {
		CraftWorld cw = (CraftWorld) w;
		WorldServer worlds = cw.getHandle();
		WorldData data = worlds.worldData;
		try {
			Field f = data.getClass().getDeclaredField("seed");
			f.setAccessible(true);
			f.setLong(data, newSeed);
			f.setAccessible(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		 
		w.save();
	}
	
	@SuppressWarnings("rawtypes")
	protected HashMap regionfiles;
	protected Field rafField;
	
	@SuppressWarnings("rawtypes")
	public void bindRegionFiles()
	{
		try
		{
			Field a = RegionFileCache.class.getDeclaredField("a"); // obfuscated - regionsByFilename in RegionFileCache
			a.setAccessible(true);
			regionfiles = (HashMap) a.get(null);
			rafField = RegionFile.class.getDeclaredField("c"); // obfuscated - dataFile in RegionFile
			rafField.setAccessible(true);
			plugin.getLogger().info("Successfully bound to region file cache.");
		}
		catch (Throwable t)
		{
			plugin.getLogger().warning("Error binding to region file cache.");
			t.printStackTrace();
		}
	}

	public void unbindRegionFiles()
	{
		regionfiles = null;
		rafField = null;
	}

	@SuppressWarnings("rawtypes")
	public synchronized boolean clearWorldReference(String worldName)
	{
		if (regionfiles == null) return false;
		if (rafField == null) return false;

		ArrayList<Object> removedKeys = new ArrayList<Object>();
		try
		{
			for (Object o : regionfiles.entrySet())
			{
				Map.Entry e = (Map.Entry) o;
				File f = (File) e.getKey();

				if (f.toString().startsWith("." + File.separator + worldName))
				{
					RegionFile file = (RegionFile) e.getValue();
					try
					{
						RandomAccessFile raf = (RandomAccessFile) rafField.get(file);
						raf.close();
						removedKeys.add(f);
					}
					catch (Exception ex)
					{
						ex.printStackTrace();
					}
				}
			}
		}
		catch (Exception ex)
		{
			plugin.getLogger().warning("Exception while removing world reference for '" + worldName + "'!");
			ex.printStackTrace();
		}
		for (Object key : removedKeys)
			regionfiles.remove(key);

		return true;
	}

	public void forceUnloadWorld(World world)
	{
		world.setAutoSave(false);
		for ( Player player : world.getPlayers() )
			player.kickPlayer("World is being deleted... and you were in it!");

		// formerly used server.unloadWorld at this point. But it was sometimes failing, even when I force-cleared the player list
		CraftServer server = (CraftServer)Bukkit.getServer();
		CraftWorld craftWorld = (CraftWorld)world;

		try
		{
			Field f = server.getClass().getDeclaredField("worlds");
			f.setAccessible(true);
			@SuppressWarnings("unchecked")
			Map<String, World> worlds = (Map<String, World>)f.get(server);
			worlds.remove(world.getName().toLowerCase());
			f.setAccessible(false);
		}
		catch ( IllegalAccessException ex )
		{
		}
		catch  ( NoSuchFieldException ex )
		{
		}

		MinecraftServer ms = getMinecraftServer();
		ms.worlds.remove(ms.worlds.indexOf(craftWorld.getHandle()));
	}
	
	protected MinecraftServer getMinecraftServer()
	{
		CraftServer server = (CraftServer)Bukkit.getServer();
		return server.getServer();
	}
}
