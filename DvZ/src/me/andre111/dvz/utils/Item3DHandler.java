package me.andre111.dvz.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

import me.andre111.dvz.DvZ;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.Packets;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;

public class Item3DHandler {
	private ArrayList<Integer> entityIDs;
	private HashMap<Integer, Item3DInfo> entityInfo;
	private HashMap<Integer, Integer> standIDs;
	private HashMap<Integer, Item3DRunnable> actions;
	
	private int standCounter;
	
	private WrappedDataWatcher zombieWatcher;
	
	public Item3DHandler(DvZ plugin) {
		entityIDs = new ArrayList<Integer>();
		entityInfo = new HashMap<Integer, Item3DInfo>();
		standIDs = new HashMap<Integer, Integer>();
		actions = new HashMap<Integer, Item3DRunnable>();
		
		standCounter = 0;
		
		zombieWatcher = getDefaultWatcher(Bukkit.getServer().getWorlds().get(0), EntityType.ZOMBIE);
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
				sendRemovePacket(p, eid);
			}
		}
	}
	
	//Spawn a new zombie
	private void spawnZombie(Player p, String world, int x, int y, int z, byte rotation, int itemID, int stand) {
		int eid = DvZ.api.newEntityID();
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
		
		sendPacket(p, world, x, y, z, rotation, eid, itemID);
	}
	
	//respawn an old zombie
	private void spawnOldZombie(Player p, String world, int x, int y, int z, byte rotation, int itemID, int eid, int stand) {
		sendPacket(p, world, x, y, z, rotation, eid, itemID);
	}

	//Spawn a zombie
	private void sendPacket(Player p, String world, int x, int y, int z, byte rotation, int entityID, int itemID) {
		//world check
		if(p==null || !p.getWorld().getName().equals(world)) {
			return;
		}
		
		PacketContainer newPacket = new PacketContainer(24);

		//entitiy id
		newPacket.getIntegers().
		write(0, entityID).
		write(1, (int) EntityType.ZOMBIE.getTypeId()).
		//position
		write(2, (int) x).
		write(3, (int) y).
		write(4, (int) z);
		
		//rotation
		newPacket.getBytes().
		write(2, rotation);

		//invisibility
		WrappedDataWatcher zombieW = zombieWatcher.deepClone();
		zombieW.setObject(0, (byte) 0x20);
		
		newPacket.getDataWatcherModifier().
		write(0, zombieW);
		
		//equipment
		PacketContainer ePacket = new PacketContainer(Packets.Server.ENTITY_EQUIPMENT);
		
		ePacket.getIntegers().
		write(0, entityID);
		
		ePacket.getItemModifier().
		write(0, new ItemStack(itemID));

		try {
			ProtocolLibrary.getProtocolManager().sendServerPacket(p, newPacket);
			ProtocolLibrary.getProtocolManager().sendServerPacket(p, ePacket);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	private void sendRemovePacket(Player p, int eid) {
		PacketContainer newPacket = new PacketContainer(Packets.Server.ENTITY_STATUS);
	
		//entitiy id
		newPacket.getIntegers().
		write(0, eid);
		
		newPacket.getBytes().
		write(0, (byte) 3);
		
		
		//teleport out of sight
		PacketContainer tPacket = new PacketContainer(Packets.Server.ENTITY_TELEPORT);
		
		//entitiy id
		tPacket.getIntegers().
		write(0, eid).
		write(1, 0).
		write(2, 1000*32).
		write(3, 0);
		
		try {
			ProtocolLibrary.getProtocolManager().sendServerPacket(p, tPacket);
			ProtocolLibrary.getProtocolManager().sendServerPacket(p, newPacket);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public WrappedDataWatcher getDefaultWatcher(World world, EntityType type) {
		Entity entity = world.spawnEntity(new Location(world, 0, 256, 0), type);
		WrappedDataWatcher watcher = WrappedDataWatcher.getEntityWatcher(entity).deepClone();

		entity.remove();
		return watcher;
	}
	
	//runnable class
	public static class Item3DRunnable {
		public void run(Player player) {
			
		}
	}
}
