package me.andre111.dvz.listeners;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.Game;
import me.andre111.dvz.GameType;
import me.andre111.dvz.config.ConfigManager;
import me.andre111.dvz.manager.BlockManager;
import me.andre111.dvz.manager.PistonManager;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockGrowEvent;
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
		
		if(game==null) return;
		
		if(game.getPlayerState(player.getName())<4 && !player.isOp()) {
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
			//monument
			if (game.isMonument(event.getBlock())) {
				//dwarves/assasins
				if(game.isDwarf(player.getName(), true)) {
					event.setCancelled(true);
					player.sendMessage(ConfigManager.getLanguage().getString("string_destroy_monument","What are you trying to do? This is your monument!"));
				}
				//monsters
				if(game.isMonster(player.getName())) {
					String message = ConfigManager.getLanguage().getString("string_destroyed_monument","Someone is destroying the monument!");
					
					if(!message.equals("") && !message.equals(" ")) {
						game.broadcastMessage(message);
					}
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
	
	//Speed up growth
	@EventHandler
	public void onBlockGrow(BlockGrowEvent event) {
		Game game = null;
	    World w = event.getBlock().getWorld();
	    for(int i=0; i<10; i++) {
	    	World w2 =  Bukkit.getServer().getWorld(plugin.getConfig().getString("world_prefix", "DvZ_")+"Main"+i+"");
	    	if(w2!=null)
	    	if(w.getName().equals(w2.getName())) {
	    		game = DvZ.instance.getGame(i);
	    		break;
	    	}
	    }
	    
	    if(game!=null) {
	    	byte extra = (byte) ConfigManager.getBlockFile().getInt("growthExtra.gameType"+GameType.getDwarfAndMonsterTypes(game.getGameType()), 0);

	    	if(extra!=0)
	    	if (event.getBlock().getType() == Material.CROPS
	    	|| event.getBlock().getType() == Material.CARROT
	    	|| event.getBlock().getType() == Material.POTATO) {
	    		event.getBlock().setData((byte) (event.getBlock().getData() + extra), true);
	    	}
	    }
	}
}
