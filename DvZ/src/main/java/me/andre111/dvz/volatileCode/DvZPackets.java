package me.andre111.dvz.volatileCode;

import java.lang.reflect.InvocationTargetException;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.disguise.DisguiseSystemHandler;
import me.andre111.dvz.event.DvZInvalidInteractEvent;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.FieldAccessException;
import com.comphenix.protocol.wrappers.EnumWrappers.EntityUseAction;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;

public abstract class DvZPackets {
	//IMPORTANT:
	//Location of values are not like in the protocol,
	//but as they are kept in memory
	//see: https://github.com/aadnk/PacketWrapper/tree/master/PacketWrapper/src/main/java/com/comphenix/packetwrapper
	
	private static int D_ENTITY_ID;
	private static WrappedDataWatcher zombieWatcher;

	public static void setEntityID(int eid) {
		D_ENTITY_ID = eid;
	}

	public static void sendInfoBar(Player player, double percentage, String name) {
		sendBossbar(player, 200*percentage, name);
	}

	//Maxhealth: 200
	private static void sendBossbar(Player player, double currentHealth, String name) {
		PacketContainer pc = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY_LIVING);

		pc.getIntegers().
		write(0, D_ENTITY_ID).
		write(1, (int) EntityType.ENDER_DRAGON.getTypeId()).
		write(2, player.getLocation().getBlockX() * 32).
		write(3, player.getLocation().getBlockY() * 32 - 256*3*32). //*2 to make it appere small as the invisibility doesn't seem to work
		write(4, player.getLocation().getBlockZ() * 32);

		pc.getBytes().
		write(0, (byte) 0). //Pitch
		write(1, (byte) 0). //Head Pitch
		write(2, (byte) 0); //Yaw

		//pc.getShorts().
		//write(0, (short) 0). //X velocity
		//write(1, (short) 0). //Y velocity
		//write(2, (short) 0); //Z velocity


		WrappedDataWatcher wdw = new WrappedDataWatcher();
		wdw.setObject(0, (Byte) (byte) 0x20); //Flags, 0x20 = invisible
		wdw.setObject(6, (Float) (float) currentHealth);
		wdw.setObject(10, (String) name); //Entity name
		wdw.setObject(11, (Byte) (byte) 1); //Show name, 1 = show, 0 = don't show

		pc.getDataWatcherModifier().write(0, wdw);

		try {
			DvZ.protocolManager.sendServerPacket(player, pc);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public static void sendBlockBreakAnimToPlayer(Player player, Location loc, byte data) {
		PacketContainer pBlockBreakAnim = DvZ.protocolManager.createPacket(PacketType.Play.Server.BLOCK_BREAK_ANIMATION);

		pBlockBreakAnim.getIntegers().
		write(0, DisguiseSystemHandler.newEntityID()).
		write(1, loc.getBlockX()).
		write(2, loc.getBlockY()).
		write(3, loc.getBlockZ());
		pBlockBreakAnim.getBytes().
		write(0, data);


		try {
			if(player.isOnline())
				DvZ.protocolManager.sendServerPacket(player, pBlockBreakAnim);
		} catch (Exception e) {
		}
	}

	//Spawn a zombie
	public static void sendFakeFloatingZombieSpawn(Player p, String world, int x, int y, int z, byte rotation, int entityID, int entityID2, int itemID) {
		if(zombieWatcher==null) {
			zombieWatcher = getDefaultWatcher(Bukkit.getServer().getWorlds().get(0), EntityType.ZOMBIE);
		}
		
		//world check
		if(p==null || !p.getWorld().getName().equals(world)) {
			return;
		}
		
		//WITHER SKULL
		PacketContainer skullPacket = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY);
		
		//entitiy id
		skullPacket.getIntegers().
		write(0, entityID2).
		write(1, (int) x).
		write(2, (int) y).
		write(3, (int) z).
		
		//speed
		write(4, 0).
		write(5, 0).
		write(6, 0).
		
		write(7, 0). //yaw
		write(8, (int) rotation). //pitch
		
		write(9, 66); //Witherskull
		

		//ZOMBIE
		PacketContainer newPacket = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY_LIVING);

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
		PacketContainer ePacket = new PacketContainer(PacketType.Play.Server.ENTITY_EQUIPMENT);

		ePacket.getIntegers().
		write(0, entityID);

		ePacket.getItemModifier().
		write(0, new ItemStack(itemID));
		
		//riding/atach
		PacketContainer aPacket = new PacketContainer(PacketType.Play.Server.ATTACH_ENTITY);
		
		aPacket.getIntegers().
		write(0, 0). //not leashed
		write(1, entityID).
		write(2, entityID2);
		
		try {
			ProtocolLibrary.getProtocolManager().sendServerPacket(p, skullPacket);
			ProtocolLibrary.getProtocolManager().sendServerPacket(p, newPacket);
			ProtocolLibrary.getProtocolManager().sendServerPacket(p, ePacket);
			ProtocolLibrary.getProtocolManager().sendServerPacket(p, aPacket);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public static void sendFakeZombieRemove(Player p, int eid) {
		PacketContainer newPacket = new PacketContainer(PacketType.Play.Server.ENTITY_STATUS);

		//entitiy id
		newPacket.getIntegers().
		write(0, eid);

		newPacket.getBytes().
		write(0, (byte) 3);


		//teleport out of sight
		PacketContainer tPacket = new PacketContainer(PacketType.Play.Server.ENTITY_TELEPORT);

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
	
	public static WrappedDataWatcher getDefaultWatcher(World world, EntityType type) {
		Entity entity = world.spawnEntity(new Location(world, 0, 256, 0), type);
		if(entity instanceof Ageable) {
			((Ageable) entity).setAdult();
		}
		WrappedDataWatcher watcher = WrappedDataWatcher.getEntityWatcher(entity).deepClone();

		entity.remove();
		return watcher;
	}
	
	public static void setupInvalidEntityInteractListener() {
		DvZ.protocolManager.addPacketListener(new PacketAdapter(DvZ.instance,
		ListenerPriority.NORMAL, PacketType.Play.Client.USE_ENTITY) {
			    @Override
			    public void onPacketReceiving(PacketEvent event) {
			    	final Player player = event.getPlayer();
			        //if (event.getPacketID() == 0x07) {
			            try {
			            	PacketContainer packet = event.getPacket();

			            	final int target = packet.getIntegers().read(0);
			            	
			            	EntityUseAction eaction = packet.getEntityUseActions().read(0);
			            	int act = 0;
			            	switch(eaction) {
							case ATTACK:
								act = 0;
								break;
							case INTERACT:
								act = 1;
								break;
							default:
								break;
			            	}
			            	final int action = act;
			            	//(Integer) packet.getModifier().read(1);
			                //int target = packet.getSpecificModifier(int.class).read(1);
			                //int action = packet.getSpecificModifier(byte.class).read(1);
			                
			            	Bukkit.getScheduler().runTask(DvZ.instance, new Runnable() {
			            		public void run() {
				            		boolean found = false;
					                for(Entity e : player.getWorld().getEntities()) {
					                	if(e.getEntityId()==target) {
					                		found = true;
					                		break;
					                	}
					                }
					                if (!found) {
					                	DvZInvalidInteractEvent newEvent = new DvZInvalidInteractEvent(player, target, action);
					                    plugin.getServer().getPluginManager().callEvent(newEvent);
					                }
			            		}
			            	});
			            } catch (FieldAccessException e) {
			                DvZ.log("Couldn't access a field in an 0x07-UseEntity packet!");
			            }
			        //}
			    }
		});
	}
}
