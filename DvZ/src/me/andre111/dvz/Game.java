package me.andre111.dvz;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import me.andre111.dvz.config.ConfigManager;
import me.andre111.dvz.dragon.Dragon;
import me.andre111.dvz.dragon.PlayerDragon;
import me.andre111.dvz.dwarf.CustomDwarf;
import me.andre111.dvz.event.DVZGameEndEvent;
import me.andre111.dvz.event.DVZGameStartEvent;
import me.andre111.dvz.item.CustomItem;
import me.andre111.dvz.monster.CustomMonster;
import me.andre111.dvz.players.SpecialPlayer;
import me.andre111.dvz.utils.ExperienceUtils;
import me.andre111.dvz.utils.GameOptionClickEventHandler;
import me.andre111.dvz.utils.IconMenu;
import me.andre111.dvz.utils.ItemHandler;
import me.andre111.dvz.utils.Slapi;
import me.andre111.dvz.utils.WaitingMenu;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import pgDev.bukkit.DisguiseCraft.disguise.Disguise;

public class Game {
	private DvZ plugin;
	
	private int gameType;
	
	private int state;
	private int time;
	private int dauer;
	private int ticker;
	private int fastticker;
	private boolean starting;
	
	public Location spawnDwarves;
	public Location spawnMonsters;
	
	public boolean enderActive;
	public Location enderPortal;
	public String enderMan;
	
	public Location monument;
	public boolean monumentexists;
	private int monumentHealth;
	private String lastdwarf;
	
	private Inventory globalCrystalChest;
	private HashMap<String, Inventory> crystalPerPlayer = new HashMap<String, Inventory>();
	
	public HashMap<String, Integer> playerstate = new HashMap<String, Integer>();
	//1 = nix
	//2 = choose dwarf
	//3 = choose monster
	//5 = dragon warrior
	//6 = assasin
	//10 - ?? = dwarves
	//?? - ?? = monsters
	//?? - ?? = dragon
	//TODO - use this vars everywhere
	public static int pickDwarf = 2;
	public static int pickMonster = 3;
	
	public static int assasinState = 5;
	public static int dragonWarrior = 6;
	
	public static int dwarfMin = 10;
	public static int dwarfMax = 29;
	public static int monsterMin = 30;
	public static int monsterMax = 49;
	public static int dragonMin = 100;
	
	public WaitingMenu waitm;
	
	private Dragon dragon;
	
	private HashMap<String, Integer> spell1time = new HashMap<String, Integer>();
	private HashMap<String, Integer> spell2time = new HashMap<String, Integer>();
	private HashMap<String, Integer> spell3time = new HashMap<String, Integer>(); //3 = bei assasin time until kill
	private HashMap<String, Integer> spell4time = new HashMap<String, Integer>(); //4 = immer map für monster/immer disable portal für dwarves
	
	private HashMap<String, Integer> invultimer = new HashMap<String, Integer>();
	
	//used for custom cooldowns String: Playername:CooldownName
	private HashMap<String, Integer> customCooldown = new HashMap<String, Integer>();
	private ManaManager mana;
	
	private boolean autoassasin;
	private int a_minutes;
	private int a_count;
	private int a_maxdeaths;
	private int a_ticker;
	public int deaths;
	
	private int infotimer;
	
	public boolean released;
	private int releasetime;
	private boolean canWin;
	private int wintime;
	
	//lobby
	private int lobby_Player;
	
	//#######################################
	//Neues Spiel
	//#######################################
	public Game(DvZ p, int type) {
		this.gameType = type;
		
		this.state = 1;
		this.time = 30;//60;
		this.plugin = p;
		this.dauer = 0;
		ticker = 0;
		fastticker = 0;
		starting = false;
		monumentexists = false;
		enderActive = false;
		enderPortal = null;
		enderMan = "";
		autoassasin = false;
		a_ticker = 0;
		deaths = 0;
		infotimer = 0;
		dragon = null;
		
		mana = new ManaManager();
		
		globalCrystalChest = Bukkit.createInventory(null, 27, ConfigManager.getLanguage().getString("string_crystal_storage", "Crystal Storage"));
		crystalPerPlayer.clear();
		
		String wadd = "";
		for(int i=0; i<p.getGameID(this); i++) {
			wadd = wadd + " ";
		}
		waitm = new WaitingMenu(p, wadd);
		waitm.close();
		released = ConfigManager.getStaticConfig().getString("need_release", "false").equals("false");
		canWin = ConfigManager.getStaticConfig().getString("can_win", "false").equals("true");
		
		initLobby();
	}
	
	public void initLobby() {
		lobby_Player = plugin.getConfig().getInt("lobby_players", 20);
	}
	
	public void start(int time) {
		DVZGameStartEvent event = new DVZGameStartEvent(this);

		Bukkit.getServer().getPluginManager().callEvent(event);

		if (!event.isCancelled()){
			if(!starting) {
				starting = true;
				this.time = time;
			//change the countdown when allready starting
			} else {
				if(this.time>0) {
					this.time = time;
				}
			}
		}
	}
	
	public void reset(boolean callEvent) {
		starting = false;
		state = 1;
		
		if(callEvent) {
			DVZGameEndEvent event = new DVZGameEndEvent(this);
			Bukkit.getServer().getPluginManager().callEvent(event);
		}
		
		for(String playern : playerstate.keySet()) {			
			Player player = Bukkit.getServer().getPlayerExact(playern);
			
			if(player!=null) {
				//undisguise
				if(DvZ.api.isDisguised(player)) DvZ.api.undisguisePlayer(player);
				//clear potion effects
				for(PotionEffect pet : player.getActivePotionEffects()) {
					player.removePotionEffect(pet.getType());
					//TODO - find a way not using the workaround override method
					player.addPotionEffect(new PotionEffect(pet.getType(), 1, 1), true);
				}
				//clear inventory
				ItemHandler.clearInv(player);
				//reset health
				player.resetMaxHealth();
				player.setHealth(player.getMaxHealth());
				
				StatManager.hide(player, true);
			}
			
			StatManager.resetPlayer(playern);
		}
		
		//change between versions
		int type = ConfigManager.getStaticConfig().getInt("game"+plugin.getGameID(this), 1);
		this.gameType = GameType.fromID(type).getNextType(this.gameType);
		
		String[] players = playerstate.keySet().toArray(new String[playerstate.keySet().size()]);
		playerstate.clear();
		dauer = 0;
		ticker = 0;
		enderActive = false;
		monumentexists = false;
		enderPortal = null;
		enderMan = "";
		autoassasin = false;
		a_ticker = 0;
		deaths = 0;
		infotimer = 0;
		dragon = null;
		
		mana.reset();
		
		globalCrystalChest = Bukkit.createInventory(null, 27, ConfigManager.getLanguage().getString("string_crystal_storage", "Crystal Storage"));
		crystalPerPlayer.clear();
		
		waitm.releaseAll();
		waitm.close();
		released = ConfigManager.getStaticConfig().getString("need_release", "false").equals("false");
		canWin = ConfigManager.getStaticConfig().getString("can_win", "false").equals("true");
		
		spell1time.clear();
		spell2time.clear();
		spell3time.clear();
		spell4time.clear();
		invultimer.clear();
		customCooldown.clear();
		
		WorldManager.resetMainWorld(plugin.getGameID(this));
		
		//rejoin
		if(plugin.getConfig().getString("auto_rejoin", "false").equals("true")) {
			for(String playern : players) {
				Player player = Bukkit.getServer().getPlayerExact(playern);
				
				if(player!=null) {
					plugin.joinGame(player, this);
				}
			}
		}
	}
	
	//#######################################
	//Tick für Countdown
	//#######################################
	public void tick() {
		//Gamestatus zu Spielern senden
		if(!starting) {
			if(infotimer++>=30) {
				infotimer = 0;
				if (state==1) {
					broadcastMessage(ConfigManager.getLanguage().getString("string_lobby_waiting","Waiting for the Game to start..."));
					broadcastMessage(ConfigManager.getLanguage().getString("string_lobby_players","-0-/-1- Players for Game to start!").replace("-0-", ""+playerstate.size()).replace("-1-", ""+plugin.getConfig().getInt("lobby_players", 20)));
				}
			}
		//Autostart{
			if(lobby_Player>0) {
				if(playerstate.size()>=lobby_Player) {
					broadcastMessage(ConfigManager.getLanguage().getString("string_game_start","Game starting in -0- Seconds!").replace("-0-", ""+plugin.getConfig().getInt("lobby_starttime", 60)));
					start(plugin.getConfig().getInt("lobby_starttime", 60));
					
					if(plugin.getConfig().getInt("lobby_playerperassasin", 10)>0) {
						int assa = playerstate.size()/plugin.getConfig().getInt("lobby_playerperassasin", 10);
						if (assa==0) assa=1;
						assasins(plugin.getConfig().getInt("lobby_assasintime", 30), assa, plugin.getConfig().getInt("lobby_assasindeath", 2));
					}
				}
			}
		} else {
			if (time>=0) time--;
			
			if (time==60*5) broadcastMessage(ConfigManager.getLanguage().getString("string_starting_minutes","-0- Minutes left!").replace("-0-", "5"));
			else if (time==60) broadcastMessage(ConfigManager.getLanguage().getString("string_starting_minute","-0- Minute left!").replace("-0-", "1"));
			else if (time==10) broadcastMessage(ConfigManager.getLanguage().getString("string_starting_seconds","-0- Seconds left!").replace("-0-", "10"));
			else if (time==5) broadcastMessage(ConfigManager.getLanguage().getString("string_starting_seconds","-0- Seconds left!").replace("-0-", "5"));
			else if (time==4) broadcastMessage(ConfigManager.getLanguage().getString("string_starting_seconds","-0- Seconds left!").replace("-0-", "4"));
			else if (time==3) broadcastMessage(ConfigManager.getLanguage().getString("string_starting_seconds","-0- Seconds left!").replace("-0-", "3"));
			else if (time==2) broadcastMessage(ConfigManager.getLanguage().getString("string_starting_seconds","-0- Seconds left!").replace("-0-", "2"));
			else if (time==1) broadcastMessage(ConfigManager.getLanguage().getString("string_starting_second","-0- Second left!").replace("-0-", "1"));
			else if (time==0) startGame();//timeUp();
			
			if (time<=0) {
				dauer++;
				ticker++;
				
				if(ConfigManager.getStaticConfig().getString("global_stats", "true").equals("true"))
					updateGlobalStats();
				
				teleportUnreleased();
				
				if (ticker==10) {
					ticker = 0;
					checkLoose();
				}
				if(ticker%2==0) {
					DvZ.effectManager.playerEffects(this);
				}
				
				//Assasin controller
				if (autoassasin) {
					a_ticker++;
					if (a_ticker>=60) {
						a_ticker = 0;
						a_minutes--;
						if(a_minutes==0) {
							if(deaths<=a_maxdeaths) {
								addAssasins(a_count);
							}
						}
					}
				}
				
				//release the monsters
				if (releasetime>=0 && state==2) {
					releasetime--;
					if(releasetime==0 && state==2) {
						release();
					}
				}
				
				//dwarf victory
				if(released && canWin) {
					if(wintime>=0 && state==2) {
						wintime--;
						if(wintime==0 && state==2) {
							win();
						}
					}
				}
				
				countdownTicker();
			}
		}
	}
	
	//#######################################
	//Tick für Countdown
	//#######################################
	private void countdownTicker() {
		for(Map.Entry<String, Integer> e : spell1time.entrySet()){
			String player = e.getKey();
			int time = e.getValue();
			
			if(time>0) {
				time--; 
				if(time==0) countdownEnd(player, 1);
			}
			spell1time.put(player, time);
		}
		for(Map.Entry<String, Integer> e : spell2time.entrySet()){
			String player = e.getKey();
			int time = e.getValue();
			
			if(time>0) {
				time--; 
				if(time==0) countdownEnd(player, 2);
			}
			spell2time.put(player, time);
		}
		for(Map.Entry<String, Integer> e : spell3time.entrySet()){
			String player = e.getKey();
			int time = e.getValue();
			
			if(time>0) {
				time--; 
				if(time==0) countdownEnd(player, 3);
			}
			spell3time.put(player, time);
		}
		for(Map.Entry<String, Integer> e : spell4time.entrySet()){
			String player = e.getKey();
			int time = e.getValue();
			
			if(time>0) {
				time--; 
				if(time==0) countdownEnd(player, 4);
			}
			spell4time.put(player, time);
		}
		
		for(Map.Entry<String, Integer> e : invultimer.entrySet()){
			String player = e.getKey();
			int time = e.getValue();
			
			if(time>0) {
				time--; 
				if(time==0) addMonsterMap(Bukkit.getPlayerExact(player));
			}

			if(time==0) invultimer.remove(player);
			else invultimer.put(player, time);
		}
		//save for cuncurrentmodification
		ArrayList<String> remove = new ArrayList<String>();
		for(Map.Entry<String, Integer> e : customCooldown.entrySet()){
			String key = e.getKey();
			int time = e.getValue();
			
			time--; 
			customCooldown.put(key, time);
			if(time<=0) remove.add(key);
		}
		for(String st : remove) {
			customCooldown.remove(st);
		}
		
		mana.tick();
	}
	
	//fastticker 20 times per second
	public void fastTick() {
		fastticker++;
		
		if(fastticker>=5) {
			fastticker = 0;
			
			DvZ.effectManager.killEffects(this);
		}
	}

	//#######################################
	//Starte das wirkliche Spiel/oder geht weiter
	//#######################################
	//TODO - Start anders machen/moderator...
	private int taskid;
	public void startGame() {
		if (state==1) {
			final Game gea = this;
			
			WorldManager.newMainWorld(plugin.getGameID(gea));
			
			final int gtemp = plugin.getGameID(this);
			
			taskid = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
				public void run() {
					World w = Bukkit.getServer().getWorld(plugin.getConfig().getString("world_prefix", "DvZ_")+"Main"+gtemp+"");
					if(w!=null) {
						if(plugin.getConfig().getString("set_to_day","true")=="true") {
							w.setTime(0);
						}
						teleportToMainWorld();
						state = 2;
						releasetime = ConfigManager.getStaticConfig().getInt("time_release",30)*60;
						wintime = ConfigManager.getStaticConfig().getInt("time_win",30)*60;
						
						DvZ.startedGames += 1;
						
						for(Map.Entry<String, Integer> e : playerstate.entrySet()){
							String players = e.getKey();
							int pstate = e.getValue();
							
							if (pstate==1) {
								playerstate.put(players, Game.pickDwarf);
								Player player = Bukkit.getServer().getPlayer(players);
								if(player!=null) {
									player.getInventory().clear();
									player.resetMaxHealth();
									player.setHealth(player.getMaxHealth());
									player.setGameMode(GameMode.SURVIVAL);
									player.sendMessage(ConfigManager.getLanguage().getString("string_choose","Choose your class!"));
									addDwarfItems(player);
								}
							}
						}
						
						DvZ.itemStandManager.loadStands(w, new File(Bukkit.getServer().getWorldContainer().getPath()+"/"+w.getName()+"/dvz/itemstands/"));
						
						endTask();
					}
			    }
			}, 20, 20);
		}
	}
	private void endTask() {
		Bukkit.getServer().getScheduler().cancelTask(taskid);
	}
	
	//#######################################
	//Spiel verloren?
	//#######################################
	private void checkLoose() {
		//No more Dwarves
		int dwarf = 0;
		//int dwarfoff = 0;
		int mons = 0;
		int monsoff = 0;
		
		for(Map.Entry<String, Integer> e : playerstate.entrySet()){
			boolean online = false;
			Player player = Bukkit.getServer().getPlayerExact(e.getKey());
			if (player!=null) online = true;
			
			if (isDwarf(e.getKey(), true)) {
				if (online) {
					dwarf++; //else dwarfoff++;
					//only the last standing and online dwarf
					lastdwarf = e.getKey();
				}
			}
			if (isMonster(e.getKey())) {
				if (online) mons++; else monsoff++;
			}
		}
		
		if (dwarf==0 && (mons>=1 || monsoff>=1)) {
			broadcastMessage(ConfigManager.getLanguage().getString("string_lose_dwarf","§4Game Over!§f No more Dwarves!"));
			if(lastdwarf!=null)
				broadcastMessage(ConfigManager.getLanguage().getString("string_last_dwarf","Last standing Dwarf - §e-0-§f! Congratulations!").replace("-0-", lastdwarf));
			
			reset(true);
		}
		//monument destroyed
		boolean destr = false;
		int destroyed = 0;
		
		if(monumentexists) {
			Block block = monument.getWorld().getBlockAt(monument);
			Block block2;
	
			for(int i=0; i<=1; i++) {
				for(int j=0; j<=1; j++) {
					block2 = block.getRelative(i, 3, j);
					if(block2.getTypeId()!=116) 
						destroyed++;
				}
			}
			if(destroyed==4) {
				destr = true;
			}
		}
		monumentHealth = 100 - (int) Math.round((100/(double)4)*destroyed);
		
		if (destr) {
			broadcastMessage(ConfigManager.getLanguage().getString("string_lose_monument","§4Game Over!§f The Monument has been destroyed!"));

			broadcastMessage(ConfigManager.getLanguage().getString("string_lose_monument_dwarves","Dwarves who failed to protect the Monument:"));
			printSurvivingPlayers();
			
			reset(true);
		}
	}
	
	private void win() {
		broadcastMessage(ConfigManager.getLanguage().getString("string_win","§4Victory!§f The dwarves protected the Monument!"));
		
		broadcastMessage(ConfigManager.getLanguage().getString("string_win_dwarves","Dwarves who survived and protected the Monument:"));
		printSurvivingPlayers();
		
		reset(true);
	}
	
	private void printSurvivingPlayers() {
		String pmessage = "";
		int pcount = 0;
		int pmaxCount = 5;
		
		for(Map.Entry<String, Integer> e : playerstate.entrySet()){
			Player player = Bukkit.getServer().getPlayerExact(e.getKey());
			
			//only online players
			if (player!=null) {
				if (isDwarf(e.getKey(), true)) {
					pmessage = pmessage + e.getKey() + ",";
					pcount++;
					if(pcount>=pmaxCount) {
						broadcastMessage(pmessage);
						
						pmessage = "";
						pcount = 0;
					}
				}
			}
		}
		if(!pmessage.equals(""))
			broadcastMessage(pmessage);
	}
	
	private void updateGlobalStats() {
		int dwarf = 0;
		int assa = 0;
		int mons = 0;
		
		for(Map.Entry<String, Integer> e : playerstate.entrySet()){
			if (isDwarf(e.getKey(), false)) {
				//online check
				if(Bukkit.getServer().getPlayer(e.getKey())!=null)
					dwarf++;
			} else if (isDwarf(e.getKey(), true)) {
				//online check
				if(Bukkit.getServer().getPlayer(e.getKey())!=null)
					assa++;
			}
			if (isMonster(e.getKey())) {
				//online check
				if(Bukkit.getServer().getPlayer(e.getKey())!=null)
					mons++;
			}
		}
		
		StatManager.setGlobalStat(ConfigManager.getLanguage().getString("scoreboard_dwarves", "Dwarves"), dwarf);
		StatManager.setGlobalStat(ConfigManager.getLanguage().getString("scoreboard_assassins", "Assassins"), assa);
		StatManager.setGlobalStat(ConfigManager.getLanguage().getString("scoreboard_monsters", "Monsters"), mons);
		if(monumentexists) {
			StatManager.setGlobalStat(ConfigManager.getLanguage().getString("scoreboard_monument", "Monument %"), monumentHealth);
		}
		
		if(!released) {
			StatManager.setTimeStat(ConfigManager.getLanguage().getString("scoreboard_release", "M.Release"), releasetime);
		} else if(canWin) {
			StatManager.setTimeStat(ConfigManager.getLanguage().getString("scoreboard_release", "M.Release"), 0);
			StatManager.setTimeStat(ConfigManager.getLanguage().getString("scoreboard_victory", "Victory"), wintime);
		}
	}
	
	//#######################################
	//Gehört dieser block zum monument
	//#######################################
	public boolean isMonument(Block b) {
		if(monumentexists) {
			if (b.getWorld()==monument.getWorld()) {
				Block block = monument.getWorld().getBlockAt(monument);
				Block block2;
		
				for(int i=0; i<=1; i++) {
					for(int j=0; j<=1; j++) {
						block2 = block.getRelative(i, 3, j);
						if(block2.getLocation().distanceSquared(b.getLocation())<1)
							return true;
					}
				}
			}
		}
		return false;
	}
	
	//Autoassasins :P
	public void assasins(int minutes, int count, int maxdeaths) {
		autoassasin = true;
		a_minutes = minutes;
		a_count = count;
		a_maxdeaths = maxdeaths;
	}
	
	//Add Assasins
	public void addAssasins(int count) {
		Random rand = new Random();
		for(int i=0; i<count; i++) {
			Object[] rplayers = playerstate.keySet().toArray();
			String playern = (String) rplayers[rand.nextInt(rplayers.length)];
			Player player = Bukkit.getServer().getPlayerExact(playern);
			
			//check for Playercount
				int ammountPlayers = 0;
				for (int j=0; j<rplayers.length; j++) {
					playern = (String) rplayers[j];
					if((playerstate.get(playern)>=10 && playerstate.get(playern)<30) && player!=null) {
						ammountPlayers += 1;
					}
				}
				if (ammountPlayers<count) {
					broadcastMessage(ConfigManager.getLanguage().getString("string_no_assasins","No Assasins have been chosen - Because there where not enough online Dwarves!!"));
					return;
				}
			while(!(playerstate.get(playern)>=dwarfMin && playerstate.get(playern)<=dwarfMax) || player==null) {
				playern = (String) rplayers[rand.nextInt(rplayers.length)];
				player = Bukkit.getServer().getPlayerExact(playern);
			}
			
			//Player player = Bukkit.getServer().getPlayerExact(playern);
			//if(player!=null) {
				player.sendMessage(ConfigManager.getLanguage().getString("string_become_assasin","You have been chosen to be a Assasin!"));
				
				playerstate.put(player.getName(), Game.assasinState);
				
				//time
				int asstime = ConfigManager.getClassFile().getInt("assasin_time_minutes",5);
				if(asstime>0) {
					spell3time.put(player.getName(), asstime*60);
					player.sendMessage(ConfigManager.getLanguage().getString("string_become_assasin_time","If you don't kill someone within the next -0- minutes you will die!").replace("-0-", ""+asstime));
				}
				
				//add assasin items to inventory
				PlayerInventory inv = player.getInventory();
				List<String> itemstrings = ConfigManager.getClassFile().getStringList("assasin_items");
				for(int j=0; j<itemstrings.size(); j++) {
					ItemStack it = ItemHandler.decodeItem(itemstrings.get(j));
					if(it!=null) {
						inv.addItem(it);
					}
				}
				/*for(int j=1; j<=10; j++) {
					ItemStack it = ItemHandler.decodeItem(plugin.getConfig().getString("assasin_item"+j, "0"));
					if(it!=null) {
						inv.addItem(it);
					}
				}*/
			//}
		}
		broadcastMessage(ConfigManager.getLanguage().getString("string_assasins","-0- Assasins have been chosen!!").replace("-0-", ""+count));
	}
	
	//#######################################
	//Countdown Over
	//#######################################
	public void countdownEnd(String player, int countdown) {
		//assasin
		if(playerstate.get(player)==Game.assasinState) {
			if(countdown==3) {
				Player playern = Bukkit.getServer().getPlayerExact(player);
				if(playern!=null) {
					playern.damage((double) 1000);
					playern.sendMessage(ConfigManager.getLanguage().getString("string_assasin_timeup","Your time is up!"));
				}
			}
		}
	}
	
	//#######################################
	//Dwarf Items hinzufügen
	//#######################################
	public void addDwarfItems(final Player player) {
		Random rand = new Random();
		PlayerInventory inv = player.getInventory();
		
		resetCountdowns(player.getName());
		
		ItemStack[] dwarfItems = new ItemStack[DvZ.dwarfManager.getCount()];
		
		for(int i=0; i<DvZ.dwarfManager.getCount(); i++) {
			dwarfItems[i] = new ItemStack(DvZ.dwarfManager.getDwarf(i).getClassItem(), 1, (short)DvZ.dwarfManager.getDwarf(i).getClassItemDamage());
			ItemMeta cim = dwarfItems[i].getItemMeta();
			cim.setDisplayName(ConfigManager.getLanguage().getString("string_become","Become -0-").replace("-0-", DvZ.dwarfManager.getDwarf(i).getName()));
			dwarfItems[i].setItemMeta(cim);
		}
		
		//costum dwarves
		if(plugin.getConfig().getString("new_classselection","true")!="true") {
			for(int i=0; i<DvZ.dwarfManager.getCount(); i++) {
				if(rand.nextInt(100)<DvZ.dwarfManager.getDwarf(i).getClassChance() || player.hasPermission("dvz.allclasses") || player.hasPermission("dvz.alldwarves")) {
					//game type
					int gID = DvZ.dwarfManager.getDwarf(i).getGameId();
					if(gID==0 || gID==GameType.getDwarfAndMonsterTypes(getGameType())) {
						//permissions
						if(player.hasPermission("dvz.dwarves."+i)) {
							inv.addItem(dwarfItems[i]);
						}
					}
				}
			}
		}
		else
		{
			final IconMenu im = new IconMenu(player.getName()+" - "+ConfigManager.getLanguage().getString("string_choose","Choose your class!"), 9, new GameOptionClickEventHandler(this) {
				
	            @Override
	            public void onOptionClick(IconMenu.OptionClickEvent event) {
	            	if(!isPlayer(event.getPlayer().getName())) {
	            		event.setWillClose(true);
	                    event.setWillDestroy(true);
	                    return;
	            	}
	            	
	            	boolean dwarf = false;
	            	int itemId = event.getItemID();
	            	int itemD = event.getItemDamage();
	            	final Player player = event.getPlayer();
	    			
	    			for(int i=0; i<DvZ.dwarfManager.getCount(); i++) {
	    				CustomDwarf cm = DvZ.dwarfManager.getDwarf(i);
	    				if(itemId==cm.getClassItem() && itemD==cm.getClassItemDamage()) {
	    					cm.becomeDwarf(game, player);
	    					dwarf = true;
	    				}
	    			}
	    			
	    			if (dwarf) {
	    				SpecialPlayer sp = DvZ.playerManager.getPlayer(player.getName());
	    				if(sp!=null) {
	    					sp.addCrytalItems(game, player);
	    				}
	    				
	    				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
							public void run() {
								if(spawnDwarves!=null) {
			    					player.teleport(spawnDwarves);
			    				}
							}
						}, 1);
	    				
	    				event.setWillClose(true);
	                    event.setWillDestroy(true);
	    			}
	                //event.getPlayer().sendMessage("You have chosen " + event.getName());
	            }
	        },  plugin);

			//adding
			int pos = 0;
			for(int i=0; i<DvZ.dwarfManager.getCount(); i++) {
				if(rand.nextInt(100)<DvZ.dwarfManager.getDwarf(i).getClassChance() || player.hasPermission("dvz.allclasses") || player.hasPermission("dvz.alldwarves")) {
					//game type
					int gID = DvZ.dwarfManager.getDwarf(i).getGameId();
					if(gID==0 || gID==GameType.getDwarfAndMonsterTypes(getGameType())) {
						//permissions
						if(player.hasPermission("dvz.dwarves."+i)) {
							im.setOption(pos, dwarfItems[i]); 
							pos++;
						}
					}
				}
			}
			
			//Delay to let teleport get throug
			Bukkit.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
				@Override
				public void run() {
					im.open(player);
				}
			}, 2);
		}
	}
	
	//#######################################
	//Monster Items hinzufügen
	//#######################################
	public void addMonsterItems(final Player player) {
		Random rand = new Random();
		PlayerInventory inv = player.getInventory();

		resetCountdowns(player.getName());
		
		ItemStack[] monsterItems = new ItemStack[DvZ.monsterManager.getCount()];
		
		for(int i=0; i<DvZ.monsterManager.getCount(); i++) {
			monsterItems[i] = new ItemStack(DvZ.monsterManager.getMonster(i).getClassItem(), 1, (short)DvZ.monsterManager.getMonster(i).getClassItemDamage());
			ItemMeta cim = monsterItems[i].getItemMeta();
			cim.setDisplayName(ConfigManager.getLanguage().getString("string_become","Become -0-").replace("-0-", DvZ.monsterManager.getMonster(i).getName()));
			monsterItems[i].setItemMeta(cim);
		}
		
		if(plugin.getConfig().getString("new_classselection","true")!="true") {
			for(int i=0; i<DvZ.monsterManager.getCount(); i++) {
				if(rand.nextInt(100)<DvZ.monsterManager.getMonster(i).getClassChance() || player.hasPermission("dvz.allclasses") || player.hasPermission("dvz.allmonsters")) {
					//game type
					int gID = DvZ.monsterManager.getMonster(i).getGameId();
					if(gID==0 || gID==GameType.getDwarfAndMonsterTypes(getGameType())) {
						//permissions
						if(player.hasPermission("dvz.monster."+i)) {
							inv.addItem(monsterItems[i]);
						}
					}
				}
			}
		}
		else
		{
			final IconMenu icm = new IconMenu(player.getName()+" - "+ConfigManager.getLanguage().getString("string_choose","Choose your class!"), 18, new GameOptionClickEventHandler(this) {
				
	            @Override
	            public void onOptionClick(IconMenu.OptionClickEvent event) {
	            	if(!isPlayer(event.getPlayer().getName())) {
	            		event.setWillClose(true);
	                    event.setWillDestroy(true);
	                    return;
	            	}
	            	
	            	boolean monster = false;
	            	int itemId = event.getItemID();
	            	int itemD = event.getItemDamage();
	            	final Player player = event.getPlayer();
	            	
	    			for(int i=0; i<DvZ.monsterManager.getCount(); i++) {
	    				CustomMonster cm = DvZ.monsterManager.getMonster(i);
	    				if(itemId==cm.getClassItem() && itemD==cm.getClassItemDamage()) {
	    					cm.becomeMonster(game, player);
	    					monster = true;
	    				}
	    			}
	    			
	    			if (monster) {
	    				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
							public void run() {
								if(spawnMonsters!=null) {
			    					player.teleport(spawnMonsters);
			    				}
							}
						}, 1);
	    				
	    				event.setWillClose(true);
	                    event.setWillDestroy(true);
	    			}
	            }
	        },  plugin);
			
			//adding
			int pos = 0;
			for(int i=0; i<DvZ.monsterManager.getCount(); i++) {
				if(rand.nextInt(100)<DvZ.monsterManager.getMonster(i).getClassChance() || player.hasPermission("dvz.allclasses") || player.hasPermission("dvz.allmonsters")) {
					//game type
					int gID = DvZ.monsterManager.getMonster(i).getGameId();
					if(gID==0 || gID==GameType.getDwarfAndMonsterTypes(getGameType())) {
						//permissions
						if(player.hasPermission("dvz.monster."+i)) {
							icm.setOption(pos, monsterItems[i]); 
							pos++;
						}
					}
				}
			}
			
			//Delay to let teleport get throug
			Bukkit.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
				@Override
				public void run() {
					icm.open(player);
				}
			}, 2);
		}
	}
	
	//#######################################
	//Spieler hat rechtsgeklickt
	//#######################################
	public void playerRC(PlayerInteractEvent event, Player player, ItemStack item, Block block) {
		if(!isPlayer(player.getName())) return;
		if(item==null) return;
		int itemId = item.getTypeId();
		int itemD = item.getDurability();
		String pname = player.getName();
		
		if(getPlayerState(pname)==2) { //dwarf werden
			boolean dwarf = false;
			
			//costum dwarves
			for(int i=0; i<DvZ.dwarfManager.getCount(); i++) {
				CustomDwarf cm = DvZ.dwarfManager.getDwarf(i);
				if(itemId==cm.getClassItem() && itemD==cm.getClassItemDamage()) {
					cm.becomeDwarf(this, player);
					dwarf = true;
				}
			}
			
			if (dwarf) {
				SpecialPlayer sp = DvZ.playerManager.getPlayer(player.getName());
				if(sp!=null) {
					sp.addCrytalItems(this, player);
				}
				
				if(spawnDwarves!=null) {
					player.teleport(spawnDwarves);
				}
			}
		}
		if(getPlayerState(pname)==3) { //monster werden
			boolean monster = false;
			
			for(int i=0; i<DvZ.monsterManager.getCount(); i++) {
				CustomMonster cm = DvZ.monsterManager.getMonster(i);
				if(itemId==cm.getClassItem() && itemD==cm.getClassItemDamage()) {
					cm.becomeMonster(this, player);
					monster = true;
				}
			}
			if (monster) {
				if(spawnMonsters!=null) {
					player.teleport(spawnMonsters);
				}
			}
		}
		
		//disable clicking when monsters are not released
		if(isMonster(player.getName()) && !released) {
			return;
		}
		
		//custom dwarves - rightclick
		if(isDwarf(player.getName(), false)) {
			int dId = getPlayerState(player.getName())-dwarfMin;
			if(dId>=0 && dId<DvZ.monsterManager.getCount()) {
				CustomDwarf cd = DvZ.dwarfManager.getDwarf(dId);
				//spell
				if(cd.isSpellEnabled()) {
					if(itemId==cd.getSpellItem()) {
						cd.spell(this, player);
					}
				}
				//transmute items
				if(block!=null) {
					if(cd.transmuteItemOnBlock(this, player, item, block)) {
						event.setCancelled(true);
					}
				}
			}
		}
		
		if(isDwarf(pname, true) && itemId==121) Spellcontroller.spellDisablePortal(this, player);
		//crstal chest is no longer a global config option
		//if(isDwarf(pname) && itemId==388) Spellcontroller.spellEnderChest(this, player, getCrystalChest(pname, false), getCrystalChest(pname, true));
		
		//custom items
		playerSpecialItemC(player, item, 1, block, null);
		
		//Monster
		if(isMonster(pname) && itemId==358) Spellcontroller.spellTeleport(this, player);
		if(isMonster(pname) && itemId==370) Spellcontroller.spellSuizide(this, player);
		
		//dragon
		if(dragon!=null) {
			if(dragon instanceof PlayerDragon) {
				if(player == dragon.getEntity()) {
					((PlayerDragon) dragon).playerRC(item, block);
				}
			}
		}
	}
	
	//#######################################
	//Spieler hat rechtsgeklickt auf anderen Spieler
	//#######################################
	public void playerRCPlayer(Player player, ItemStack item, Player target) {
		if(!isPlayer(player.getName())) return;
		if(item==null) return;
		
		//disable clicking when monsters are not released
		if(isMonster(player.getName()) && !released) {
			return;
		}
		
		//custom items
		playerSpecialItemC(player, item, 1, null, target);
	}
	
	//#######################################
	//Spieler hat linksgeklickt
	//#######################################
	public void playerLC(Player player, ItemStack item, Block block) {
		if(!isPlayer(player.getName())) return;
		if(item==null) return;
		int itemId = item.getTypeId();
		//int itemD = item.getDurability();
		String pname = player.getName();
		
		//disable clicking when monsters are not released
		if(isMonster(player.getName()) && !released) {
			return;
		}
		
		//custom items
		playerSpecialItemC(player, item, 0, block, null);
		
		if(itemId == 373 && isDwarf(pname, true)) {
			//changed from old hacky potionhandler to new bukkit functionallity
			if(ExperienceUtils.getCurrentExp(player)>=plugin.getConfig().getInt("dwarf_potion_exp", 2)) {
				ExperienceUtils.changeExp(player, -plugin.getConfig().getInt("dwarf_potion_exp", 2));
				ThrownPotion thrp = player.launchProjectile(ThrownPotion.class);
				thrp.setItem(item);
			} else {
				player.sendMessage(ConfigManager.getLanguage().getString("string_needexp","You don't have enough exp!"));
			}
			//Spellcontroller.spellLaunchPotion(this, player, itemD);
		}
	}
	
	//#######################################
	//Spieler hat geklickt custom item
	//actions:
	//0 = leftclick
	//1 = rigthclick
	//2 = eat
	//#######################################
	public void playerSpecialItemC(Player player, ItemStack item, int action, Block block, Player target) {
		String pname = player.getName();
		
		if(isPlayer(pname)) {
			ItemMeta im = item.getItemMeta();
			if(im!=null)
			if(im.hasDisplayName()) {
				List<CustomItem> cil = DvZ.itemManager.getItemByDisplayName(im.getDisplayName());
				if(cil!=null) {
					for(int i=0; i<cil.size(); i++) {
						CustomItem ci = cil.get(i);

						if(ci.isThisItem(item)) {
							if(block!=null)
								ci.cast(this, action, player, block);
							else if(target!=null)
								ci.cast(this, action, player, target);
							else
								ci.cast(this, action, player);
						}
					}
				}
			}
		}
	}
	
	public void playerEat(PlayerItemConsumeEvent event, Player player, ItemStack item) {
		if(!isPlayer(player.getName())) return;
		if(item==null) return;
		
		//custom items
		playerSpecialItemC(player, item, 2, null, null);
	}
	
	public void playerBreakBlock(Player player, Block block) {
		if(isDwarf(player.getName(), false)) {
			int dId = getPlayerState(player.getName()) - Game.dwarfMin;
			CustomDwarf cd = DvZ.dwarfManager.getDwarf(dId);
			
			if(cd != null)
				cd.transmuteItemOnBreak(this, player, block);
		}
	}
	
	//#######################################
	//Anfang des Spieles Spieler hizufügen
	//#######################################
	public boolean addPlayer(String player) {
		//nur wenn noch nicht eingetragen und spiel nicht gestartet
		if (!playerstate.containsKey(player) && state==1) {
			playerstate.put(player, 1);	//nix, pregame
			spell1time.put(player, 0);  //spellcountdown
			spell2time.put(player, 0);  //spellcountdown
			spell3time.put(player, 0);  //spellcountdown
			spell4time.put(player, 0);  //spellcountdown
			DvZ.log(player+" added to the Game.");
			return true;
		}
		return false;
	}
	
	//#######################################
	//Spieler entfernen
	//#######################################
	public void removePlayer(String player) {
		if(playerstate.containsKey(player)) {
			playerstate.remove(player);
		}
	}
	
	//#######################################
	//Monster unverwundbar für ... Sekunden
	//#######################################
	public void addMonsterBuff(Player player) {
		int time = plugin.getConfig().getInt("monster_invulnarable", 30);
		
		player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, time*20, 4), false);
		player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, time*20, 6), false);
		
		player.sendMessage(ConfigManager.getLanguage().getString("string_invulnarable","You are -0- seconds invulnarable!").replace("-0-", ""+time));
		
		invultimer.put(player.getName(), time);
	}
	
	private void addMonsterMap(Player player) {
		//only monsters are allowed to get these items
		if(getPlayerState(player.getName())>=Game.monsterMin && getPlayerState(player.getName())<=Game.monsterMax) {
			PlayerInventory inv = player.getInventory();
			
			ItemStack it = new ItemStack(358, 1);
			ItemMeta im = it.getItemMeta();
			im.setDisplayName(ConfigManager.getLanguage().getString("string_spell_teleport","Teleport to Enderman Portal"));
			ArrayList<String> li4 = new ArrayList<String>();
			li4.add(ConfigManager.getLanguage().getString("string_used_seconds","Can be used every -0- Seconds!").replace("-0-", ""+plugin.getConfig().getInt("spelltime_teleport",15)));
			im.setLore(li4);
			it.setItemMeta(im);
			inv.addItem(it);
			
			if(plugin.getConfig().getString("monster_suizidepill", "true")=="true") {
				it = new ItemStack(370, 1);
				im = it.getItemMeta();
				im.setDisplayName(ConfigManager.getLanguage().getString("string_spell_suizide","Suizidepill"));
				it.setItemMeta(im);
				inv.addItem(it);
			}
			
			DvZ.updateInventory(player);
		}
	}
	
	public boolean isBuffed(String player) {
		return invultimer.containsKey(player);
	}
	
	//#######################################
	//Creating the Monument
	//#######################################
	public void createMonument(boolean obsi) {
		Block block = monument.getWorld().getBlockAt(monument);
		Block block2;
		
		for(int i=-1; i<=2; i++) {
			for(int j=-1; j<=2; j++) {
				if(obsi) {
					block2 = block.getRelative(i, 0, j);
					block2.setTypeId(49);
				}
			}
		}
		
		for(int i=0; i<=1; i++) {
			for(int j=0; j<=1; j++) {
				if(obsi) {
					block2 = block.getRelative(i, 1, j);
					block2.setTypeId(49);
					block2 = block.getRelative(i, 2, j);
					block2.setTypeId(49);
				}
				block2 = block.getRelative(i, 3, j);
				block2.setTypeId(116);
			}
		}
	}
	
	//#######################################
	//Spieler schon registriert?
	//#######################################
	public boolean isPlayer(String player) {
		return playerstate.containsKey(player);
	}
	
	//#######################################
	//Bekomme Playerstate
	//#######################################
	public int getPlayerState(String player) {
		if (playerstate.containsKey(player))
			return playerstate.get(player);
		else
			return 0;
	}
	public boolean isDwarf(String player, boolean assassins) {
		if(playerstate.containsKey(player)) {
			int pstate = playerstate.get(player);
			if((pstate>=Game.dwarfMin && pstate<=Game.dwarfMax)
			  || pstate==Game.dragonWarrior) {
				return true;
			}
			
			if(assassins && pstate==Game.assasinState) {
				return true;
			}
		}
		
		return false;
	}
	public boolean isMonster(String player) {
		if(playerstate.containsKey(player)) {
			int pstate = playerstate.get(player);
			if(pstate>=Game.monsterMin && pstate<=Game.monsterMax) {
				return true;
			}
		}
		
		return false;
	}
	
	//#######################################
	//Setze Playerstate
	//#######################################
	public void setPlayerState(String player, int pstate) {
		playerstate.put(player, pstate);
	}
	
	//#######################################
	//Alle Spieler neu "Disguisen" - Temporärer Workaround
	//#######################################
	public void redisguisePlayers() {
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				Object[] rplayers = playerstate.keySet().toArray();
				for(int i=0; i<rplayers.length; i++) {
					String playern = (String) rplayers[i];
					Player player = Bukkit.getServer().getPlayerExact(playern);
					
					if (player!=null) {
						if( DvZ.api.isDisguised(player)) {
							Disguise dg = DvZ.api.getDisguise(player);
							DvZ.api.undisguisePlayer(player);
							DvZ.api.disguisePlayer(player, dg);
						}
					}
				}
		    }
		}, 2);		
	}
	
	//#######################################
	//Lade Gameinfo von DvZ_Main
	//#######################################
	private int taskid2;
	public void loadGameInfo() {
		File wf = new File(Bukkit.getServer().getWorldContainer().getPath()+"/"+plugin.getConfig().getString("world_prefix", "DvZ_")+"Main"+plugin.getGameID(this)+"/");
		
		if(wf.exists()) {
			final File spawnD = (new File(wf+"/dvz/", "dvz_spawn_d.dat").exists()) ?
					new File(wf+"/dvz/", "dvz_spawn_d.dat")
					: new File(wf, "dvz_spawn_d.dat");
			final File spawnM = (new File(wf+"/dvz/", "dvz_spawn_m.dat").exists()) ?
					new File(wf+"/dvz/", "dvz_spawn_m.dat")
					: new File(wf, "dvz_spawn_m.dat");
			final File monF = (new File(wf+"/dvz/", "dvz_mon.dat").exists()) ?
					new File(wf+"/dvz/", "dvz_mon.dat")
					: new File(wf, "dvz_mon.dat");		
			
			final int gtemp = plugin.getGameID(this);
			
			taskid2 = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
				public void run() {
					World w = Bukkit.getServer().getWorld(plugin.getConfig().getString("world_prefix", "DvZ_")+"Main"+gtemp+"");
					if(w!=null) {
						if(spawnD.exists()) {
							try {
								String st = (String) Slapi.load(spawnD.getPath());
								String[] strings = st.split(":");
								spawnDwarves = new Location(Bukkit.getServer().getWorld(plugin.getConfig().getString("world_prefix", "DvZ_")+"Main"+gtemp+""), 
										Double.parseDouble(strings[0]), Double.parseDouble(strings[1]), Double.parseDouble(strings[2]), Float.parseFloat(strings[3]), Float.parseFloat(strings[4]));
							} catch (Exception e) {}
						}
						if(spawnM.exists()) {
							try {
								String st = (String) Slapi.load(spawnM.getPath());
								String[] strings = st.split(":");
								spawnMonsters = new Location(Bukkit.getServer().getWorld(plugin.getConfig().getString("world_prefix", "DvZ_")+"Main"+gtemp+""), 
										Double.parseDouble(strings[0]), Double.parseDouble(strings[1]), Double.parseDouble(strings[2]), Float.parseFloat(strings[3]), Float.parseFloat(strings[4]));
							} catch (Exception e) {}
						}
						if(monF.exists()) {
							try {
								String st = (String) Slapi.load(monF.getPath());
								String[] strings = st.split(":");
								monument = new Location(Bukkit.getServer().getWorld(plugin.getConfig().getString("world_prefix", "DvZ_")+"Main"+gtemp+""), 
										Double.parseDouble(strings[0]), Double.parseDouble(strings[1]), Double.parseDouble(strings[2]));
								monumentexists = true;
								createMonument(false);
							} catch (Exception e) {}
						}
						
						endTask2();
					}
			    }
			}, 20, 20);
		}
	}
	private void endTask2() {
		Bukkit.getServer().getScheduler().cancelTask(taskid2);
	}
	
	//#######################################
	//Monsterrelease
	//#######################################
	public void release() {
		released = true;
		
		Object[] rplayers = playerstate.keySet().toArray();
		for(int i=0; i<rplayers.length; i++) {
			String playern = (String) rplayers[i];
			Player player = Bukkit.getServer().getPlayerExact(playern);

			if(player!=null) {
				waitm.release(player);
			}
		}
		
		broadcastMessage(ConfigManager.getLanguage().getString("string_release", "The Monsters have been released!"));
	}
	
	//teleport unreleased monsters back to their spawn
	public void teleportUnreleased() {
		if(released) return;
		
		int minutes = (int) Math.floor(releasetime/60);
		int seconds = releasetime - minutes*60;
		String message = ConfigManager.getLanguage().getString("string_release_wait", "&cMonsters are not released yet!");
		String message2 = ConfigManager.getLanguage().getString("string_release_time", "&cMaximum Time until release: &6-0- Minutes -1- Seconds").replace("-0-", ""+minutes).replace("-1-", ""+seconds);
		
		Object[] rplayers = playerstate.keySet().toArray();
		for(int i=0; i<rplayers.length; i++) {
			String playern = (String) rplayers[i];
			if(isMonster(playern)) {
				Player player = Bukkit.getServer().getPlayerExact(playern);
	
				if(player!=null) {
					if (ticker==10) {
						player.sendMessage(message);
						player.sendMessage(message2);
					}
					
					Location loc = player.getLocation();
					Location target = Bukkit.getServer().getWorld(plugin.getConfig().getString("world_prefix", "DvZ_")+"Main"+plugin.getGameID(this)+"").getSpawnLocation();
					
					if(spawnMonsters!=null) {
						target = spawnMonsters;
					}
					
					if(loc.distanceSquared(target)>2) {
						player.teleport(target);
					}
				}
			}
		}
	}
	
	public void setDragon(Dragon dragon2) {
		dragon = dragon2;
	}
	
	//#######################################
	//Gamestate setzen
	//#######################################
	public void setGameState(int newstate) {
		state = newstate;
	}
	
	//#######################################
	//Teleportiert alle Spieler zum Spawn der Mainwelt
	//#######################################
	public void teleportToMainWorld() {
		World w = Bukkit.getServer().getWorld(plugin.getConfig().getString("world_prefix", "DvZ_")+"Main"+plugin.getGameID(this)+"");
		Object[] rplayers = playerstate.keySet().toArray();
		for(int i=0; i<rplayers.length; i++) {
			String playern = (String) rplayers[i];
			Player player = Bukkit.getServer().getPlayerExact(playern);
			
			if (player!=null && w!=null) {
				player.teleport(w.getSpawnLocation());
			}
		}
	}
	
	//#######################################
	//Broadcast Message nur für die Spieler
	//#######################################
	public void broadcastMessage(String message) {
		Object[] rplayers = playerstate.keySet().toArray();
		for(int i=0; i<rplayers.length; i++) {
			String playern = (String) rplayers[i];
			Player player = Bukkit.getServer().getPlayerExact(playern);
			
			if(player!=null) {
				player.sendMessage(message);
			}
		}
	}
	
	public int getCountdown(String player, int id) {
		switch(id) {
		case 1:
			return spell1time.get(player);
		case 2:
			return spell2time.get(player);
		case 3:
			return spell3time.get(player);
		case 4:
			return spell4time.get(player);
		default:
			return 1;
		}
	}
	
	public void setCountdown(String player, int id, int t) {
		switch(id) {
		case 1:
			spell1time.put(player, t);
			break;
		case 2:
			spell2time.put(player, t);
			break;
		case 3:
			spell3time.put(player, t);
			break;
		case 4:
			spell4time.put(player, t);
			break;
		default: break;
		}
	}
	
	public void resetCountdowns(String player) {
		spell1time.put(player, 0);
		spell2time.put(player, 0);
		spell3time.put(player, 0);
		spell4time.put(player, 0);
		invultimer.remove(player);
		crystalPerPlayer.remove(player);
	}
	
	public void setCustomCooldown(String player, String name, int time) {
		customCooldown.put(player+":"+name, time);
	}
	public int getCustomCooldown(String player, String name) {
		if(customCooldown.containsKey(player+":"+name)) {
			return customCooldown.get(player+":"+name);
		}
		
		return -1;
	}
	public void resetCustomCooldown(String player, String name) {
		customCooldown.remove(player+":"+name);
	}
	
	public Inventory getCrystalChest(String pname, boolean global) {
		if(global) {
			return globalCrystalChest;
		} else {
			if(!crystalPerPlayer.containsKey(pname)) crystalPerPlayer.put(pname, Bukkit.createInventory(null, 27, ConfigManager.getLanguage().getString("string_crystal_storage", "Crystal Storage")));
			
			return crystalPerPlayer.get(pname);
		}
	}
	
	public int getGameType() {
		return gameType;
	}
	
	public int getState() {
		return state;
	}
	
	public boolean getStarting() {
		return starting;
	}
	
	public boolean isRunning() {
		return state>1;
	}
	
	public int getStartTime() {
		return time;
	}
	
	public int getDauer() {
		return dauer;
	}
	
	public ManaManager getManaManager() {
		return mana;
	}
	
	public DvZ getPlugin() {
		return plugin;
	}
}
