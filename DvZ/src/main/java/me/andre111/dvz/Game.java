package me.andre111.dvz;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import me.andre111.dvz.config.ConfigManager;
import me.andre111.dvz.config.DVZFileConfiguration;
import me.andre111.dvz.customclass.CustomClass;
import me.andre111.dvz.disguise.DisguiseSystemHandler;
import me.andre111.dvz.dragon.Dragon;
import me.andre111.dvz.dragon.PlayerDragon;
import me.andre111.dvz.event.DVZGameEndEvent;
import me.andre111.dvz.event.DVZGameStartEvent;
import me.andre111.dvz.manager.HighscoreManager;
import me.andre111.dvz.manager.PlayerScore;
import me.andre111.dvz.manager.StatManager;
import me.andre111.dvz.manager.WorldManager;
import me.andre111.dvz.players.SpecialPlayer;
import me.andre111.dvz.teams.GameTeamSetup;
import me.andre111.dvz.teams.Team;
import me.andre111.dvz.utils.GameOptionClickEventHandler;
import me.andre111.dvz.utils.IconMenu;
import me.andre111.dvz.utils.InventoryHandler;
import me.andre111.dvz.utils.PlayerHandler;
import me.andre111.dvz.utils.Slapi;
import me.andre111.dvz.utils.WaitingMenu;
import me.andre111.dvz.volatileCode.DvZPackets;
import me.andre111.items.ItemHandler;
import me.andre111.items.item.spell.ItemPortal;
import me.andre111.items.utils.Attributes;
import me.andre111.items.utils.Attributes.Attribute;
import me.andre111.items.utils.Attributes.AttributeType;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

//TODO - convert all classes/... to use new itemformat
public class Game {
	private DvZ plugin;
	
	private GameState state;
	private int gameType;
	private int starttime;
	
	private boolean voting;
	private HashMap<Integer, Integer> votes = new HashMap<Integer, Integer>();
	private int maxVote;
	private boolean currentlyVoting;
	private ArrayList<UUID> votingPlayers = new ArrayList<UUID>();
	
	private int dauer;
	private int ticker;
	private boolean starting;
	
	public boolean enderActive;
	public Location enderPortal;
	
	private Inventory globalCrystalChest;
	private HashMap<UUID, Inventory> crystalPerPlayer = new HashMap<UUID, Inventory>();
	
	public GameTeamSetup teamSetup;
	public HashMap<UUID, String> playerteam = new HashMap<UUID, String>();
	public HashMap<UUID, String> playerstate = new HashMap<UUID, String>();
	
	//TODO - use this vars everywhere
	public static final String STATE_PREGAME = "PREGAME";
	public static final String STATE_ASSASSIN = "ASSASSIN";
	public static final String STATE_CHOOSECLASS = "CHOOSE_CLASS";
	public static final String STATE_CLASSPREFIX = "CLASS_";
	
	public WaitingMenu waitm;
	
	private Dragon dragon;
	
	//used for custom cooldowns String: Playeruuidstring::CooldownName
	private HashMap<String, Integer> customCooldown = new HashMap<String, Integer>();
	
	private int infotimer;
	private String lastTimerDisplay = "";
	
	//lobby
	private int lobby_Player;
	
	//#######################################
	//Neues Spiel
	//#######################################
	public Game(DvZ p, int type, int gid) {
		gameType = type;
		teamSetup = new GameTeamSetup(gid);
		
		state = GameState.IDLING;
		starttime = 30;//60;
		
		voting = ConfigManager.getStaticConfig().getBoolean("lobby_voting", false);
		currentlyVoting = false;
		votingPlayers.clear();
		
		plugin = p;
		dauer = 0;
		ticker = 0;
		starting = false;
		enderActive = false;
		enderPortal = null;

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
				starttime = time;
				
				if(voting) {
					maxVote = WorldManager.getWorldIDSize(plugin.getGameID(this));
					currentlyVoting = true;
				}
			//change the countdown when allready starting
			} else {
				if(starttime>0) {
					starttime = time;
				}
			}
		}
	}
	
	public void reset(boolean callEvent) {
		starting = false;
		state = GameState.IDLING;
		
		if(callEvent) {
			DVZGameEndEvent event = new DVZGameEndEvent(this);
			Bukkit.getServer().getPluginManager().callEvent(event);
		}
		
		for(UUID playern : playerstate.keySet()) {			
			Player player = Bukkit.getPlayer(playern);
			
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
		
		UUID[] players = playerstate.keySet().toArray(new UUID[playerstate.keySet().size()]);
		playerstate.clear();
		playerteam.clear();
		teamSetup = new GameTeamSetup(DvZ.instance.getGameID(this));
		votes.clear();
		maxVote = -1;
		currentlyVoting = false;
		votingPlayers.clear();
		dauer = 0;
		ticker = 0;
		enderActive = false;
		enderPortal = null;
		infotimer = 0;
		dragon = null;
		
		globalCrystalChest = Bukkit.createInventory(null, ConfigManager.getStaticConfig().getInt("globalstorage", 27), ConfigManager.getLanguage().getString("string_crystal_storage", "Crystal Storage"));
		crystalPerPlayer.clear();
		
		waitm.releaseAll();
		waitm.close();
		
		customCooldown.clear();
		
		WorldManager.resetMainWorld(plugin.getGameID(this));
		HighscoreManager.saveHighscore();
		
		//rejoin
		if(plugin.getConfig().getBoolean("auto_rejoin", false)) {
			for(UUID playern : players) {
				Player player = Bukkit.getServer().getPlayer(playern);
				
				if(player!=null) {
					plugin.joinGame(player, this);
				}
			}
		}
	}
	
	//#######################################
	//Tick f�r Countdown
	//#######################################
	public void tick() {
		//Gamestatus zu Spielern senden
		if(!starting) {
			if(infotimer++>=30) {
				infotimer = 0;
				if (state==GameState.IDLING) {
					broadcastMessage(ConfigManager.getLanguage().getString("string_lobby_waiting","Waiting for the Game to start..."));
					broadcastMessage(ConfigManager.getLanguage().getString("string_lobby_players","-0-/-1- Players for Game to start!").replace("-0-", ""+getOnlinePlayers()).replace("-1-", ""+plugin.getConfig().getInt("lobby_players", 20)));
				}
			}
			//Autostart{
			if(lobby_Player>0) {
				if(getOnlinePlayers()>=lobby_Player) {
					broadcastMessage(ConfigManager.getLanguage().getString("string_game_start","Game starting in -0- Seconds!").replace("-0-", ""+plugin.getConfig().getInt("lobby_starttime", 60)));
					start(plugin.getConfig().getInt("lobby_starttime", 60));
				}
			}
			
			updateHighscore();
		} else {
			if (starttime>=0) {
				starttime--;
				updateHighscore();
				pregameCountdownTick();
			} else if (starttime<=0 && state==GameState.RUNNING) {
				dauer++;
				ticker++;
				
				if(ConfigManager.getStaticConfig().getBoolean("global_stats", true))
					updateGlobalStats();
				
				try {
					teleportUnreleased();
					kickUnwanted();
				} catch(Exception e) {
					//TODO - make sure there are no errors occuring
				}
				
				if (ticker>=10) {
					ticker = 0;

					//healthbar
					if(ConfigManager.getStaticConfig().getBoolean("show_monument_bar", true)) {
						for(UUID st : playerstate.keySet()){
							Player player = Bukkit.getPlayer(st);
							Team team = getTeam(st);
							if(team!=null) {
								Team barTeam = teamSetup.getTeam(team.getMonumentBarTeam());
								if (player!=null && barTeam!=null && barTeam.hasMonument()) {
									DvZPackets.sendInfoBar(player, barTeam.getMonumentHealth()/100D, ConfigManager.getLanguage().getString("monument_bar","Monument"));
								}
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
				Player player = Bukkit.getPlayer(st);
				if(player!=null && player.isValid()) {
					player.setScoreboard(HighscoreManager.createOrRefreshPlayerScore(player.getUniqueId()));
				}
			}
		}
	}
	private void pregameCountdownTick() {
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
	}
	
	//#######################################
	//Tick f�r Countdown
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
				String[] split = key.split("::");
				
				countdownEnd(UUID.fromString(split[0]), split[1]);
			}
			if(time<=0) remove.add(key);
		}
		for(String st : remove) {
			customCooldown.remove(st);
		}
		remove.clear();
	}
	
	//#######################################
	//Countdown Over
	//#######################################
	public void countdownEnd(UUID player, String countdown) {
		//assasin
		if(playerstate.get(player).equals(STATE_ASSASSIN)) {
			if(countdown.equals("assassin_time")) {
				Player playern = Bukkit.getPlayer(player);
				if(playern!=null) {
					playern.damage((double) 1000);
					DvZ.sendPlayerMessageFormated(playern, ConfigManager.getLanguage().getString("string_assasin_timeup","Your time is up!"));
				}
			}
		}

		if(countdown.equals("monster_invulnarability")) {
			addSpawnBuffItems(Bukkit.getPlayer(player));
		}
	}
	
	//fastticker 20 times per second
	public void fastTick() {
		if(state==GameState.RUNNING)
			teamSetup.tick();
	}

	//#######################################
	//Starte das wirkliche Spiel/oder geht weiter
	//#######################################
	private int taskid = -1;
	private Random rand = new Random();
	public void startGame() {
		if (state==GameState.IDLING) {
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
			
			//Setup laden
			final int gid = DvZ.instance.getGameID(this);
			File file = new File(new File(Bukkit.getServer().getWorldContainer().getPath()+"/"+DvZ.instance.getConfig().getString("world_prefix", "DvZ_")+"Main"+gid+"/"), "setup.yml");
			if(!file.exists()) {
				file = new File(DvZ.instance.getDataFolder(), "setup.yml");
			}
			DVZFileConfiguration sconfig = DVZFileConfiguration.loadConfiguration(file);
			teamSetup.loadSetup(sconfig);
			
			//spieler in starteams einteilen
			for(Map.Entry<UUID, String> e : playerstate.entrySet()){
				UUID players = e.getKey();
				
				if(!playerteam.containsKey(players) || playerteam.get(players).equals(GameTeamSetup.NO_TEAM)) {
					playerteam.put(players, teamSetup.getStartTeam());
				}
			}
			
			if(taskid==-1)
			taskid = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
				public void run() {
					World w = Bukkit.getServer().getWorld(plugin.getConfig().getString("world_prefix", "DvZ_")+"Main"+gid+"");
					if(w!=null) {
						if(plugin.getConfig().getBoolean("set_to_day", true)) {
							w.setTime(0);
						}
						//TODO - disabled to not teleport wrong players
						//teleportToMainWorld();
						state = GameState.RUNNING;
						
						DvZ.startedGames += 1;
						
						for(Map.Entry<UUID, String> e : playerstate.entrySet()){
							UUID players = e.getKey();
							String pstate = e.getValue();
							
							if (pstate.equals(STATE_PREGAME)) {
								Player player = Bukkit.getPlayer(players);
								if(player!=null) {
									playerstate.put(players, Game.STATE_CHOOSECLASS);
									
									InventoryHandler.clearInv(player, false);
									//this doesn't really seem to work
									player.resetMaxHealth();
									//TODO - so lets use this for now
									player.setMaxHealth(20d);
									player.setHealth(player.getMaxHealth());
									player.setGameMode(GameMode.SURVIVAL);
									player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
									DvZ.sendPlayerMessageFormated(player, ConfigManager.getLanguage().getString("string_choose","Choose your class!"));
									addClassItems(player);
								}
							}
						}
						
						DvZ.itemStandManager.loadStands(w, new File(Bukkit.getServer().getWorldContainer().getPath()+"/"+w.getName()+"/dvz/"));
						
						endTask();
					}
			    }
			}, 20, 20);
		}
	}
	private void endTask() {
		Bukkit.getScheduler().cancelTask(taskid);
		taskid = -1;
	}
	
	public void win(Team team) {
		ArrayList<Team> winTeams = new ArrayList<Team>();
		winTeams.add(team);
		multiWinLose(winTeams, null);
	}
	public void lose(Team team) {
		ArrayList<Team> loseTeams = new ArrayList<Team>();
		loseTeams.add(team);
		multiWinLose(null, loseTeams);
	}
	public void multiWinLose(ArrayList<Team> winTeams, ArrayList<Team> loseTeams) {
		if(winTeams!=null)
		for(Team team : winTeams) {
			broadcastMessage(ConfigManager.getLanguage().getString("string_win","-0- won the game!").replace("-0-", team.getDisplayName()));
			
			//broadcastMessage(ConfigManager.getLanguage().getString("string_win_dwarves","Dwarves who survived and protected the Monument:"));
			printSurvivingPlayers(ConfigManager.getStaticConfig().getInt("hscore_win", 20), ConfigManager.getLanguage().getString("highscore_get_win","You received -0- for winning!"), team);
			
			//Score/Stats
			for(UUID st : playerstate.keySet()){
				if(getTeam(st).getName().equals(team.getName())) {
					Player player = Bukkit.getPlayer(st);
					if (player!=null) {
						PlayerScore pscore = HighscoreManager.getPlayerScore(st);
						pscore.setVictories(pscore.getVictories()+1);
					}
				}
			}
		}
		if(loseTeams!=null)
		for(Team team : loseTeams) {
			//TODO - change message for teams and points for teams
			broadcastMessage(ConfigManager.getLanguage().getString("string_lose","-0- lost the game!").replace("-0-", team.getDisplayName()));
			printSurvivingPlayers(ConfigManager.getStaticConfig().getInt("hscore_loose_monument", -5), ConfigManager.getLanguage().getString("highscore_lose_points","You lost -0-!"), team);
			
			//Score/Stats
			for(UUID st : playerstate.keySet()){
				if(getTeam(st).getName().equals(team.getName())) {
					Player player = Bukkit.getPlayer(st);
					if (player!=null) {
						PlayerScore pscore = HighscoreManager.getPlayerScore(player.getUniqueId());
						pscore.setLosses(pscore.getLosses()+1);
					}
				}
			}
		}
		
		reset(true);
	}
	
	private void printSurvivingPlayers(int score, String score_text, Team team) {
		String pmessage = "";
		int pcount = 0;
		int pmaxCount = 5;
		
		for(Map.Entry<UUID, String> e : playerstate.entrySet()){
			Player player = Bukkit.getPlayer(e.getKey());
			
			//only online players
			if (player!=null) {
				if (getTeam(e.getKey()).getName().equals(team.getName())) {
					pmessage = pmessage + player.getName() + ",";
					pcount++;
					if(pcount>=pmaxCount) {
						broadcastMessage(pmessage);
						
						pmessage = "";
						pcount = 0;
					}
					
					//Score
					HighscoreManager.addPoints(e.getKey(), score);
					if(Math.abs(score)==1)
						DvZ.sendPlayerMessageFormated(player, score_text.replace("-0-", ConfigManager.getLanguage().getString("highscore_point","-0- Point").replace("-0-", score+"")));
					else
						DvZ.sendPlayerMessageFormated(player, score_text.replace("-0-", ConfigManager.getLanguage().getString("highscore_points","-0- Points").replace("-0-", score+"")));
				}
			}
		}
		if(!pmessage.equals(""))
			broadcastMessage(pmessage);
		
		//Score save
		HighscoreManager.saveHighscore();
	}
	
	private void updateGlobalStats() {
		for(Team team : teamSetup.getTeams()) {
			int tcount = 0;
			int assa = 0;
			for(UUID puuid : getTeamPlayers(team)) {
				Player player = Bukkit.getPlayer(puuid);
				if(player!=null) {
					tcount++;
					if(getPlayerState(puuid)==STATE_ASSASSIN) {
						assa++;
					}
				}
			}
			StatManager.setGlobalStat(team.getDisplayName(), tcount, false);
			if(!team.getAssassinDisplay().equals("")) {
				StatManager.setGlobalStat(team.getAssassinDisplay(), assa, true);
			}
			
			if(team.hasMonument()) {
				StatManager.setGlobalStat(team.getMonumentName(), team.getMonumentHealth(), false);
			}
		}
		
		if(!teamSetup.getTimerDisplay().equals("") && !teamSetup.getTimerDisplay().equals(lastTimerDisplay)) {
			if(!lastTimerDisplay.equals("")) {
				StatManager.setTimeStat(lastTimerDisplay, 0);
			}
			lastTimerDisplay = teamSetup.getTimerDisplay();
		}
		if(teamSetup.isTimerDisplayed() && !teamSetup.getTimerDisplay().equals("")) {
			StatManager.setTimeStat(teamSetup.getTimerDisplay(), teamSetup.getTimerDisplayVar()/20);
		}
	}
	
	//#######################################
	//Geh�rt dieser block zum monument
	//#######################################
	public boolean isMonument(Block b, Team team) {
		if(team!=null && team.hasMonument()) {
			Location monument = team.getMonumentLocation();
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
	
	//TODO - readd deathcounter for assasins to not spawn
	//Add Assasins
	public void addAssasins(Team team, int percentage) {
		Random rand = new Random();
		boolean chooseOne = false;
		
		//check for Playercount
		Object[] rplayers = playerstate.keySet().toArray();
		int ammountPlayers = 0;
		for (int j=0; j<rplayers.length; j++) {
			UUID playern = (UUID) rplayers[j];
			Player player = Bukkit.getPlayer(playern);
			if(player!=null && playerteam.get(playern).equals(team.getName())) {
				ammountPlayers += 1;
			}
		}
		
		int count = (int) Math.ceil(ammountPlayers*(percentage/100D));
		for(int i=0; i<count; i++) {
			UUID playern = (UUID) rplayers[rand.nextInt(rplayers.length)];
			Player player = Bukkit.getPlayer(playern);
			
			if (ammountPlayers<count && !chooseOne) {
				broadcastMessage(ConfigManager.getLanguage().getString("string_no_assasins","No Assasins have been chosen - Because there where not enough online Dwarves!!"));
				return;
			}
			while(!playerteam.get(playern).equals(team.getName()) || player==null) {
				playern = (UUID) rplayers[rand.nextInt(rplayers.length)];
				player = Bukkit.getPlayer(playern);
			}

			chooseOne = true;

			DvZ.sendPlayerMessageFormated(player, ConfigManager.getLanguage().getString("string_become_assasin","You have been chosen to be a Assasin!"));

			playerstate.put(player.getUniqueId(), Game.STATE_ASSASSIN);

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
	//Dwarf Items hinzuf�gen
	//#######################################
	//NEVER CHANGE this
	private static final UUID classselectionID = UUID.fromString("95076b00-d15a-11e3-9c1a-0800200c9a66");
	public void addClassItems(final Player player) {
		Random rand = new Random();
		PlayerInventory inv = player.getInventory();
		
		resetCountdowns(player.getUniqueId());
		
		HashMap<String, ItemStack> dwarfItems = new HashMap<String, ItemStack>();
		
		for(String classI : getTeam(player.getUniqueId()).getClasses()) {
			ItemStack it = ItemHandler.decodeItem(DvZ.classManager.getClass(classI).getClassItem(), player);
			if(it!=null) {
				dwarfItems.put(classI, it);
				ItemMeta cim = dwarfItems.get(classI).getItemMeta();
				cim.setDisplayName(ConfigManager.getLanguage().getString("string_become","Become -0-").replace("-0-", DvZ.classManager.getClass(classI).getName()));
				dwarfItems.get(classI).setItemMeta(cim);
				
				Attribute att = Attribute.newBuilder().uuid(classselectionID).name(""+classI).amount(0).type(AttributeType.GENERIC_ATTACK_DAMAGE).build();
				Attributes attributes = new Attributes(dwarfItems.get(classI));
				attributes.add(att);
				
				dwarfItems.put(classI, attributes.getStack());
			}
		}
		
		//costum dwarves
		if(!plugin.getConfig().getBoolean("new_classselection", true)) {
			for(Map.Entry<String, ItemStack> e : dwarfItems.entrySet()) {
				if(rand.nextInt(100)<DvZ.classManager.getClass(e.getKey()).getClassChance() || player.hasPermission("dvz.allclasses")/* || player.hasPermission("dvz.alldwarves")*/) {
					//game type
					int gID = DvZ.classManager.getClass(e.getKey()).getGameId();
					if(gID==0 || gID==GameType.getDwarfAndMonsterTypes(getGameType())) {
						//permissions
						if(player.hasPermission("dvz.classes."+e.getKey())) {
							inv.addItem(e.getValue());
						}
					}
				}
			}
		}
		else
		{
			final IconMenu im = new IconMenu(ConfigManager.getLanguage().getString("string_choose","Choose your class!"), 9, new GameOptionClickEventHandler(this) {
				
	            @Override
	            public void onOptionClick(IconMenu.OptionClickEvent event) {
	            	if(!isPlayer(event.getPlayer().getUniqueId())) {
	            		event.setWillClose(true);
	                    event.setWillDestroy(true);
	                    return;
	            	}
	            	
	            	//safety for not running games
	            	if(!game.isRunning()) {
	            		resetPlayerToWorldLobby(player, true);
	    				
	    				event.setWillClose(true);
	                    event.setWillDestroy(true);
	                    return;
	            	}
	            	
	            	boolean classFound = false;
	            	final Player player = event.getPlayer();
	            	
	            	String classID = "";
	            	Attributes attributes = new Attributes(event.getItem());
	            	for(Attribute att : attributes.values()) {
	            		if(att.getUUID().equals(classselectionID)) {
	            			classID = att.getName();
	            		}
	            	}
	    			
	    			CustomClass cm = DvZ.classManager.getClass(classID/*i*/);
	    			cm.becomeClass(game, player);
	    			classFound = true;
	    			
	    			if (classFound) {
	    				SpecialPlayer sp = DvZ.playerManager.getPlayer(player.getUniqueId());
	    				if(sp!=null) {
	    					sp.addCrytalItems(game, player);
	    				}
	    				
	    				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
							public void run() {
								Location loc = getTeam(player.getUniqueId()).getSpawnLocation(getWorld());
								player.teleport(loc);
							}
						}, 1);
	    				
	    				event.setWillClose(true);
	                    event.setWillDestroy(true);
	    			}
	                //DvZ.sendPlayerMessageFormated(event.getPlayer(), "You have chosen " + event.getName());
	            }
	        },  plugin);
			im.setSpecificTo(player);

			//adding
			int pos = 0;
			for(Map.Entry<String, ItemStack> e : dwarfItems.entrySet()) {
				if(rand.nextInt(100)<DvZ.classManager.getClass(e.getKey()).getClassChance() || player.hasPermission("dvz.allclasses")/* || player.hasPermission("dvz.alldwarves")*/) {
					//game type
					int gID = DvZ.classManager.getClass(e.getKey()).getGameId();
					if(gID==0 || gID==GameType.getDwarfAndMonsterTypes(getGameType())) {
						//permissions
						if(player.hasPermission("dvz.classes."+e.getKey())) {
							im.setOption(pos, e.getValue()); 
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
	
	public void resetPlayerToWorldLobby(final Player player, boolean resetState) {
		if(resetState) {
			playerstate.put(player.getUniqueId(), STATE_PREGAME);
			//TODO - is a teamreset needed here or not?
			playerteam.remove(player.getUniqueId());
		}
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				World w;
				if(ConfigManager.getStaticConfig().getBoolean("use_lobby", true))
					w = Bukkit.getServer().getWorld(ConfigManager.getStaticConfig().getString("world_prefix", "DvZ_")+"Lobby");
				else
					w = Bukkit.getServer().getWorlds().get(0);
				
				if(w!=null) {
					if(player.getWorld()!=w || player.getLocation().distanceSquared(w.getSpawnLocation())>2) {
						player.teleport(w.getSpawnLocation());
					}
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

		UUID puuid = player.getUniqueId();
		
		if(getPlayerState(puuid).equals(Game.STATE_CHOOSECLASS)) { //class pick
			boolean classFound = false;
			
			//safety for not running games
        	if(!isRunning()) {
        		resetPlayerToWorldLobby(player, true);
        		return;
        	}
			
			//costum dwarves
        	String classID = "";
        	Attributes attributes = new Attributes(event.getItem());
        	for(Attribute att : attributes.values()) {
        		if(att.getUUID().equals(classselectionID)) {
        			classID = att.getName();
        		}
        	}
        	
        	if(!classID.equals("")) {
	        	CustomClass cm = DvZ.classManager.getClass(classID/*i*/);
				cm.becomeClass(this, player);
				classFound = true;
        	}
			
			if (classFound) {
				SpecialPlayer sp = DvZ.playerManager.getPlayer(player.getUniqueId());
				if(sp!=null) {
					sp.addCrytalItems(this, player);
				}
				Location loc = getTeam(player.getUniqueId()).getSpawnLocation(getWorld());
				player.teleport(loc);
				addSpawnBuff(player, getTeam(player.getUniqueId()).getSpawnBuff());
			}
		}
		
		//disable clicking when monsters are not released
		if(getTeam(puuid)==null || !getTeam(puuid).isReleased()) {
			return;
		}
		
		//custom class - rightclick
		if(getPlayerState(puuid).startsWith(STATE_CLASSPREFIX)) {
			CustomClass cd = getClass(puuid);
				
			//transmute items
			if(block!=null) {
				if(cd.transmuteItemOnBlock(this, player, item, block)) {
					event.setCancelled(true);
				}
			}
		}
		
		//TODO - recreate as special items for teams
		if(item.getType()==Material.ENDER_STONE) ItemPortal.spellDisablePortal(this, player);
		
		//dragon
		if(dragon!=null) {
			if(dragon instanceof PlayerDragon) {
				if(player == dragon.getEntity()) {
					((PlayerDragon) dragon).playerRC(item, block);
				}
			}
		}
	}
	
	public void playerBreakBlock(Player player, Block block) {
		if(isPlayer(player.getUniqueId()) && getPlayerState(player.getUniqueId()).startsWith(STATE_CLASSPREFIX)) {
			CustomClass cd = getClass(player.getUniqueId());
			
			if(cd != null)
				cd.transmuteItemOnBreak(this, player, block);
		}
	}
	
	//#######################################
	//Anfang des Spieles Spieler hizuf�gen
	//#######################################
	public boolean addPlayer(UUID player) {
		//nur wenn noch nicht eingetragen und spiel nicht gestartet
		if (!playerstate.containsKey(player) && state==GameState.IDLING) {
			playerstate.put(player, STATE_PREGAME);	//nix, pregame
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
	//Spawn unverwundbar f�r ... Sekunden
	//#######################################
	public void addSpawnBuff(Player player, int time) {
		//int time = plugin.getConfig().getInt("monster_invulnarable", 30);
		if(time<=0) return;
		
		player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, time*20, 4), false);
		player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, time*20, 6), false);
		
		DvZ.sendPlayerMessageFormated(player, ConfigManager.getLanguage().getString("string_invulnarable","You are -0- seconds invulnarable!").replace("-0-", ""+time));
		
		setCustomCooldown(player.getUniqueId(), "monster_invulnarability", time);
	}
	
	private void addSpawnBuffItems(Player player) {
		if(player!=null) {
			Team team = getTeam(player.getUniqueId());
			if(team!=null && !team.getSpawnBuffItems().isEmpty()) {
				PlayerInventory inv = player.getInventory();
				
				for(String item : team.getSpawnBuffItems()) {
					ItemStack it = ItemHandler.decodeItem(item, player);
					if(it!=null) {
						inv.addItem(it);
					}
				}
				
				InventoryHandler.updateInventory(player);
			}
		}
	}
	
	public boolean isBuffed(UUID player) {
		return getCustomCooldown(player, "monster_invulnarability")>0;
	}
	
	//#######################################
	//Creating the Monument
	//#######################################
	public static void createMonument(Location loc, boolean obsi) {
		Block block = loc.getWorld().getBlockAt(loc);
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
	public String getPlayerState(UUID player) {
		if (playerstate.containsKey(player))
			return playerstate.get(player);
		else
			return "";
	}
	public CustomClass getClass(UUID player) {
		if(playerstate.containsKey(player) && playerstate.get(player).startsWith(STATE_CLASSPREFIX))
			return DvZ.classManager.getClass(playerstate.get(player).replace(STATE_CLASSPREFIX, ""));
		else
			return null;
	}
	public Team getTeam(UUID player) {
		return teamSetup.getTeam(playerteam.get(player));
	}
	public ArrayList<UUID> getTeamPlayers(Team team) {
		ArrayList<UUID> ret = new ArrayList<UUID>();
		for(UUID player : playerstate.keySet()) {
			if(team.getName().equals(playerteam.get(player))) {
				ret.add(player);
			}
		}
		return ret;
	}
	
	//#######################################
	//Setze Playerstate
	//#######################################
	public void setPlayerState(UUID player, String pstate) {
		playerstate.put(player, pstate);
	}
	public void setPlayerTeam(UUID player, String team) {
		playerteam.put(player, team);
	}
	
	//#######################################
	//Alle Spieler neu "Disguisen" - Tempor�rer Workaround
	//#######################################
	public void redisguisePlayers() {
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				Object[] rplayers = playerstate.keySet().toArray();
				for(int i=0; i<rplayers.length; i++) {
					UUID playern = (UUID) rplayers[i];
					Player player = Bukkit.getPlayer(playern);
					
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
			File locationFile = new File(wf, "/dvz/locations.yml");
			
			if(locationFile.exists()) {
				loadNewGameInfo(locationFile);
			} else {
				loadLegacyGameInfo();
			}
		}
	}
	
	private void loadNewGameInfo(final File f) {
		final int gtemp = plugin.getGameID(this);
		
		taskid2 = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			public void run() {
				World w = Bukkit.getServer().getWorld(plugin.getConfig().getString("world_prefix", "DvZ_")+"Main"+gtemp+"");
				if(w!=null) {
					teamSetup.loadLocations(DVZFileConfiguration.loadConfiguration(f), w);
					
					endTask2();
				}
		    }
		}, 20, 20);
	}
	
	private void loadLegacyGameInfo() {
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
								Location spawnDwarves = new Location(Bukkit.getServer().getWorld(plugin.getConfig().getString("world_prefix", "DvZ_")+"Main"+gtemp+""), 
										Double.parseDouble(strings[0]), Double.parseDouble(strings[1]), Double.parseDouble(strings[2]), Float.parseFloat(strings[3]), Float.parseFloat(strings[4]));
								Team dTeam = teamSetup.getTeam("dwarves");
								if(dTeam!=null) {
									dTeam.setSpawnLocation(spawnDwarves);
								}
							} catch (Exception e) {}
						}
						if(spawnM.exists()) {
							try {
								String st = (String) Slapi.load(spawnM.getPath());
								String[] strings = st.split(":");
								Location spawnMonsters = new Location(Bukkit.getServer().getWorld(plugin.getConfig().getString("world_prefix", "DvZ_")+"Main"+gtemp+""), 
										Double.parseDouble(strings[0]), Double.parseDouble(strings[1]), Double.parseDouble(strings[2]), Float.parseFloat(strings[3]), Float.parseFloat(strings[4]));
								Team mTeam = teamSetup.getTeam("zombies");
								if(mTeam!=null) {
									mTeam.setSpawnLocation(spawnMonsters);
								}
							} catch (Exception e) {}
						}
						if(monF.exists()) {
							try {
								String st = (String) Slapi.load(monF.getPath());
								String[] strings = st.split(":");
								Location monument = new Location(Bukkit.getServer().getWorld(plugin.getConfig().getString("world_prefix", "DvZ_")+"Main"+gtemp+""), 
										Double.parseDouble(strings[0]), Double.parseDouble(strings[1]), Double.parseDouble(strings[2]));
								Team dTeam = teamSetup.getTeam("dwarves");
								if(dTeam!=null) {
									dTeam.setMonumentLocation(monument);
									createMonument(monument, false);
								}
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
	public void release(Team team) {
		//released = true;
		
		Object[] rplayers = playerstate.keySet().toArray();
		for(int i=0; i<rplayers.length; i++) {
			UUID playern = (UUID) rplayers[i];
			Player player = Bukkit.getServer().getPlayer(playern);

			if(player!=null && getTeam(playern)==team) {
				waitm.release(player);
			}
		}
		
		broadcastMessage(ConfigManager.getLanguage().getString("string_release", "-0- have been released!").replace("-0-", team.getDisplayName()));
	}
	
	//teleport unreleased monsters back to their spawn
	public void teleportUnreleased() {
		//int minutes = (int) Math.floor(releasetime/60);
		//int seconds = releasetime - minutes*60;

		String message = ConfigManager.getLanguage().getString("string_release_wait", "-0- are not released yet!");
		//TODO - todo somehow readd releasetime display here?(not possible?)
		//String message2 = ConfigManager.getLanguage().getString("string_release_time", "&cMaximum Time until release: &6-0- Minutes -1- Seconds");//.replace("-0-", ""+minutes).replace("-1-", ""+seconds);
		
		Object[] rplayers = playerstate.keySet().toArray();
		for(int i=0; i<rplayers.length; i++) {
			UUID playern = (UUID) rplayers[i];
			if(!getTeam(playern).isReleased()) {
				Player player = Bukkit.getPlayer(playern);
	
				if(player!=null) {
					if (ticker==10) {
						DvZ.sendPlayerMessageFormated(player, message.replace("-0-", getTeam(playern).getDisplayName()));
						//DvZ.sendPlayerMessageFormated(player, message2);
					}
					
					if(getTeam(playern).isSelectInLobby()) {
						resetPlayerToWorldLobby(player, false);
					} else {
						Location loc = player.getLocation();
						Location target = getTeam(playern).getSpawnLocation(getWorld());
						
						if(loc.distanceSquared(target)>2) {
							player.teleport(target);
						}
					}
				}
			}
		}
	}
	
	//teleport unwanted players
	public void kickUnwanted() {
		//mainworld
		World w = getWorld();
		
		if(w!=null) {
			for(Player p : w.getPlayers()) {
				//not playing -> kick to lobby
				if(!isPlayer(p.getUniqueId()) || getPlayerState(p.getUniqueId()).equals(STATE_PREGAME)) {
					resetPlayerToWorldLobby(p, true);
					playerstate.remove(p.getUniqueId());
					playerteam.remove(p.getUniqueId());
					//DvZ.instance.joinGame(p, this, false);
				}
				//pickclass -> to team spawn
				if(getPlayerState(p.getUniqueId()).equals(STATE_CHOOSECLASS)) {
					if(getTeam(p.getUniqueId()).isSelectInLobby()) {
						resetPlayerToWorldLobby(p, false);
					} else {
						Location loc = getTeam(p.getUniqueId()).getSpawnLocation(w);
						if(loc.distanceSquared(p.getLocation())>2) {
							p.teleport(loc);
						}
					}
				}
			}
		}
		
		//lobby
		World wl;
		if(ConfigManager.getStaticConfig().getBoolean("use_lobby", true))
			wl = Bukkit.getServer().getWorld(ConfigManager.getStaticConfig().getString("world_prefix", "DvZ_")+"Lobby");
		else
			wl = Bukkit.getServer().getWorlds().get(0);
		
		if(wl!=null) {
			for(Player p : wl.getPlayers()) {
				if(getPlayerState(p.getUniqueId()).equals(STATE_CHOOSECLASS) || !getTeam(p.getUniqueId()).isReleased()) {
					if(getTeam(p.getUniqueId()).isSelectInLobby()) {
						continue;
					}
				}
				
				Location loc = getTeam(p.getUniqueId()).getSpawnLocation(getWorld());
				
				p.teleport(loc);
			}
		}
	}
	
	public void setDragon(Dragon dragon2) {
		dragon = dragon2;
	}
	
	//#######################################
	//Gamestate setzen
	//#######################################
	public void setGameState(GameState newstate) {
		state = newstate;
	}
	
	public int getOnlinePlayers() {
		int counter = 0;
		for(UUID puuid : playerstate.keySet()) {
			if(Bukkit.getPlayer(puuid)!=null) {
				counter++;
			}
		}
		return counter;
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
					
					UUID playern = (UUID) rplayers[ii];
					Player player = Bukkit.getServer().getPlayer(playern);
					
					if (player!=null && w!=null) {
						player.teleport(w.getSpawnLocation());
					}
					
					ii++;
				}
				
			}.runTaskTimer(plugin, 0, ConfigManager.getStaticConfig().getInt("delayed_teleporation", 0));
		} else {
			//instant teleportation
			for(int i=0; i<rplayers.length; i++) {
				UUID playern = (UUID) rplayers[i];
				Player player = Bukkit.getServer().getPlayer(playern);
				
				if (player!=null && w!=null) {
					player.teleport(w.getSpawnLocation());
				}
			}
		}
	}
	
	//#######################################
	//Broadcast Message nur f�r die Spieler
	//#######################################
	public void broadcastMessage(String message) {
		Object[] rplayers = playerstate.keySet().toArray();
		for(int i=0; i<rplayers.length; i++) {
			UUID playern = (UUID) rplayers[i];
			Player player = Bukkit.getServer().getPlayer(playern);
			
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
		customCooldown.put(player+"::"+name, time);
	}
	public int getCustomCooldown(UUID player, String name) {
		if(customCooldown.containsKey(player+"::"+name)) {
			return customCooldown.get(player+"::"+name);
		}
		
		return -1;
	}
	public void resetCustomCooldown(UUID player, String name) {
		customCooldown.remove(player+"::"+name);
	}
	
	public Inventory getCrystalChest(UUID pname, boolean global) {
		if(global) {
			return globalCrystalChest;
		} else {
			if(!crystalPerPlayer.containsKey(pname)) crystalPerPlayer.put(pname, Bukkit.createInventory(null, ConfigManager.getStaticConfig().getInt("privatestorage", 27), ConfigManager.getLanguage().getString("string_crystal_storage", "Crystal Storage")));
			
			return crystalPerPlayer.get(pname);
		}
	}
	
	public World getWorld() {
		return Bukkit.getServer().getWorld(plugin.getConfig().getString("world_prefix", "DvZ_")+"Main"+plugin.getGameID(this)+"");
	}
	
	public int getGameType() {
		return gameType;
	}
	
	public GameState getState() {
		return state;
	}
	
	public boolean getStarting() {
		return starting;
	}
	
	public boolean isRunning() {
		return state==GameState.RUNNING;
	}
	
	public int getStartTime() {
		return starttime;
	}
	
	public int getDauer() {
		return dauer;
	}
}
