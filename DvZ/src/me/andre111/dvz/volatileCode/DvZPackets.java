package me.andre111.dvz.volatileCode;

import java.lang.reflect.InvocationTargetException;

import me.andre111.dvz.DvZ;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.comphenix.protocol.Packets;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;

public abstract class DvZPackets {
	private static int D_ENTITY_ID;
	
	public static void setEntityID(int eid) {
		D_ENTITY_ID = eid;
	}
	
	public static void sendInfoBar(Player player, double percentage, String name) {
		sendBossbar(player, 200*percentage, name);
	}
	
	//Maxhealth: 200
	public static void sendBossbar(Player player, double currentHealth, String name) {
		PacketContainer pc = new PacketContainer(Packets.Server.MOB_SPAWN);
		
		pc.getIntegers().
		write(0, D_ENTITY_ID).
		write(1, (int) EntityType.ENDER_DRAGON.getTypeId()).
		write(2, player.getLocation().getBlockX() * 32).
		write(3, player.getLocation().getBlockY() * 32 + 256*3*32). //*2 to make it appere small as the invisibility doesn't seem to work
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
}
