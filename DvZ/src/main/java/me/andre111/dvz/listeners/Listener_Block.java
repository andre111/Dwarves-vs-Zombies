package me.andre111.dvz.listeners;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.Game;
import me.andre111.dvz.GameType;
import me.andre111.dvz.config.ConfigManager;
import me.andre111.dvz.manager.BlockManager;
import me.andre111.dvz.manager.PistonManager;
import me.andre111.dvz.teams.Team;
import me.andre111.items.RewardManager;

import org.bukkit.Bukkit;
import org.bukkit.CropState;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.material.Crops;

public class Listener_Block implements Listener {
	private DvZ plugin;

	public Listener_Block(DvZ plugin){
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event){
		Player player = event.getPlayer();
		
		Game game = plugin.getPlayerGame(player.getUniqueId());
		if(game==null) return;
		
		String pstate = game.getPlayerState(player.getUniqueId());
		if((pstate.equals(Game.STATE_PREGAME) || pstate.equals(Game.STATE_CHOOSECLASS)) && !player.isOp()) {
			event.setCancelled(true);
			return;
		}
		//rewards on blockplace
		if(game.isPlayer(player.getUniqueId()) && pstate.startsWith(Game.STATE_CLASSPREFIX)) {
			if(!game.getClass(player.getUniqueId()).isPlaceBlocks()) {
				event.setCancelled(true);
				return;
			}
			
			if(game.getClass(player.getUniqueId()).isRewardOnBlockPlace()) {
				RewardManager.addRewardPoints(player, 1);
			}
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event){
		if(event.isCancelled()) return;
		
		//int ammount = 2;
		
		Player player = event.getPlayer();
		
		Game game = plugin.getPlayerGame(player.getUniqueId());
		if (game!=null) {
			Team team = game.getTeam(player.getUniqueId());
			//monument
			if (game.isMonument(event.getBlock(), team)) {
				event.setCancelled(true);
				DvZ.sendPlayerMessageFormated(player, ConfigManager.getLanguage().getString("string_destroy_monument","What are you trying to do? This is your monument!"));
			} else {
				for(Team teamO : game.teamSetup.getTeams()) {
					if(game.isMonument(event.getBlock(), teamO)) {
						if(team.isFriendly(teamO)) {
							event.setCancelled(true);
							DvZ.sendPlayerMessageFormated(player, ConfigManager.getLanguage().getString("string_destroy_monument_other","What are you trying to do? This is the monument of -0-!").replace("-0-", teamO.getDisplayName()));
						} else {
							//TODO - maybe fix wording for team(of Dwarfes?)
							String message = ConfigManager.getLanguage().getString("string_destroyed_monument","Someone is destroying the monument of -0-!").replace("-0-", teamO.getDisplayName());
							
							if(!message.equals("") && !message.equals(" ")) {
								game.broadcastMessage(message);
							}
						}
					}
				}
			}
		}
		
		if(event.isCancelled()) return;
		BlockManager.onBlockBreak(event);
		if(game!=null)
			game.playerBreakBlock(player, event.getBlock());
	}
	
	//test for changing block when moved by a piston
	@EventHandler
	public void onBlockPistonExtend(BlockPistonExtendEvent event) {
		PistonManager.onPiston(event);
	}
	
	//Speed up growth
	private CropState[] stateOrder = {CropState.SEEDED, CropState.GERMINATED, CropState.VERY_SMALL, CropState.SMALL,
									  CropState.MEDIUM, CropState.TALL, CropState.VERY_TALL, CropState.RIPE};
	
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
	    	//if (event.getBlock().getType() == Material.CROPS || event.getBlock().getType() == Material.CARROT || event.getBlock().getType() == Material.POTATO) {
	    		if(event.getNewState().getData() instanceof Crops) {
	    			Crops crops = (Crops) event.getNewState().getData();
	    			
	    			CropState nextState = crops.getState();
	    			int pos = 0;
	    			for(int i=0; i<stateOrder.length; i++) {
	    				if(nextState==stateOrder[i]) {
	    					pos = i;
	    					break;
	    				}
	    			}
	    			pos += extra;
	    			if(pos>=stateOrder.length) pos = stateOrder.length-1;
	    			
	    			crops.setState(stateOrder[pos]);
	    		}
	    		//event.getBlock().setData((byte) (event.getBlock().getData() + extra), true);
	    	//}
	    }
	}
}
