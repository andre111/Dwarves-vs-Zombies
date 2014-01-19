package me.andre111.dvz;

import java.util.HashSet;

import me.andre111.dvz.config.ConfigManager;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Spellcontroller {
	public static DvZ plugin;
	
	public static void spellDisablePortal(Game game, Player player) {
		if(game.getCustomCooldown(player.getName(), "oldspell_dwarf_disable_portal")<=0) {
			game.setCustomCooldown(player.getName(), "oldspell_dwarf_disable_portal", plugin.getConfig().getInt("spelltime_disableportal",3));
			
			if(game.enderPortal!=null) {
				game.enderActive = false;
				
				game.broadcastMessage(ConfigManager.getLanguage().getString("string_portal_disable","The Portal has been disabled!"));
			} else {
				game.setCustomCooldown(player.getName(), "oldspell_dwarf_disable_portal", 0);
				DvZ.sendPlayerMessageFormated(player, ConfigManager.getLanguage().getString("string_teleport_noportal","The Enderman Portal does not exist yet!"));
			}
		} else {
			DvZ.sendPlayerMessageFormated(player, ConfigManager.getLanguage().getString("string_wait","You have to wait -0- Seconds!").replace("-0-", ""+game.getCustomCooldown(player.getName(), "oldspell_dwarf_disable_portal")));
		}
	}
	
	//###################################
	//Monster
	//###################################
	//rest in Listener_Entity onEntityDamaged
    public final static HashSet<Player> jumping = new HashSet<Player>();
	
	public static void spellIronGolemLand(Player player) {
		World w = player.getWorld();
		Location loc = player.getLocation();
		w.createExplosion(loc, 0);
		Game game = plugin.getPlayerGame(player.getName());
		if (game!=null)
				game.broadcastMessage(ConfigManager.getLanguage().getString("string_iron_near","An Iron Golem is nearby!"));
	}
	
	public final static HashSet<Player> jumpingNormal = new HashSet<Player>();
	public static void spellLeap(Player player, double forward, double upward, float power, boolean diasableDamage) {
		Vector v = player.getLocation().getDirection();
        v.setY(0).normalize().multiply(forward*power).setY(upward*power);
        player.setVelocity(v);
        if(diasableDamage)
        	jumpingNormal.add(player);
	}
	
	//rest in Listener_Entity onEntitydamagedEntity
	public static float identifier = (float)Math.random() * 20F;
	public static int sdamage = 8;
	
	public static void spellTeleport(Game game, Player player) {
		if(game.getCustomCooldown(player.getName(), "oldspell_monster_teleport_portal")<=0) {
			game.setCustomCooldown(player.getName(), "oldspell_monster_teleport_portal", plugin.getConfig().getInt("spelltime_teleport", 15));
			
			if(game.enderPortal!=null) {
				if(game.enderActive) {
					player.teleport(game.enderPortal);
					DvZ.sendPlayerMessageFormated(player, ConfigManager.getLanguage().getString("string_teleport_success","You teleported to the Enderman Portal!"));
				} else {
					game.setCustomCooldown(player.getName(), "oldspell_monster_teleport_portal", plugin.getConfig().getInt("spelltime_teleport", 0));
					DvZ.sendPlayerMessageFormated(player, ConfigManager.getLanguage().getString("string_teleport_inactive","The Enderman Portal has been deactivated!"));
				}
			} else {
				game.setCustomCooldown(player.getName(), "oldspell_monster_teleport_portal", plugin.getConfig().getInt("spelltime_teleport", 0));
				DvZ.sendPlayerMessageFormated(player, ConfigManager.getLanguage().getString("string_teleport_noportal","The Enderman Portal does not exist yet!"));
			}
		} else {
			DvZ.sendPlayerMessageFormated(player, ConfigManager.getLanguage().getString("string_wait","You have to wait -0- Seconds!").replace("-0-", ""+game.getCustomCooldown(player.getName(), "oldspell_monster_teleport_portal")));
		}
	}
	
	//create the portalblocks
	public static void createPortal(Location loc) {
		World w = loc.getWorld();
		int x = loc.getBlockX();
		int y = loc.getBlockY()-1;
		int z = loc.getBlockZ();
		
		Block block = w.getBlockAt(x, y, z);
		Block block2;
		block.setType(Material.ENDER_STONE);
		
		for(int i=0; i<=4; i+=2) {
			block2 = block.getRelative(-1-i, 0, 0);
			block2.setType(Material.ENDER_STONE);
			for(int j=-i/2; j<=i/2; j++) {
				block2 = block.getRelative(-1-i, 0, j);
				block2.setType(Material.ENDER_STONE);
			}
			
			block2 = block.getRelative(1+i, 0, 0);
			block2.setType(Material.ENDER_STONE);
			for(int j=-i/2; j<=i/2; j++) {
				block2 = block.getRelative(1+i, 0, j);
				block2.setType(Material.ENDER_STONE);
			}
			
			block2 = block.getRelative(0, 0, -1-i);
			block2.setType(Material.ENDER_STONE);
			for(int j=-i/2; j<=i/2; j++) {
				block2 = block.getRelative(j, 0, -1-i);
				block2.setType(Material.ENDER_STONE);
			}
			
			block2 = block.getRelative(0, 0, 1+i);
			block2.setType(Material.ENDER_STONE);
			for(int j=-i/2; j<=i/2; j++) {
				block2 = block.getRelative(j, 0, 1+i);
				block2.setType(Material.ENDER_STONE);
			}
		}
		
		for(int i=-2; i<=2; i+=2) {
			for(int j=-2; j<=2; j+=2) {
				block2 = block.getRelative(i, 0, j);
				block2.setType(Material.ENDER_STONE);
			}
		}
		
		for(int i=-4; i<=4; i+=4) {
			for(int j=-3; j<=3; j+=3) {
				block2 = block.getRelative(i, 0, j);
				block2.setType(Material.ENDER_STONE);
			}
		}
		
		for(int i=-3; i<=3; i+=3) {
			for(int j=-4; j<=4; j+=4) {
				block2 = block.getRelative(i, 0, j);
				block2.setType(Material.ENDER_STONE);
			}
		}
	}
}
