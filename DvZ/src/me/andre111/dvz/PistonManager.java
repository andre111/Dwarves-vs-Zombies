package me.andre111.dvz;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import me.andre111.dvz.dwarf.CustomDwarf;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPistonExtendEvent;

public class PistonManager {
	public static double maxDistance = 3*3;
	//private static ArrayList<Location> disabled = new ArrayList<Location>();
	private static int called = 0;
	
	public static void onPiston(BlockPistonExtendEvent event) {
		final List<Block> blist = event.getBlocks();
		Location loc = event.getBlock().getLocation();
		
		//find nearest player
		Player player = null;
		double distance = maxDistance;
		for(Player p : loc.getWorld().getPlayers()) {
			if(p.getLocation().distanceSquared(loc)<distance) {
				distance = p.getLocation().distanceSquared(loc);
				player = p;
			}
		}
		
		if(player!=null) {
			//TODO - maybe not use gametypes, but only add the abilities of the new dvz
			//is game of the new type
			Game game = DvZ.instance.getPlayerGame(player.getName());
			if(game!=null) {
				//if(game.getGameType()==Game.GAMETYPE_NEW) {
					checkPlayer(event, player, player.getLocation(), maxDistance, blist);
				//}
			}
		}
	}
	
	private static void checkPlayer(BlockPistonExtendEvent event, Player player, final Location loc, final double maxD, final List<Block> blist) {
		Game game = DvZ.instance.getPlayerGame(player.getName());
		
		if(game!=null) {
			int dSt = game.getPlayerState(player.getName()) - Game.dwarfMin;
			if(dSt>=0 && dSt<DvZ.dwarfManager.getCount()) {
				CustomDwarf cd = DvZ.dwarfManager.getDwarf(dSt);
				
				if(cd.isPistonEnabled()) {
					if(player.getLocation().clone().subtract(0, 1, 0).getBlock().getTypeId()!=cd.getPistonBlockBelow()) {
						return;
					}
					if(player.getLocation().clone().subtract(0, -2, 0).getBlock().getTypeId()!=cd.getPistonBlockAbove()) {
						return;
					}
					
					transform(event, loc, maxD, blist, cd.getPistonChange());
				}
			}
		}
	}
	
	private static void transform(BlockPistonExtendEvent event, final Location loc, final double maxD, final List<Block> blist, final LinkedHashMap<Integer, Integer> changeList) {
		if(called>0) {
			called -= 1;
			return;
		} else {
			for(Block b : blist) {
				for(Map.Entry<Integer, Integer> e : changeList.entrySet()) {
					if(b.getTypeId()==e.getKey()) {
						//in radius?
						if(b.getLocation().distanceSquared(loc)<=maxD) {
							called = 1;
							//important -> disable cainreation before setting the block
							b.setTypeId(e.getValue());
							break;
						}
					}
				}
			}
		}
	}
}
