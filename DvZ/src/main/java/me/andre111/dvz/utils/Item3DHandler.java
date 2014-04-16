package me.andre111.dvz.utils;

import java.util.ArrayList;
import java.util.HashMap;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.disguise.DisguiseSystemHandler;
import me.andre111.dvz.volatileCode.DvZPackets;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Item3DHandler {
	private ArrayList<Integer> entityIDs;
	private HashMap<Integer, Item3DInfo> entityInfo;
	private HashMap<Integer, Integer> standIDs;
	private HashMap<Integer, Item3DRunnable> actions;
	
	private int standCounter;
	
	public Item3DHandler(DvZ plugin) {
		entityIDs = new ArrayList<Integer>();
		entityInfo = new HashMap<Integer, Item3DInfo>();
		standIDs = new HashMap<Integer, Integer>();
		actions = new HashMap<Integer, Item3DRunnable>();
		
		standCounter = 0;
	}
	
	//Spawn a clickable "3d-Item" around the block
	public void spawnAroundBlock(Player player, Location loc, int itemID, Item3DRunnable run) {
		int centerx = (int) Math.floor(loc.getX());
		int centery = (int) Math.floor(loc.getY());
		int centerz = (int) Math.floor(loc.getZ());
		
		//-----------
		//-----------
		
		int x = centerx * 32 + 32 + 12;
		int y = centery * 32;
		int z = centerz * 32;
		
		byte rotation = (byte) (0);
		
		spawnZombie(player, loc.getWorld().getName(), x, y, z, rotation, itemID, standCounter);
		
		//-----------
		
		x = (int) centerx * 32 + 32;
		y = (int) centery * 32;
		z = (int) centerz * 32 + 32 + 12;
		
		rotation = (byte) (256/4*1);
		
		spawnZombie(player, loc.getWorld().getName(), x, y, z, rotation, itemID, standCounter);
		
		//-----------
		
		x = (int) centerx * 32 - 12;
		y = (int) centery * 32;
		z = (int) centerz * 32 + 32;
		
		rotation = (byte) (256/4*2);
		
		spawnZombie(player, loc.getWorld().getName(), x, y, z, rotation, itemID, standCounter);
		
		//-----------
		
		x = (int) centerx * 32;
		y = (int) centery * 32;
		z = (int) centerz * 32 - 12;
		
		rotation = (byte) (256/4*3);
		
		spawnZombie(player, loc.getWorld().getName(), x, y, z, rotation, itemID, standCounter);
	
		//-----------
	
		actions.put(standCounter, run);
		
		//update players
		for(Player p : loc.getWorld().getPlayers()) {
			respawnAll(p);
		}
		
		standCounter++;
	}
	
	public void clickOnInvalidEntity(Player player, int entityID) {
		if(entityIDs.contains(entityID)) {
			int standID = standIDs.get(entityID);
			
			actions.get(standID).run(player);
		}
	}
	
	//TODO - respawn them when the cunk reloads
	//respawn all fake zombies
	public void respawnAll(Player player) {
		for(Integer eid : entityIDs) {
			Item3DInfo info = entityInfo.get(eid);
			int standID = standIDs.get(eid);
			
			spawnOldZombie(player, info.getWorldName(), info.getX(), info.getY(), info.getZ(),
					info.getRotation(), info.getItemID(), eid, standID);
		}
	}
	
	public void removeAll() {
		for(Player p : Bukkit.getOnlinePlayers()) {
			for(Integer eid : entityIDs) {
				DvZPackets.sendFakeZombieRemove(p, eid);
			}
		}
	}
	
	//Spawn a new zombie
	private void spawnZombie(Player p, String world, int x, int y, int z, byte rotation, int itemID, int stand) {
		int eid = DisguiseSystemHandler.newEntityID();
		entityIDs.add(eid);
		standIDs.put(eid, stand);
		
		//save positioninfo for respawning
		Item3DInfo info = new Item3DInfo();
		info.setWorldName(world);
		info.setX(x);
		info.setY(y);
		info.setZ(z);
		info.setRotation(rotation);
		info.setItemID(itemID);
		entityInfo.put(eid, info);
		
		DvZPackets.sendFakeZombieSpawn(p, world, x, y, z, rotation, eid, itemID);
	}
	
	//respawn an old zombie
	private void spawnOldZombie(Player p, String world, int x, int y, int z, byte rotation, int itemID, int eid, int stand) {
		DvZPackets.sendFakeZombieSpawn(p, world, x, y, z, rotation, eid, itemID);
	}
	
	//runnable class
	public static class Item3DRunnable {
		public void run(Player player) {
			
		}
	}
}
