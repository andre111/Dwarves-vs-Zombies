package me.andre111.dvz.manager;

import java.util.ArrayList;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.Game;
import me.andre111.dvz.config.ConfigManager;
import me.andre111.dvz.volatileCode.DeprecatedMethods;
import me.andre111.items.ItemHandler;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class BlockManager {
	//TODO - change for teams
	private static ArrayList<String> gameType1_dwarf = new ArrayList<String>();
	//private static ArrayList<String> gameType1_monster = new ArrayList<String>();
	private static ArrayList<String> blacklist1_dwarf = new ArrayList<String>();
	//private static ArrayList<String> blacklist1_monster = new ArrayList<String>();
	private static ArrayList<String> changeBreak1_dwarf = new ArrayList<String>();
	//private static ArrayList<String> changeBreak1_monster = new ArrayList<String>();
	private static boolean infiniteCake1_dwarf;
	//private static boolean infiniteCake1_monster;
	
	private static ArrayList<String> gameType2_dwarf = new ArrayList<String>();
	//private static ArrayList<String> gameType2_monster = new ArrayList<String>();
	private static ArrayList<String> blacklist2_dwarf = new ArrayList<String>();
	//private static ArrayList<String> blacklist2_monster = new ArrayList<String>();
	private static ArrayList<String> changeBreak2_dwarf = new ArrayList<String>();
	//private static ArrayList<String> changeBreak2_monster = new ArrayList<String>();
	private static boolean infiniteCake2_dwarf;
	//private static boolean infiniteCake2_monster;

	public static void loadConfig() {
		gameType1_dwarf.addAll(ConfigManager.getBlockFile().getStringList("blocks.drop.gameType1.dwarves"));
		//gameType1_monster.addAll(ConfigManager.getBlockFile().getStringList("blocks.drop.gameType1.monsters"));
		blacklist1_dwarf.addAll(ConfigManager.getBlockFile().getStringList("blocks.breakBlackList.gameType1.dwarves"));
		//blacklist1_monster.addAll(ConfigManager.getBlockFile().getStringList("blocks.breakBlackList.gameType1.monsters"));
		changeBreak1_dwarf.addAll(ConfigManager.getBlockFile().getStringList("blocks.breakChangeBlock.gameType1.dwarves"));
		//changeBreak1_monster.addAll(ConfigManager.getBlockFile().getStringList("blocks.breakChangeBlock.gameType1.monsters"));
		infiniteCake1_dwarf = ConfigManager.getBlockFile().getBoolean("blocks.infiniteCake.gameType1.dwarves");
		//infiniteCake1_monster = ConfigManager.getBlockFile().getBoolean("blocks.infiniteCake.gameType1.monsters");
		
		gameType2_dwarf.addAll(ConfigManager.getBlockFile().getStringList("blocks.drop.gameType2.dwarves"));
		//gameType2_monster.addAll(ConfigManager.getBlockFile().getStringList("blocks.drop.gameType2.monsters"));
		blacklist2_dwarf.addAll(ConfigManager.getBlockFile().getStringList("blocks.breakBlackList.gameType2.dwarves"));
		//blacklist2_monster.addAll(ConfigManager.getBlockFile().getStringList("blocks.breakBlackList.gameType2.monsters"));
		changeBreak2_dwarf.addAll(ConfigManager.getBlockFile().getStringList("blocks.breakChangeBlock.gameType2.dwarves"));
		//changeBreak2_monster.addAll(ConfigManager.getBlockFile().getStringList("blocks.breakChangeBlock.gameType2.monsters"));
		infiniteCake2_dwarf = ConfigManager.getBlockFile().getBoolean("blocks.infiniteCake.gameType2.dwarves");
		//infiniteCake2_monster = ConfigManager.getBlockFile().getBoolean("blocks.infiniteCake.gameType2.monsters");
	}
	
	public static void onBlockBreak(BlockBreakEvent event) {
		if(event.isCancelled()) return;
		
		int blockID = DeprecatedMethods.getBlockID(event.getBlock());
		byte data = DeprecatedMethods.getBlockData(event.getBlock());
		
		Player player = event.getPlayer();
		Game game = DvZ.instance.getPlayerGame(player.getUniqueId());
		
		if(game!=null) {
			int gType = game.getGameType();
			//Blockbreak blacklist
			//if(game.isDwarf(player.getUniqueId(), true)) {
				if(gType==1) if(isBlackListed(blockID, data, blacklist1_dwarf)) {
					event.setCancelled(true);
					return;
				} else 
				if(gType==2) if(isBlackListed(blockID, data, blacklist2_dwarf)) {
					event.setCancelled(true);
					return;
				}
			/*} else {
				if(gType==1) if(isBlackListed(blockID, data, blacklist1_monster)) {
					event.setCancelled(true);
					return;
				} else 
				if(gType==2) if(isBlackListed(blockID, data, blacklist2_monster)) {
					event.setCancelled(true);
					return;
				}
			}*/
			
			//change drops
			//if(game.isDwarf(player.getUniqueId(), true)) {
				if(gType==1) changeDrops(event, gameType1_dwarf);
				else if(gType==2) changeDrops(event, gameType2_dwarf);
			/*} else {
				if(gType==1) changeDrops(event, gameType1_monster);
				else if(gType==2) changeDrops(event, gameType2_monster);
			}*/
			
			//change block on break
			//if(game.isDwarf(player.getUniqueId(), true)) {
				if(gType==1) changeBreak(event, blockID, data, changeBreak1_dwarf);
				else if(gType==2) changeBreak(event, blockID, data, changeBreak2_dwarf);
			/*} else {
				if(gType==1) changeBreak(event, blockID, data, changeBreak1_monster);
				else if(gType==2) changeBreak(event, blockID, data, changeBreak2_monster);
			}*/
		}
	}
	
	private static boolean isBlackListed(int blockID, byte data, ArrayList<String> blacklist) {
		for(String st : blacklist) {
			//check if it is the right block
			String[] binfo = st.split(":");
			
			if(Integer.parseInt(binfo[0])==blockID) {
				if(Integer.parseInt(binfo[1])==data) {
					return true;
				}
			}
		}
		return false;
	}
	
	private static void changeDrops(BlockBreakEvent event, ArrayList<String> drop) {
		int id = DeprecatedMethods.getBlockID(event.getBlock());
		byte data = DeprecatedMethods.getBlockData(event.getBlock());

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
					ItemStack it = ItemHandler.decodeItem(split[i], null);
					
					if(it!=null) {
						block.getWorld().dropItemNaturally(block.getLocation(), it);
					}
				}
			}
		}
	}
	
	private static void changeBreak(BlockBreakEvent event, int blockID, byte data, ArrayList<String> drop) {
		for(String st : drop) {
			String[] split = st.split(";");
			
			//check if it is the right block
			String[] binfo = split[0].split(":");
			String[] newBlock = split[1].split(":");
			
			
			if(Integer.parseInt(binfo[0])==blockID) {
				if(Integer.parseInt(binfo[1])==data) {
					event.setCancelled(true);
					
					Block b = event.getBlock();
					DeprecatedMethods.setBlockIDandData(b, Integer.parseInt(newBlock[0]), (byte)Integer.parseInt(newBlock[1]), true);
				}
			}
		}
	}
	
	//infinite cake
	public static void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Game game = DvZ.instance.getPlayerGame(player.getUniqueId());
		
		if(game!=null) {
			int gType = game.getGameType();
			cakeEaten(event, gType);
		}
	}
	
	private static void cakeEaten(PlayerInteractEvent event, int gameType) {
		Player player = event.getPlayer();
		Game game = DvZ.instance.getPlayerGame(player.getUniqueId());
		
		if(game!=null) {
			//if(game.isDwarf(player.getUniqueId(), true)) {
				if(gameType==1 && !infiniteCake1_dwarf) return;
				if(gameType==2 && !infiniteCake2_dwarf) return;
			/*} else {
				if(gameType==1 && !infiniteCake1_monster) return;
				if(gameType==2 && !infiniteCake2_monster) return;
			}*/
		}
		
		if(event.getAction()==Action.RIGHT_CLICK_BLOCK) {
			Block block = event.getClickedBlock();
			
			if(block.getType()==Material.CAKE_BLOCK) {
				DeprecatedMethods.setBlockData(block, (byte) 0, true);
			}
		}
	}
	
	//reload this configsection/file
	public static void reload() {
		gameType1_dwarf.clear();
		//gameType1_monster.clear();
		blacklist1_dwarf.clear();
		//blacklist1_monster.clear();
		changeBreak1_dwarf.clear();
		//changeBreak1_monster.clear();
		
		gameType2_dwarf.clear();
		//gameType2_monster.clear();
		blacklist2_dwarf.clear();
		//blacklist2_monster.clear();
		changeBreak2_dwarf.clear();
		//changeBreak2_monster.clear();
		
		loadConfig();
	}
}
