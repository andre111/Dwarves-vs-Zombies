package me.andre111.dvz.volatileCode;

import java.io.File;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import me.andre111.dvz.DvZ;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;

public class DynamicClassFunctions {
	
	public static String nmsPackage = "net.minecraft.server.v1_5_R2";
	public static String obcPackage = "org.bukkit.craftbukkit.v1_5_R2";
	
	public static boolean setPackages() {
		Server craftServer = Bukkit.getServer();
		if (craftServer != null) {
			try {
				Class<?> craftClass = craftServer.getClass();
				Method getHandle = craftClass.getMethod("getHandle");
				Class<?> returnType = getHandle.getReturnType();

				obcPackage = craftClass.getPackage().getName();
				nmsPackage = returnType.getPackage().getName();
				return true;
			} catch (Exception e) {
			}
		}
		return false;
	}
	
	public static HashMap<String, Class<?>> classes = new HashMap<String, Class<?>>();
	public static boolean setClasses() {
		try {
			// org.bukkit.craftbukkit
			classes.put("CraftServer", Class.forName(obcPackage + ".CraftServer"));
			classes.put("CraftWorld", Class.forName(obcPackage + ".CraftWorld"));
			classes.put("CraftFallingSand", Class.forName(obcPackage + ".entity.CraftFallingSand"));
			
			// net.minecraft.server
			classes.put("MinecraftServer", Class.forName(nmsPackage + ".MinecraftServer"));
			classes.put("RegionFile", Class.forName(nmsPackage + ".RegionFile"));
			classes.put("RegionFileCache", Class.forName(nmsPackage + ".RegionFileCache"));
			classes.put("WorldData", Class.forName(nmsPackage + ".WorldData"));
			classes.put("WorldServer", Class.forName(nmsPackage + ".WorldServer"));
			classes.put("EntityFallingBlock", Class.forName(nmsPackage + ".EntityFallingBlock"));
			
			return true;
		} catch (Exception e) {
			DvZ.logger.log(Level.SEVERE, "Could not aquire a required class", e);
			return false;
		}
	}
	
	public static HashMap<String, Method> methods = new HashMap<String, Method>();
	public static boolean setMethods() {
		try {
			// org.bukkit.craftbukkit
			methods.put("CraftWorld.getHandle()", classes.get("CraftWorld").getDeclaredMethod("getHandle"));
			methods.put("CraftServer.getServer()", classes.get("CraftServer").getDeclaredMethod("getServer"));
			methods.put("CraftFallingSand.getHandle()", classes.get("CraftFallingSand").getDeclaredMethod("getHandle"));
			
			return true;
		} catch (Exception e) {
			DvZ.logger.log(Level.SEVERE, "Could not find a required method", e);
			return false;
		}
	}
	
	public static HashMap<String, Field> fields = new HashMap<String, Field>();
	public static boolean setFields() {
		try {
			fields.put("RegionFileCache.regionsByFilename", classes.get("RegionFileCache").getDeclaredField("a")); 		// obfuscated - regionsByFilename in RegionFileCache
			fields.put("RegionFile.dataFile", classes.get("RegionFile").getDeclaredField("c"));							// obfuscated - dataFile in RegionFile
			fields.put("WorldData.seed", classes.get("WorldData").getDeclaredField("seed"));
			
			fields.put("EntityFallingBlock.hurtEntities", classes.get("EntityFallingBlock").getDeclaredField("hurtEntities"));
			fields.put("EntityFallingBlock.fallHurtAmount", classes.get("EntityFallingBlock").getDeclaredField("fallHurtAmount"));
			fields.put("EntityFallingBlock.fallHurtMax", classes.get("EntityFallingBlock").getDeclaredField("fallHurtMax"));
			
			fields.put("MinecraftServer.worlds", classes.get("MinecraftServer").getDeclaredField("worlds"));
			
			fields.put("CraftServer.worlds", classes.get("CraftServer").getDeclaredField("worlds"));
			//fields.put("RegionFile.dataFile", classes.get("RegionFile").getDeclaredField("c"));
			return true;
		} catch (Exception e) {
			DvZ.logger.log(Level.SEVERE, "Could not find a field class", e);
			return false;
		}
	}
	
	//methods
	@SuppressWarnings("rawtypes")
	protected static HashMap regionfiles;
	protected static Field rafField;
	
	@SuppressWarnings("rawtypes")
	public static void bindRegionFiles()
	{
		try
		{
			fields.get("RegionFileCache.regionsByFilename").setAccessible(true);
			regionfiles = (HashMap) fields.get("RegionFileCache.regionsByFilename").get(null);
			rafField = fields.get("RegionFile.dataFile");
			rafField.setAccessible(true);
			DvZ.logger.info("Successfully bound to region file cache.");
		}
		catch (Throwable t)
		{
			DvZ.logger.warning("Error binding to region file cache.");
			t.printStackTrace();
		}
	}

	public static void unbindRegionFiles()
	{
		regionfiles = null;
		rafField = null;
	}
	
	@SuppressWarnings("rawtypes")
	public static synchronized boolean clearWorldReference(String worldName)
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
					try
					{
						RandomAccessFile raf = (RandomAccessFile) rafField.get(e.getValue());
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
			DvZ.logger.warning("Exception while removing world reference for '" + worldName + "'!");
			ex.printStackTrace();
		}
		for (Object key : removedKeys)
			regionfiles.remove(key);

		return true;
	}
	
	@SuppressWarnings("unchecked")
	public static void forceUnloadWorld(World world)
	{
		world.setAutoSave(false);
		for ( Player player : world.getPlayers() )
			player.kickPlayer("World is being deleted... and you were in it!");

		// formerly used server.unloadWorld at this point. But it was sometimes failing, even when I force-cleared the player list
		
		try
		{
			Field f = fields.get("CraftServer.worlds");
			f.setAccessible(true);
			Map<String, World> worlds = (Map<String, World>)f.get(Bukkit.getServer());
			worlds.remove(world.getName().toLowerCase());
			f.setAccessible(false);
		} catch ( IllegalAccessException ex ) {
		}

		Object ms = getMinecraftServer();

		List<Object> worldList;
		try {
			worldList = (List<Object>) fields.get("MinecraftServer.worlds").get(ms);
			
			worldList.remove(worldList.indexOf(methods.get("CraftWorld.getHandle()").invoke(world)));
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		}
	}
	
	protected static Object getMinecraftServer()
	{
		try {
			return methods.get("CraftServer.getServer()").invoke(Bukkit.getServer());
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		}
		
		return null;
	}

	public static void setFallingBlockHurtEntities(FallingBlock block, float damage, int max) {
		try {
			Object efb = methods.get("CraftFallingSand.getHandle()").invoke(block);
			
			Field field = fields.get("EntityFallingBlock.hurtEntities");
			field.setAccessible(true);
			field.setBoolean(efb, true);
			
			field = fields.get("EntityFallingBlock.fallHurtAmount");
			field.setAccessible(true);
			field.setFloat(efb, damage);

			field = fields.get("EntityFallingBlock.fallHurtMax");
			field.setAccessible(true);
			field.setInt(efb, max);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
