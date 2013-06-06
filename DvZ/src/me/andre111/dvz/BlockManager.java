package me.andre111.dvz;

import java.util.ArrayList;

import me.andre111.dvz.utils.ItemHandler;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class BlockManager {
	private static ArrayList<String> gameType1 = new ArrayList<String>();
	private static ArrayList<String> gameType2 = new ArrayList<String>();
	
	private static boolean infiniteCake1;
	private static boolean infiniteCake2;

	public static void loadConfig() {
		 gameType1.addAll(DvZ.getBlockFile().getStringList("blocks.drop.gameType1"));
		 infiniteCake1 = DvZ.getBlockFile().getBoolean("blocks.infiniteCake.gameType1");
		 
		 gameType2.addAll(DvZ.getBlockFile().getStringList("blocks.drop.gameType2"));
		 infiniteCake2 = DvZ.getBlockFile().getBoolean("blocks.infiniteCake.gameType2");
	}
	
	public static void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Game game = DvZ.instance.getPlayerGame(player.getName());
		
		if(game!=null) {
			int gType = game.getGameType();
			if(gType==1) changeDrops(event, gameType1);
			else if(gType==2) changeDrops(event, gameType2);
		}
	}
	
	private static void changeDrops(BlockBreakEvent event, ArrayList<String> drop) {
		int id = event.getBlock().getTypeId();
		byte data = event.getBlock().getData();
		
		for(String st : drop) {
			String[] split = st.split(";");
			
			//check if it is the right block
			String[] binfo = split[0].split(":");
			if(Integer.parseInt(binfo[0])==id && Integer.parseInt(binfo[1])==data) {
				//drop the items
				Block block = event.getBlock();
				event.setCancelled(true);
				block.setType(Material.AIR); 
				
				if(!split[1].equals("-1")) {
					block.getWorld().playSound(block.getLocation(), Sound.valueOf(split[1]), 0.5F, 1F);
				}
				
				for(int i=2; i<split.length; i++) {
					ItemStack it = ItemHandler.decodeItem(split[i]);
					
					if(it!=null) {
						block.getWorld().dropItemNaturally(block.getLocation(), it);
					}
				}
			}
		}
	}
	
	//infinite cake
	public static void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Game game = DvZ.instance.getPlayerGame(player.getName());
		
		if(game!=null) {
			int gType = game.getGameType();
			cakeEaten(event, gType);
		}
	}
	
	private static void cakeEaten(PlayerInteractEvent event, int gameType) {
		if(gameType==1 && !infiniteCake1) return;
		if(gameType==2 && !infiniteCake2) return;
		
		if(event.getAction()==Action.RIGHT_CLICK_BLOCK) {
			Block block = event.getClickedBlock();
			
			if(block.getType()==Material.CAKE_BLOCK) {
				block.setData((byte) 0, true);
			}
		}
	}
	
}
