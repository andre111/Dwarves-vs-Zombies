package me.andre111.dvz;

import java.util.HashSet;

import me.andre111.dvz.config.ConfigManager;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Spellcontroller {
	public static DvZ plugin;
	
	public static void spellDisablePortal(Game game, Player player) {
		if(game.getCustomCooldown(player.getUniqueId(), "oldspell_dwarf_disable_portal")<=0) {
			game.setCustomCooldown(player.getUniqueId(), "oldspell_dwarf_disable_portal", plugin.getConfig().getInt("spelltime_disableportal",3));
			
			if(game.enderPortal!=null) {
				game.enderActive = false;
				
				game.broadcastMessage(ConfigManager.getLanguage().getString("string_portal_disable","The Portal has been disabled!"));
			} else {
				game.setCustomCooldown(player.getUniqueId(), "oldspell_dwarf_disable_portal", 0);
				DvZ.sendPlayerMessageFormated(player, ConfigManager.getLanguage().getString("string_teleport_noportal","The Enderman Portal does not exist yet!"));
			}
		} else {
			DvZ.sendPlayerMessageFormated(player, ConfigManager.getLanguage().getString("string_wait","You have to wait -0- Seconds!").replace("-0-", ""+game.getCustomCooldown(player.getUniqueId(), "oldspell_dwarf_disable_portal")));
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
		Game game = plugin.getPlayerGame(player.getUniqueId());
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
		if(game.getCustomCooldown(player.getUniqueId(), "oldspell_monster_teleport_portal")<=0) {
			game.setCustomCooldown(player.getUniqueId(), "oldspell_monster_teleport_portal", plugin.getConfig().getInt("spelltime_teleport", 15));
			
			if(game.enderPortal!=null) {
				if(game.enderActive) {
					player.teleport(game.enderPortal);
					DvZ.sendPlayerMessageFormated(player, ConfigManager.getLanguage().getString("string_teleport_success","You teleported to the Enderman Portal!"));
				} else {
					game.setCustomCooldown(player.getUniqueId(), "oldspell_monster_teleport_portal", plugin.getConfig().getInt("spelltime_teleport", 0));
					DvZ.sendPlayerMessageFormated(player, ConfigManager.getLanguage().getString("string_teleport_inactive","The Enderman Portal has been deactivated!"));
				}
			} else {
				game.setCustomCooldown(player.getUniqueId(), "oldspell_monster_teleport_portal", plugin.getConfig().getInt("spelltime_teleport", 0));
				DvZ.sendPlayerMessageFormated(player, ConfigManager.getLanguage().getString("string_teleport_noportal","The Enderman Portal does not exist yet!"));
			}
		} else {
			DvZ.sendPlayerMessageFormated(player, ConfigManager.getLanguage().getString("string_wait","You have to wait -0- Seconds!").replace("-0-", ""+game.getCustomCooldown(player.getUniqueId(), "oldspell_monster_teleport_portal")));
		}
	}
}
