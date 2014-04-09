package me.andre111.dvz;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import me.andre111.dvz.config.ConfigManager;
import me.andre111.dvz.disguise.DisguiseSystemHandler;
import me.andre111.dvz.dragon.Dragon;
import me.andre111.dvz.dragon.PlayerDragon;
import me.andre111.dvz.dwarf.CustomDwarf;
import me.andre111.dvz.event.DVZGameEndEvent;
import me.andre111.dvz.event.DVZGameStartEvent;
import me.andre111.dvz.manager.HighscoreManager;
import me.andre111.dvz.manager.PlayerScore;
import me.andre111.dvz.manager.StatManager;
import me.andre111.dvz.manager.WorldManager;
import me.andre111.dvz.monster.CustomMonster;
import me.andre111.dvz.players.SpecialPlayer;
import me.andre111.dvz.utils.ExperienceUtils;
import me.andre111.dvz.utils.GameOptionClickEventHandler;
import me.andre111.dvz.utils.IconMenu;
import me.andre111.dvz.utils.InventoryHandler;
import me.andre111.dvz.utils.PlayerHandler;
import me.andre111.dvz.utils.Slapi;
import me.andre111.dvz.utils.WaitingMenu;
import me.andre111.dvz.volatileCode.DvZPackets;
import me.andre111.items.ItemHandler;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class Game {
	private DvZ plugin;
	
	private int gameType;
	
	private int state;
	private int starttime;
	
	private boolean voting;
	private HashMap<Integer, Integer> votes = new HashMap<Integer, Integer>();
	private int maxVote;
	private boolean currentlyVoting;
	private ArrayList<UUID> votingPlayers = new ArrayList<UUID>();
	
	private int dauer;
	private int ticker;
	private int fastticker;
	private boolean starting;
	
	public Location spawnDwarves;
	public Location spawnMonsters;
	
	public boolean enderActive;
	public Location enderPortal;
	public UUID enderMan;
	
	public Location monument;
	public boolean monumentexists;
	private int monumentHealth;
	private String lastdwarf;
	
	private Inventory globalCrystalChest;
	private HashMap<UUID, Inventory> crystalPerPlayer = new HashMap<UUID, Inventory>();
	
	public HashMap<UUID, Integer> playerstate = new HashMap<UUID, Integer>();
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
	
	//used for custom cooldowns String: Playeruuidstring||CooldownName
	private HashMap<String, Integer> customCooldown = new HashMap<String, Integer>();
	
	//monument distance counting down to 0
	private HashMap<UUID, Integer> monDistance = new HashMap<UUID, Integer>();
	
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
	private int gracetime;
	
	//lobby
	private int lobby_Player;
	
	//#######################################
	//Neues Spiel
	//#######################################
	public Game(DvZ p, int type) {
		gameType = type;
		
		state = 1;
		starttime = 30;//60;
		
		voting = ConfigManager.getStaticConfig().getBoolean("lobby_voting", false);
		currentlyVoting = false;
		votingPlayers.clear();
		
		plugin = p;
		dauer = 0;
		ticker = 0;
		fastticker = 0;
		starting = false;
		monumentexists = false;
		enderActive = false;
		enderPortal = null;
		enderMan = null;
		autoassasin = false;
		a_ticker = 0;
		deaths = 0;
		infotimer = 0;
		dragon = null;
		
		globalCrystalChest = Bukkit.createInventory(null, ConfigManager.getStaticConfig().getInt("globalstorage", 27), ConfigManager.getLanguage().getString("string_crystal_storage", "Crystal Storage"));
		crystalPerPlayer.clear();
		
		String wadd = "";
		for(int i=0; i<p.getGameID(this); i++) {
			wadd = wadd + " ";
		}
		waitm = new WaitingMenu(p, wadd);
		waitm.close();
		released = ConfigManager.getStaticConfig().getString("need_release", "false").equals("false");
		canWin = ConfigManager.getStaticConfig().getString("can_win", "false").equals("true");
		gracetime = 0;
		
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
				this.starttime = time;
				
				if(voting) {
					maxVote = WorldManager.getWorldIDSize(plugin.getGameID(this));
					currentlyVoting = true;
				}
			//change the countdown when allready starting
			} else {
				if(this.starttime>0) {
					this.starttime = time;
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
		
		for(UUID playern : playerstate.keySet()) {			
			Player player = PlayerHandler.getPlayerFromUUID(playern);
			
			if(player!=null) {
				//undisguise
				DisguiseSystemHandler.undisguiseP(player);
				//clear potion effects
				PlayerHandler.resetPotionEffects(player);
				//clear inventory
				InventoryHandler.clearInv(player, false);
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
		votes.clear();
		maxVote = -1;
		currentlyVoting = false;
		votingPlayers.clear();
		dauer = 0;
		ticker = 0;
		enderActive = false;
		monumentexists = false;
		enderPortal = null;
		enderMan = null;
		autoassasin = false;
		a_ticker = 0;
		deaths = 0;
		infotimer = 0;
		dragon = null;
		
		globalCrystalChest = Bukkit.createInventory(null, ConfigManager.getStaticConfig().getInt("globalstorage", 27), ConfigManager.getLanguage().getString("string_crystal_storage", "Crystal Storage"));
		crystalPerPlayer.clear();
		
		waitm.releaseAll();
		waitm.close();
		released = ConfigManager.getStaticConfig().getString("need_release", "false").equals("false");
		canWin = ConfigManager.getStaticConfig().getString("can_win", "false").equals("true");
		gracetime = 0;
		
		customCooldown.clear();
		
		WorldManager.resetMainWorld(plugin.getGameID(this));
		HighscoreManager.saveHighscore();
		
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
						//-1 => calculate assassin count
						assasins(plugin.getConfig().getInt("lobby_assasintime", 30), -1, plugin.getConfig().getInt("lobby_assasindeath", 2));
					}
				}
			}
			
			updateHighscore();
		} else {
			if (starttime>=0) {
				starttime--;
				updateHighscore();
			}
			
			if (starttime==60*5) broadcastMessage(ConfigManager.getLanguage().getString("string_starting_minutes","-0- Minutes left!").replace("-0-", "5"));
			else if (starttime==60) broadcastMessage(ConfigManager.getLanguage().getString("string_starting_minute","-0- Minute left!").replace("-0-", "1"));
			else if (starttime==10) broadcastMessage(ConfigManager.getLanguage().getString("string_starting_seconds","-0- Seconds left!").replace("-0-", "10"));
			else if (starttime==5) broadcastMessage(ConfigManager.getLanguage().getString("string_starting_seconds","-0- Seconds left!").replace("-0-", "5"));
			else if (starttime==4) broadcastMessage(ConfigManager.getLanguage().getString("string_starting_seconds","-0- Seconds left!").replace("-0-", "4"));
			else if (starttime==3) broadcastMessage(ConfigManager.getLanguage().getString("string_starting_seconds","-0- Seconds left!").replace("-0-", "3"));
			else if (starttime==2) broadcastMessage(ConfigManager.getLanguage().getString("string_starting_seconds","-0- Seconds left!").replace("-0-", "2"));
			else if (starttime==1) broadcastMessage(ConfigManager.getLanguage().getString("string_starting_second","-0- Second left!").replace("-0-", "1"));
			else if (starttime==0) startGame();//timeUp();
			
			//Voting
			if(voting && starttime%10==0 && starttime>0) {
				broadcastMessage(ConfigManager.getLanguage().getString("string_vote","Vote for your favourite map with /dvz vote!"));
				for(int i=0; i<maxVote; i++) {
					int vote = 0;
					if(votes.containsKey(i))
						vote = votes.get(i);

					broadcastMessage(ConfigManager.getLanguage().getString("string_vote_map","-0-. -1- - -2- votes")
										.replace("-0-", (i+1)+"")
										.replace("-1-", WorldManager.getWorldName(plugin.getGameID(this), i))
										.replace("-2-", vote+""));
				}
			}
			
			if (starttime<=0) {
				dauer++;
				ticker++;
				
				if(ConfigManager.getStaticConfig().getString("global_stats", "true").equals("true"))
					updateGlobalStats();
				
				try {
					teleportUnreleased();
					kickUnwanted();
				} catch(Exception e) {
					//TODO - make sure there are no errors occuring
				}
				
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
							if(deaths<=a_maxdeaths || a_maxdeaths<=0) {
								//-1 => calculate assassin count
								if(a_count==-1) {
									a_count = playerstate.size()/plugin.getConfig().getInt("lobby_playerperassasin", 10);
									if (a_count==0) a_count=1;
								}
								addAssasins(a_count);
							}
						}
					}
				}
				
				//gracetime
				if(gracetime>0 && state==2) {
					gracetime -= 1;
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
				
				//monument distance
				if(monumentexists)
				for(UUID playern : playerstate.keySet()) {
					if(isDwarf(playern, true)) {
						Player player = PlayerHandler.getPlayerFromUUID(playern);
						
						if(player!=null) {
							Location tempPLoc = player.getLocation().clone();
							tempPLoc.setY(monument.getY());
							if(tempPLoc.distanceSquared(monument)>ConfigManager.getStaticConfig().getInt("max_monument_distance", 200)*ConfigManager.getStaticConfig().getInt("max_monument_distance", 200)) {
								int current = ConfigManager.getStaticConfig().getInt("max_monument_counter", 10);
								if(monDistance.containsKey(playern)) {
									current = monDistance.get(playern) - 1;
								}
								
								if(current>0) {
									monDistance.put(playern, current);
									DvZ.sendPlayerMessageFormated(player, ConfigManager.getLanguage().getString("max_monument_warning", "&4WARNING: Get closer to the monument or you will loose points!"));
									DvZ.sendPlayerMessageFormated(player, ConfigManager.getLanguage().getString("max_monument_wtime", "&4Time remaining: -0- Seconds!").replace("-0-", ""+current));
								} else {
									if(spawnDwarves!=null) {
										player.teleport(spawnDwarves);
									} else {
										player.teleport(player.getLocation().getWorld().getSpawnLocation());
									}
									
									monDistance.remove(playern);
									
									DvZ.sendPlayerMessageFormated(player, ConfigManager.getLanguage().getString("max_monument_teleport", "&4You have been teleported back because you went to far from the monument!"));
									//score notice
									int score = ConfigManager.getStaticConfig().getInt("", -1);
									String score_text = ConfigManager.getLanguage().getString("highscore_loose_distance", "You lost -0- for being to far from the monument!");
									if(Math.abs(score)==1)
										DvZ.sendPlayerMessageFormated(player, score_text.replace("-0-", ConfigManager.getLanguage().getString("highscore_point","-0- Point").replace("-0-", Math.abs(score)+"")));
									else
										DvZ.sendPlayerMessageFormated(player, score_text.replace("-0-", ConfigManager.getLanguage().getString("highscore_points","-0- Points").replace("-0-", Math.abs(score)+"")));
								}
							} else {
								monDistance.remove(playern);
							}
						}
					}
				}
				
				countdownTicker();
			}
		}
	}
	
	private void updateHighscore() {
		//Highscore
		if(ConfigManager.getStaticConfig().getBoolean("hscore_in_lobby", true)) {
			for(UUID st : playerstate.keySet()) {
				Player player = PlayerHandler.getPlayerFromUUID(st);
				if(player.isValid()) {
					player.setScoreboard(HighscoreManager.createOrRefreshPlayerScore(player.getUniqueId()));
				}
			}
		}
	}
	
	//#######################################
	//Tick für Countdown
	//#######################################
	private void countdownTicker() {
		//save for cuncurrentmodification
		ArrayList<String> remove = new ArrayList<String>();
		for(Map.Entry<String, Integer> e : customCooldown.entrySet()){
			String key = e.getKey();
			int time = e.getValue();
			
			time--; 
			customCooldown.put(key, time);
			//end
			if(time==0) {
				String[] split = key.split("||");
				
				countdownEnd(split[0], split[1]);
			}
			if(time<=0) remove.add(key);
		}
		for(String st : remove) {
			customCooldown.remove(st);
		}
		remove.clear();
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
	private Random rand = new Random();
	public void startGame() {
		if (state==1) {
			currentlyVoting = false;
			
			//welt erstellen
			if(voting) {
				//sieger der Abstimmung suchen
				int pos = 0;
				int highestVote = -1;
				ArrayList<Integer> highestMaps = new ArrayList<Integer>();
				
				for(int i=0; i<maxVote; i++) {
					int vote = 0;
					if(votes.containsKey(i))
						vote = votes.get(i);
					
					if(vote>highestVote) {
						highestMaps.clear();
						highestVote = vote;
						highestMaps.add(i);
					} else if(vote==highestVote) {
						highestMaps.add(i);
					}
				}
				
				//unentschieden -> zufall
				pos = highestMaps.get(rand.nextInt(highestMaps.size()));
				
				WorldManager.newMainWorld(plugin.getGameID(this), pos);
			} else {
				WorldManager.newRandomMainWorld(plugin.getGameID(this));
			}
			
			final int gtemp = plugin.getGameID(this);
			
			taskid = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
				public void run() {
					World w = Bukkit.getServer().getWorld(plugin.getConfig().getString("world_prefix", "DvZ_")+"Main"+gtemp+"");
					if(w!=null) {
						if(plugin.getConfig().getString("set_to_day","true").equals("true")) {
							w.setTime(0);
						}
						//TODO - disabled to not teleport wrong players
						//teleportToMainWorld();
						state = 2;
						releasetime = ConfigManager.getStaticConfig().getInt("time_release",30)*60;
						wintime = ConfigManager.getStaticConfig().getInt("time_win",30)*60;
						gracetime = ConfigManager.getStaticConfig().getInt("time_grace",5)*60;
						
						DvZ.startedGames += 1;
						
						for(Map.Entry<UUID, Integer> e : playerstate.entrySet()){
							UUID players = e.getKey();
							int pstate = e.getValue();
							
							if (pstate==1) {
								Player player = PlayerHandler.getPlayerFromUUID(players);
								if(player!=null) {
									InventoryHandler.clearInv(player, false);
									//this doesn't really seem to work
									player.resetMaxHealth();
									//TODO - so lets use this for now
									player.setMaxHealth(20d);
									player.setHealth(player.getMaxHealth());
									player.setGameMode(GameMode.SURVIVAL);
									player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
									DvZ.sendPlayerMessageFormated(player, ConfigManager.getLanguage().getString("string_choose","Choose your class!"));
									addDwarfItems(player);
									
									playerstate.put(players, Game.pickDwarf);
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
		
		for(Map.Entry<UUID, Integer> e : playerstate.entrySet()){
			boolean online = false;
			Player player = PlayerHandler.getPlayerFromUUID(e.getKey());
			if (player!=null) online = true;
			
			if (isDwarf(e.getKey(), true)) {
				if (online) {
					dwarf++; //else dwarfoff++;
					//only the last standing and online dwarf
					lastdwarf = player.getName();
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
					if(block2.getType()!=Material.ENCHANTMENT_TABLE) 
						destroyed++;
				}
			}
			if(destroyed==4) {
				destr = true;
			}
		}
		monumentHealth = 100 - (int) Math.round((100/(double)4)*destroyed);
		
		//healthbar
		if(ConfigManager.getStaticConfig().getString("show_monument_bar", "true").equals("true")) {
			for(UUID st : playerstate.keySet()){
				Player player = PlayerHandler.getPlayerFromUUID(st);
				if (player!=null) {
					DvZPackets.sendInfoBar(player, monumentHealth/100D, ConfigManager.getLanguage().getString("monument_bar","Monument"));
				}
			}
		}
		
		//loose
		if (destr) {
			broadcastMessage(ConfigManager.getLanguage().getString("string_lose_monument","§4Game Over!§f The Monument has been destroyed!"));

			broadcastMessage(ConfigManager.getLanguage().getString("string_lose_monument_dwarves","Dwarves who failed to protect the Monument:"));
			printSurvivingPlayers(ConfigManager.getStaticConfig().getInt("hscore_loose_monument", -5), ConfigManager.getLanguage().getString("highscore_loose_lost","You lost -0- for failing to protect the monument!"));
			
			//Score/Stats
			for(UUID st : playerstate.keySet()){
				if(isDwarf(st, true)) {
					Player player = PlayerHandler.getPlayerFromUUID(st);
					if (player!=null) {
						PlayerScore pscore = HighscoreManager.getPlayerScore(player.getUniqueId());
						pscore.setLosses(pscore.getLosses()+1);
					}
				}
			}
			
			reset(true);
		}
	}
	
	private void win() {
		broadcastMessage(ConfigManager.getLanguage().getString("string_win","§4Victory!§f The dwarves protected the Monument!"));
		
		broadcastMessage(ConfigManager.getLanguage().getString("string_win_dwarves","Dwarves who survived and protected the Monument:"));
		printSurvivingPlayers(ConfigManager.getStaticConfig().getInt("hscore_win", 20), ConfigManager.getLanguage().getString("highscore_get_win","You received -0- for winning!"));
		
		//Score/Stats
		for(UUID st : playerstate.keySet()){
			if(isDwarf(st, true)) {
				Player player = PlayerHandler.getPlayerFromUUID(st);
				if (player!=null) {
					PlayerScore pscore = HighscoreManager.getPlayerScore(st);
					pscore.setVictories(pscore.getVictories()+1);
				}
			}
		}
		
		reset(true);
	}
	
	private void printSurvivingPlayers(int score, String score_text) {
		String pmessage = "";
		int pcount = 0;
		int pmaxCount = 5;
		
		for(Map.Entry<UUID, Integer> e : playerstate.entrySet()){
			Player player = PlayerHandler.getPlayerFromUUID(e.getKey());
			
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
					
					//Score
					HighscoreManager.addPoints(e.getKey(), score);
					if(Math.abs(score)==1)
						DvZ.sendPlayerMessageFormated(player, score_text.replace("-0-", ConfigManager.getLanguage().getString("highscore_point","-0- Point").replace("-0-", Math.abs(score)+"")));
					else
						DvZ.sendPlayerMessageFormated(player, score_text.replace("-0-", ConfigManager.getLanguage().getString("highscore_points","-0- Points").replace("-0-", Math.abs(score)+"")));
				}
			}
		}
		if(!pmessage.equals(""))
			broadcastMessage(pmessage);
		
		//Score save
		HighscoreManager.saveHighscore();
	}
	
	private void updateGlobalStats() {
		int dwarf = 0;
		int assa = 0;
		int mons = 0;
		
		for(Map.Entry<UUID, Integer> e : playerstate.entrySet()){
			if (isDwarf(e.getKey(), false)) {
				//online check
				if(PlayerHandler.getPlayerFromUUID(e.getKey())!=null)
					dwarf++;
			} else if (isDwarf(e.getKey(), true)) {
				//online check
				if(PlayerHandler.getPlayerFromUUID(e.getKey())!=null)
					assa++;
			}
			if (isMonster(e.getKey())) {
				//online check
				if(PlayerHandler.getPlayerFromUUID(e.getKey())!=null)
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
		boolean chooseOne = false;
		for(int i=0; i<count; i++) {
			Object[] rplayers = playerstate.keySet().toArray();
			UUID playern = (UUID) rplayers[rand.nextInt(rplayers.length)];
			Player player = PlayerHandler.getPlayerFromUUID(playern);
			
			//check for Playercount
			int ammountPlayers = 0;
			for (int j=0; j<rplayers.length; j++) {
				playern = (UUID) rplayers[j];
				if(isDwarf(playern, false) && player!=null) {
					ammountPlayers += 1;
				}
			}
			if (ammountPlayers<count && !chooseOne) {
				broadcastMessage(ConfigManager.getLanguage().getString("string_no_assasins","No Assasins have been chosen - Because there where not enough online Dwarves!!"));
				return;
			}
			while(!isDwarf(playern, false) || player==null) {
				playern = (UUID) rplayers[rand.nextInt(rplayers.length)];
				player = PlayerHandler.getPlayerFromUUID(playern);
			}

			chooseOne = true;

			DvZ.sendPlayerMessageFormated(player, ConfigManager.getLanguage().getString("string_become_assasin","You have been chosen to be a Assasin!"));

			playerstate.put(player.getUniqueId(), Game.assasinState);

			//time
			int asstime = ConfigManager.getClassFile().getInt("assasin_time_minutes",5);
			if(asstime>0) {
				setCustomCooldown(player.getUniqueId(), "assassin_time", asstime*60);

				DvZ.sendPlayerMessageFormated(player, ConfigManager.getLanguage().getString("string_become_assasin_time","If you don't kill someone within the next -0- minutes you will die!").replace("-0-", ""+asstime));
			}

			//add assasin items to inventory
			PlayerInventory inv = player.getInventory();
			List<String> itemstrings = ConfigManager.getClassFile().getStringList("assasin_items");
			for(int j=0; j<itemstrings.size(); j++) {
				ItemStack it = ItemHandler.decodeItem(itemstrings.get(j), player);
				if(it!=null) {
					inv.addItem(it);
				}
			}
		}
		broadcastMessage(ConfigManager.getLanguage().getString("string_assasins","-0- Assasins have been chosen!!").replace("-0-", ""+count));
	}
	
	//#######################################
	//Countdown Over
	//#######################################
	public void countdownEnd(String player, String countdown) {
		//assasin
		if(playerstate.get(player)==Game.assasinState) {
			if(countdown.equals("assassin_time")) {
				Player playern = PlayerHandler.getPlayerFromUUID(player);
				if(playern!=null) {
					playern.damage((double) 1000);
					DvZ.sendPlayerMessageFormated(playern, ConfigManager.getLanguage().getString("string_assasin_timeup","Your time is up!"));
				}
			}
		}
		
		if(countdown.equals("monster_invulnarability")) {
			addMonsterMap(PlayerHandler.getPlayerFromUUID(player));
		}
	}
	
	//#######################################
	//Dwarf Items hinzufügen
	//#######################################
	public void addDwarfItems(final Player player) {
		Random rand = new Random();
		PlayerInventory inv = player.getInventory();
		
		resetCountdowns(player.getUniqueId());
		
		ItemStack[] dwarfItems = new ItemStack[DvZ.dwarfManager.getCount()];
		
		for(int i=0; i<DvZ.dwarfManager.getCount(); i++) {
			dwarfItems[i] = new ItemStack(DvZ.dwarfManager.getDwarf(i).getClassItem(), 1, (short)DvZ.dwarfManager.getDwarf(i).getClassItemDamage());
			ItemMeta cim = dwarfItems[i].getItemMeta();
			cim.setDisplayName(ConfigManager.getLanguage().getString("string_become","Become -0-").replace("-0-", DvZ.dwarfManager.getDwarf(i).getName()));
			dwarfItems[i].setItemMeta(cim);
		}
		
		//costum dwarves
		if(!plugin.getConfig().getString("new_classselection","true").equals("true")) {
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
	            	if(!isPlayer(event.getPlayer().getUniqueId())) {
	            		event.setWillClose(true);
	                    event.setWillDestroy(true);
	                    return;
	            	}
	            	
	            	//safety for not running games
	            	if(!game.isRunning()) {
	            		resetPlayerToWorldLobby(player);
	    				
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
	    				SpecialPlayer sp = DvZ.playerManager.getPlayer(player.getUniqueId());
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
	                //DvZ.sendPlayerMessageFormated(event.getPlayer(), "You have chosen " + event.getName());
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

		resetCountdowns(player.getUniqueId());
		
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
	            	if(!isPlayer(event.getPlayer().getUniqueId())) {
	            		event.setWillClose(true);
	                    event.setWillDestroy(true);
	                    return;
	            	}
	            	
	            	//safety for not running games
	            	if(!game.isRunning()) {
	            		resetPlayerToWorldLobby(player);
	    				
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
	
	public void resetPlayerToWorldLobby(final Player player) {
		playerstate.put(player.getUniqueId(), 1);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				World w;
				if(ConfigManager.getStaticConfig().getString("use_lobby", "true").equals("true"))
					w = Bukkit.getServer().getWorld(ConfigManager.getStaticConfig().getString("world_prefix", "DvZ_")+"Lobby");
				else
					w = Bukkit.getServer().getWorlds().get(0);
				
				if(w!=null) {
					player.teleport(w.getSpawnLocation());
				}
			}
		}, 1);
	}
	
	//#######################################
	//Spieler hat rechtsgeklickt
	//#######################################
	public void playerRC(PlayerInteractEvent event, Player player, ItemStack item, Block block) {
		if(!isPlayer(player.getUniqueId())) return;
		if(item==null) return;
		int itemId = item.getTypeId();
		int itemD = item.getDurability();
		UUID puuid = player.getUniqueId();
		
		if(getPlayerState(puuid)==2) { //dwarf werden
			boolean dwarf = false;
			
			//safety for not running games
        	if(!isRunning()) {
        		resetPlayerToWorldLobby(player);
        		return;
        	}
			
			//costum dwarves
			for(int i=0; i<DvZ.dwarfManager.getCount(); i++) {
				CustomDwarf cm = DvZ.dwarfManager.getDwarf(i);
				if(itemId==cm.getClassItem() && itemD==cm.getClassItemDamage()) {
					cm.becomeDwarf(this, player);
					dwarf = true;
				}
			}
			
			if (dwarf) {
				SpecialPlayer sp = DvZ.playerManager.getPlayer(player.getUniqueId());
				if(sp!=null) {
					sp.addCrytalItems(this, player);
				}
				
				if(spawnDwarves!=null) {
					player.teleport(spawnDwarves);
				}
			}
		}
		if(getPlayerState(puuid)==3) { //monster werden
			boolean monster = false;
			
			//safety for not running games
        	if(!isRunning()) {
        		resetPlayerToWorldLobby(player);
        		return;
        	}
			
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
		if(isMonster(puuid) && !released) {
			return;
		}
		
		//custom dwarves - rightclick
		if(isDwarf(puuid, false)) {
			int dId = getPlayerState(puuid)-dwarfMin;
			if(dId>=0 && dId<DvZ.monsterManager.getCount()) {
				CustomDwarf cd = DvZ.dwarfManager.getDwarf(dId);
				
				//transmute items
				if(block!=null) {
					if(cd.transmuteItemOnBlock(this, player, item, block)) {
						event.setCancelled(true);
					}
				}
			}
		}
		
		if(isDwarf(puuid, true) && itemId==121) Spellcontroller.spellDisablePortal(this, player);
		
		//Monster
		if(isMonster(puuid) && itemId==358) Spellcontroller.spellTeleport(this, player);
		
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
		if(!isPlayer(player.getUniqueId())) return;
		if(item==null) return;
		
		//disable clicking when monsters are not released
		if(isMonster(player.getUniqueId()) && !released) {
			return;
		}
	}
	
	//#######################################
	//Spieler hat linksgeklickt
	//#######################################
	public void playerLC(Player player, ItemStack item, Block block) {
		if(!isPlayer(player.getUniqueId())) return;
		if(item==null) return;
		int itemId = item.getTypeId();
		UUID puuid = player.getUniqueId();
		
		//disable clicking when monsters are not released
		if(isMonster(puuid) && !released) {
			return;
		}
		
		if(itemId == 373 && isDwarf(puuid, true)) {
			//changed from old hacky potionhandler to new bukkit functionallity
			if(ExperienceUtils.getCurrentExp(player)>=plugin.getConfig().getInt("dwarf_potion_exp", 2)) {
				ExperienceUtils.changeExp(player, -plugin.getConfig().getInt("dwarf_potion_exp", 2));
				ThrownPotion thrp = player.launchProjectile(ThrownPotion.class);
				thrp.setItem(item);
			} else {
				DvZ.sendPlayerMessageFormated(player, ConfigManager.getLanguage().getString("string_needexp","You don't have enough exp!"));
			}
		}
	}
	
	public void playerBreakBlock(Player player, Block block) {
		if(isDwarf(player.getUniqueId(), false)) {
			int dId = getPlayerState(player.getUniqueId()) - Game.dwarfMin;
			CustomDwarf cd = DvZ.dwarfManager.getDwarf(dId);
			
			if(cd != null)
				cd.transmuteItemOnBreak(this, player, block);
		}
	}
	
	//#######################################
	//Anfang des Spieles Spieler hizufügen
	//#######################################
	public boolean addPlayer(UUID player) {
		//nur wenn noch nicht eingetragen und spiel nicht gestartet
		if (!playerstate.containsKey(player) && state==1) {
			playerstate.put(player, 1);	//nix, pregame
			DvZ.log(player+" added to the Game.");
			return true;
		}
		return false;
	}
	
	//#######################################
	//Spieler entfernen
	//#######################################
	public void removePlayer(UUID player) {
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
		
		DvZ.sendPlayerMessageFormated(player, ConfigManager.getLanguage().getString("string_invulnarable","You are -0- seconds invulnarable!").replace("-0-", ""+time));
		
		setCustomCooldown(player.getUniqueId(), "monster_invulnarability", time);
	}
	
	private void addMonsterMap(Player player) {
		//only monsters are allowed to get these items
		if(player!=null && getPlayerState(player.getUniqueId())>=Game.monsterMin && getPlayerState(player.getUniqueId())<=Game.monsterMax) {
			PlayerInventory inv = player.getInventory();
			
			ItemStack it = new ItemStack(Material.MAP, 1);
			ItemMeta im = it.getItemMeta();
			im.setDisplayName(ConfigManager.getLanguage().getString("string_spell_teleport","Teleport to Enderman Portal"));
			ArrayList<String> li4 = new ArrayList<String>();
			li4.add(ConfigManager.getLanguage().getString("string_used_seconds","Can be used every -0- Seconds!").replace("-0-", ""+plugin.getConfig().getInt("spelltime_teleport",15)));
			im.setLore(li4);
			it.setItemMeta(im);
			inv.addItem(it);
			
			if(plugin.getConfig().getString("monster_suizidepill", "true")=="true") {
				it = ItemHandler.decodeItem("!spellitems:internSuicidePill", player);
				if(it!=null) {
					inv.addItem(it);
				}
			}
			
			DvZ.updateInventory(player);
		}
	}
	
	public boolean isBuffed(UUID player) {
		return getCustomCooldown(player, "monster_invulnarability")>0;
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
					block2.setType(Material.OBSIDIAN);
				}
			}
		}
		
		for(int i=0; i<=1; i++) {
			for(int j=0; j<=1; j++) {
				if(obsi) {
					block2 = block.getRelative(i, 1, j);
					block2.setType(Material.OBSIDIAN);
					block2 = block.getRelative(i, 2, j);
					block2.setType(Material.OBSIDIAN);
				}
				block2 = block.getRelative(i, 3, j);
				block2.setType(Material.ENCHANTMENT_TABLE);
			}
		}
	}
	
	//#######################################
	//Spieler schon registriert?
	//#######################################
	public boolean isPlayer(UUID player) {
		return playerstate.containsKey(player);
	}
	
	//#######################################
	//Bekomme Playerstate
	//#######################################
	public int getPlayerState(UUID player) {
		if (playerstate.containsKey(player))
			return playerstate.get(player);
		else
			return 0;
	}
	public boolean isDwarf(UUID player, boolean assassins) {
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
	public boolean isMonster(UUID player) {
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
	public void setPlayerState(UUID player, int pstate) {
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
					UUID playern = (UUID) rplayers[i];
					Player player = PlayerHandler.getPlayerFromUUID(playern);
					
					if (player!=null) {
						DisguiseSystemHandler.redisguiseP(player);
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
			UUID playern = (UUID) rplayers[i];
			if(isMonster(playern)) {
				Player player = PlayerHandler.getPlayerFromUUID(playern);
	
				if(player!=null) {
					if (ticker==10) {
						DvZ.sendPlayerMessageFormated(player, message);
						DvZ.sendPlayerMessageFormated(player, message2);
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
	
	//teleport unwanted players
	public void kickUnwanted() {
		//mainworld
		World w = Bukkit.getServer().getWorld(plugin.getConfig().getString("world_prefix", "DvZ_")+"Main"+plugin.getGameID(this)+"");
		
		if(w!=null) {
			for(Player p : w.getPlayers()) {
				//not playing -> kick to lobby
				if(!isPlayer(p.getUniqueId()) || getPlayerState(p.getUniqueId())==1) {
					resetPlayerToWorldLobby(p);
					playerstate.remove(p.getUniqueId());
					//DvZ.instance.joinGame(p, this, false);
				}
				//picking monster -> to monsterspawn
				if(getPlayerState(p.getUniqueId())==Game.pickMonster) {
					if(spawnMonsters!=null) {
						if(spawnMonsters.distanceSquared(p.getLocation())>2)
							p.teleport(spawnMonsters);
					}
				}
				//pickdwarf -> rejoin
				if(getPlayerState(p.getUniqueId())==Game.pickDwarf) {
					resetPlayerToWorldLobby(p);
					playerstate.remove(p.getUniqueId());
					/*if(state>1) {
						DvZ.instance.joinGame(p, this, true);
					}*/
				}
			}
		}
		
		//lobby
		World wl;
		if(ConfigManager.getStaticConfig().getString("use_lobby", "true").equals("true"))
			wl = Bukkit.getServer().getWorld(ConfigManager.getStaticConfig().getString("world_prefix", "DvZ_")+"Lobby");
		else
			wl = Bukkit.getServer().getWorlds().get(0);
		
		if(wl!=null) {
			for(Player p : wl.getPlayers()) {
				//dwarves
				if(isDwarf(p.getUniqueId(), true)) {
					Location loc = wl.getSpawnLocation();
					if(spawnDwarves!=null) loc = spawnDwarves;
					
					p.teleport(loc);
				}
				//monsters
				if(isMonster(p.getUniqueId())) {
					Location loc = wl.getSpawnLocation();
					if(spawnMonsters!=null) loc = spawnMonsters;
					
					p.teleport(loc);
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
		final World w = Bukkit.getServer().getWorld(plugin.getConfig().getString("world_prefix", "DvZ_")+"Main"+plugin.getGameID(this)+"");
		final Object[] rplayers = playerstate.keySet().toArray();
		
		if(ConfigManager.getStaticConfig().getInt("delayed_teleporation", 0)>0) {
			new BukkitRunnable() {
				private int ii = 0;

				@Override
				public void run() {
					if(ii>=rplayers.length) this.cancel();
					
					String playern = (String) rplayers[ii];
					Player player = Bukkit.getServer().getPlayerExact(playern);
					
					if (player!=null && w!=null) {
						player.teleport(w.getSpawnLocation());
					}
					
					ii++;
				}
				
			}.runTaskTimer(getPlugin(), 0, ConfigManager.getStaticConfig().getInt("delayed_teleporation", 0));
		} else {
			//instant teleportation
			for(int i=0; i<rplayers.length; i++) {
				String playern = (String) rplayers[i];
				Player player = Bukkit.getServer().getPlayerExact(playern);
				
				if (player!=null && w!=null) {
					player.teleport(w.getSpawnLocation());
				}
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
				DvZ.sendPlayerMessageFormated(player, message);
			}
		}
	}
	
	//#######################################
	//Voting
	//#######################################
	public boolean acceptsVotes() {
		return currentlyVoting;
	}
	public boolean vote(Player player, int pos) {
		if(votingPlayers.contains(player.getUniqueId())) {
			DvZ.sendPlayerMessageFormated(player, ConfigManager.getLanguage().getString("string_vote_allready_voted", "You have allready voted!"));
			return false;
		} else {
			if(pos>maxVote || pos<1) {
				DvZ.sendPlayerMessageFormated(player, ConfigManager.getLanguage().getString("string_vote_invalid", "Please vote for an existing Map!"));
				return false;
			}
			
			votingPlayers.add(player.getUniqueId());
			
			int vote = 0;
			if(votes.containsKey(pos-1))
				vote = votes.get(pos-1);
			vote++;
			votes.put(pos-1, vote);
			
			return true;
		}
	}
	
	//#######################################
	//public Methoden Cooldowns(getters/...
	//#######################################
	public void resetCountdowns(UUID player) {
		crystalPerPlayer.remove(player);
	}
	
	public void setCustomCooldown(UUID player, String name, int time) {
		customCooldown.put(player+"||"+name, time);
	}
	public int getCustomCooldown(UUID player, String name) {
		if(customCooldown.containsKey(player+"||"+name)) {
			return customCooldown.get(player+"||"+name);
		}
		
		return -1;
	}
	public void resetCustomCooldown(UUID player, String name) {
		customCooldown.remove(player+"||"+name);
	}
	
	public Inventory getCrystalChest(UUID pname, boolean global) {
		if(global) {
			return globalCrystalChest;
		} else {
			if(!crystalPerPlayer.containsKey(pname)) crystalPerPlayer.put(pname, Bukkit.createInventory(null, ConfigManager.getStaticConfig().getInt("privatestorage", 27), ConfigManager.getLanguage().getString("string_crystal_storage", "Crystal Storage")));
			
			return crystalPerPlayer.get(pname);
		}
	}
	
	public boolean isGraceTime() {
		return gracetime>0;
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
		return starttime;
	}
	
	public int getDauer() {
		return dauer;
	}
	
	public DvZ getPlugin() {
		return plugin;
	}
}
