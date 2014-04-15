package me.andre111.dvz.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BlockIterator;

public class PlayerHandler {
	//get Player looked at
	public static Player getTarget(Player player, int range) {
		List<Entity> nearbyE = player.getNearbyEntities(range, range, range);
		ArrayList<Player> livingE = new ArrayList<Player>();

		for (Entity e : nearbyE) {
			if (e instanceof Player) {
				livingE.add((Player) e);
			}
		}

		Player target = null;
		BlockIterator bItr = new BlockIterator(player, range);
		Block block;
		Location loc;
		int bx, by, bz;
		double ex, ey, ez;
		// loop through player's line of sight
		while (bItr.hasNext()) {
			block = bItr.next();
			bx = block.getX();
			by = block.getY();
			bz = block.getZ();
			// check for entities near this block in the line of sight
			for (Player e : livingE) {
				loc = e.getLocation();
				ex = loc.getX();
				ey = loc.getY();
				ez = loc.getZ();
				if ((bx-.75 <= ex && ex <= bx+1.75) && (bz-.75 <= ez && ez <= bz+1.75) && (by-1 <= ey && ey <= by+2.5)) {
					// entity is close enough, set target and stop
					target = e;
					break;
				}
			}
		}
		
		return target;
	}
	
	public static boolean hasHigherPotionEffect(Player player, PotionEffectType type, int level) {
		if(player.hasPotionEffect(type)) {
			PotionEffect[] effects = (PotionEffect[]) player.getActivePotionEffects().toArray(new PotionEffect[player.getActivePotionEffects().size()]);
			for(int i=0; i<effects.length; i++) {
				if(effects[i].getType()==type) {
					if(effects[i].getAmplifier()>level) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public static void resetPotionEffects(Player player) {
		for(PotionEffect pet : player.getActivePotionEffects()) {
			player.removePotionEffect(pet.getType());
			//TODO - find a way not using the workaround override method
			player.addPotionEffect(new PotionEffect(pet.getType(), 1, 1), true);
		}
	}
	
	public static Player getPlayerFromUUID(String uuid) {
		return getPlayerFromUUID(UUID.fromString(uuid));
	}
	public static Player getPlayerFromUUID(UUID uuid) {
		for(Player player : Bukkit.getOnlinePlayers()) {
			if(player.getUniqueId().equals(uuid)) {
				return player;
			}
		}
		
		return null;
	}
}
