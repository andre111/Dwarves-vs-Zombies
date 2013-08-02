package me.andre111.dvz;

import java.util.HashSet;
import java.util.Random;

import me.andre111.dvz.config.ConfigManager;
import me.andre111.dvz.utils.InventoryHandler;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.util.Vector;

public class Spellcontroller {
	public static DvZ plugin;
	
	public static void spellDisablePortal(Game game, Player player) {
		if(game.getCountdown(player.getName(), 4)==0) {
			game.setCountdown(player.getName(), 4, plugin.getConfig().getInt("spelltime_disableportal",3));
			
			if(game.enderPortal!=null) {
				game.enderActive = false;
				
				game.broadcastMessage(ConfigManager.getLanguage().getString("string_portal_disable","The Portal has been disabled!"));
			} else {
				game.setCountdown(player.getName(), 4, 0);
				player.sendMessage(ConfigManager.getLanguage().getString("string_teleport_noportal","The Enderman Portal does not exist yet!"));
			}
		} else {
			player.sendMessage(ConfigManager.getLanguage().getString("string_wait","You have to wait -0- Seconds!").replace("-0-", ""+game.getCountdown(player.getName(), 4)));
		}
	}
	
	//###################################
	//Monster
	//###################################
	//rest in Listener_Entity onEntityDamaged
	private static double forwardVelocity = 40 / 10D;
    private static double upwardVelocity = 15 / 10D;
    public final static HashSet<Player> jumping = new HashSet<Player>();
	public static void spellIronGolemLeap(Game game, Player player) {
		if(game.getCountdown(player.getName(), 2)==0) {
			game.setCountdown(player.getName(), 2, plugin.getConfig().getInt("spelltime_ironjump",30));
			
            spellLeap(player, forwardVelocity, upwardVelocity, 1, true);
            jumping.add(player);
		} else {
			player.sendMessage(ConfigManager.getLanguage().getString("string_wait","You have to wait -0- Seconds!").replace("-0-", ""+game.getCountdown(player.getName(), 2)));
		}
	}
	
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
	public static void spellSnowGolemThrow(Game game, Player player) {
		if(game.getCountdown(player.getName(), 2)==0) {
			if(InventoryHandler.countItems(player, 332, 0)>=96) {
				InventoryHandler.removeItems(player, 332, 0, 96);
				game.setCountdown(player.getName(), 2, plugin.getConfig().getInt("spelltime_snowgolemthrow",0));
			
				 Random rand = new Random();
                 Vector mod;
                 for (int i = 0; i < 250; i++) {
                	 Snowball snowball = player.launchProjectile(Snowball.class);
                	 snowball.setFallDistance(identifier); // tag the snowballs
                	 mod = new Vector((rand.nextDouble() - .5) * 15 / 10.0, (rand.nextDouble() - .5) * 5 / 10.0, (rand.nextDouble() - .5) * 15 / 10.0);
                	 snowball.setVelocity(snowball.getVelocity().add(mod));
                 }
			} else {
				player.sendMessage(ConfigManager.getLanguage().getString("string_need_snow","You need 96 Snowballs!"));
			}
		} else {
			player.sendMessage(ConfigManager.getLanguage().getString("string_wait","You have to wait -0- Seconds!").replace("-0-", ""+game.getCountdown(player.getName(), 2)));
		}
	}
	
	public static void spellTeleport(Game game, Player player) {
		if(game.getCountdown(player.getName(), 4)==0) {
			game.setCountdown(player.getName(), 4, plugin.getConfig().getInt("spelltime_teleport",15));
			
			if(game.enderPortal!=null) {
				if(game.enderActive) {
					player.teleport(game.enderPortal);
					player.sendMessage(ConfigManager.getLanguage().getString("string_teleport_success","You teleported to the Enderman Portal!"));
				} else {
					game.setCountdown(player.getName(), 4, 0);
					player.sendMessage(ConfigManager.getLanguage().getString("string_teleport_inactive","The Enderman Portal has been deactivated!"));
				}
			} else {
				game.setCountdown(player.getName(), 4, 0);
				player.sendMessage(ConfigManager.getLanguage().getString("string_teleport_noportal","The Enderman Portal does not exist yet!"));
			}
		} else {
			player.sendMessage(ConfigManager.getLanguage().getString("string_wait","You have to wait -0- Seconds!").replace("-0-", ""+game.getCountdown(player.getName(), 4)));
		}
	}
	
	public static void spellSuizide(Game game, Player player) {
		player.damage((double) 1000);
	}
	
	//create the portalblocks
	public static void createPortal(Location loc) {
		World w = loc.getWorld();
		int x = loc.getBlockX();
		int y = loc.getBlockY()-1;
		int z = loc.getBlockZ();
		
		Block block = w.getBlockAt(x, y, z);
		Block block2;
		block.setTypeId(121);
		
		for(int i=0; i<=4; i+=2) {
			block2 = block.getRelative(-1-i, 0, 0);
			block2.setTypeId(121);
			for(int j=-i/2; j<=i/2; j++) {
				block2 = block.getRelative(-1-i, 0, j);
				block2.setTypeId(121);
			}
			
			block2 = block.getRelative(1+i, 0, 0);
			block2.setTypeId(121);
			for(int j=-i/2; j<=i/2; j++) {
				block2 = block.getRelative(1+i, 0, j);
				block2.setTypeId(121);
			}
			
			block2 = block.getRelative(0, 0, -1-i);
			block2.setTypeId(121);
			for(int j=-i/2; j<=i/2; j++) {
				block2 = block.getRelative(j, 0, -1-i);
				block2.setTypeId(121);
			}
			
			block2 = block.getRelative(0, 0, 1+i);
			block2.setTypeId(121);
			for(int j=-i/2; j<=i/2; j++) {
				block2 = block.getRelative(j, 0, 1+i);
				block2.setTypeId(121);
			}
		}
		
		for(int i=-2; i<=2; i+=2) {
			for(int j=-2; j<=2; j+=2) {
				block2 = block.getRelative(i, 0, j);
				block2.setTypeId(121);
			}
		}
		
		for(int i=-4; i<=4; i+=4) {
			for(int j=-3; j<=3; j+=3) {
				block2 = block.getRelative(i, 0, j);
				block2.setTypeId(121);
			}
		}
		
		for(int i=-3; i<=3; i+=3) {
			for(int j=-4; j<=4; j+=4) {
				block2 = block.getRelative(i, 0, j);
				block2.setTypeId(121);
			}
		}
	}
}
