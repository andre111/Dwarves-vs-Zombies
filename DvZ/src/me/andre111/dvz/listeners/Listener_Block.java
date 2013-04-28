package me.andre111.dvz.listeners;

import me.andre111.dvz.BlockManager;
import me.andre111.dvz.DvZ;
import me.andre111.dvz.Game;
import me.andre111.dvz.PistonManager;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class Listener_Block implements Listener {
	private DvZ plugin;

	public Listener_Block(DvZ plugin){
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event){
		//if not dedicated and the player is not in the game->ignore
		if(!plugin.getConfig().getString("dedicated_mode","false").equals("true") && plugin.getPlayerGame(event.getPlayer().getName())==null) return;
		
		Player player = event.getPlayer();
		
		//wenn nicht ingame -> platzieren verbieten
		Game game = plugin.getPlayerGame(player.getName());
		if (game==null && !player.isOp()) {
			event.setCancelled(true);
			return;
		}
		if(game.getPlayerState(player.getName())<10 && !player.isOp()) {
			event.setCancelled(true);
			return;
		}
		//fix für das platzieren von köpfen/Enderman das Portal zu platzieren - deaktiviert, da jetzt custom monster existieren
		if(game.isMonster(player.getName())) {
			int id = game.getPlayerState(player.getName()) - Game.monsterMin;
			if(!DvZ.monsterManager.getMonster(id).isPlaceBlocks()) {
				event.setCancelled(true);
				return;
			}
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event){
		//if not dedicated and the player is not in the game->ignore
		if(!plugin.getConfig().getString("dedicated_mode","false").equals("true") && plugin.getPlayerGame(event.getPlayer().getName())==null) return;
		if(event.isCancelled()) return;
		
		//int ammount = 2;
		
		Player player = event.getPlayer();
		
		Game game = plugin.getPlayerGame(player.getName());
		if (game!=null) {
			//dwarves/assasins
			if(game.isDwarf(player.getName())) {
				if (game.isMonument(event.getBlock())) {
					event.setCancelled(true);
					player.sendMessage(DvZ.getLanguage().getString("string_destroy_monument","What are you trying to do? This is your monument!"));
				}
			}
		}
		
		if(event.isCancelled()) return;
		BlockManager.onBlockBreak(event);
		game.playerBreakBlock(player, event.getBlock());
	}
	
	//test for changing block when moved by a piston
	@EventHandler
	public void onBlockPistonExtend(BlockPistonExtendEvent event) {
		PistonManager.onPiston(event);
	}
}
