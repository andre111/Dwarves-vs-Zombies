package me.andre111.dvz;

import me.andre111.dvz.utils.InventoryHandler;

//import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Classswitcher {
	public static DvZ plugin;
	
	//###################################
	//MONSTER
	//###################################
	public static void becomeMonster(final Game game, final Player player) {
		becomeMonster(game, player, true);
	}
	public static void becomeMonster(final Game game, final Player player, boolean buff) {
		game.resetCountdowns(player.getName());
		if (buff) game.addMonsterBuff(player);
		
		InventoryHandler.clearInv(player, false);
		player.setTotalExperience(0);
		player.setLevel(0);
		player.setExp(0);
		
		if(!game.released) {
			/*Bukkit.getScheduler().scheduleSyncDelayedTask(game.getPlugin(), new Runnable() {
				public void run() {
					//TODO - diabled until I can fix it
					game.waitm.open(player);
				}
			}, 2);*/
		}
	}
}
