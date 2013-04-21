package me.andre111.dvz;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import me.andre111.dvz.dragon.Dragon;
import me.andre111.dvz.dragon.PlayerDragon;
import me.andre111.dvz.event.DVZGameEndEvent;
import me.andre111.dvz.event.DVZGameStartEvent;
import me.andre111.dvz.item.CustomItem;
import me.andre111.dvz.monster.CustomMonster;
import me.andre111.dvz.utils.ExperienceUtils;
import me.andre111.dvz.utils.GameOptionClickEventHandler;
import me.andre111.dvz.utils.IconMenu;
import me.andre111.dvz.utils.ItemHandler;
import me.andre111.dvz.utils.Slapi;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import pgDev.bukkit.DisguiseCraft.disguise.Disguise;

public class Game {
	private DvZ plugin;
	private int state;
	private int time;
	private int dauer;
	private int ticker;
	private boolean starting;
	
	public Location spawnDwarves;
	public Location spawnMonsters;
	
	public boolean enderActive;
	public Location enderPortal;
	public String enderMan;
	
	public Location monument;
	public boolean monumentexists;
	private String lastdwarf;
	
	private Inventory eciTest;
	
	public HashMap<String, Integer> playerstate = new HashMap<String, Integer>();
	//1 = nix
	//2 = choose dwarf
	//3 = choose monster
	//10 - 29 = dwarves
	//30 - 49 = monsters
	//80 = dragon warrior
	//90 = assasin
	//100 - ?? = dragon
	//TODO - use this vars everywhere
	public static int pickDwarf = 2;
	public static int pickMonster = 3;
	public static int dwarfMin = 10;
	public static int dwarfMax = 29;
	public static int monsterMin = 30;
	public static int monsterMax = 49;
	public static int assasinState = 90;
	public static int dragonWarrior = 80;
	public static int dragonMin = 100;
	
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
	
	private int[][] effectMonsterDay;
	private int[][] effectMonsterNight;
	private int[][] effectMonsterMidNight;
	private int[][] effectDwarfAbove;
	private int[][] effectDwarfBelow;
	
	//lobby
	private int lobby_Player;
	
	//#######################################
	//Neues Spiel
	//#######################################
	public Game(DvZ p) {
		this.state = 1;
		this.time = 30;//60;
		this.plugin = p;
		this.dauer = 0;
		ticker = 0;
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
		
		eciTest = Bukkit.createInventory(null, 27, DvZ.getLanguage().getString("string_crystal_storage", "Crystal Storage"));
		
		plugin.waitm.close();
		released = plugin.getConfig().getString("need_release", "false")=="false";
		
		initPlayerEffects();
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
				}
				//clear inventory
				ItemHandler.clearInv(player);
				
				StatManager.hide(player);
			}
			
			StatManager.resetPlayer(playern);
		}
		
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
		
		eciTest = Bukkit.createInventory(null, 27, DvZ.getLanguage().getString("string_crystal_storage", "Crystal Storage"));
		
		plugin.waitm.close();
		released = plugin.getConfig().getString("need_release", "false")=="false";
		
		spell1time.clear();
		spell2time.clear();
		spell3time.clear();
		spell4time.clear();
		invultimer.clear();
		customCooldown.clear();
		
		plugin.resetMainWorld(plugin.getGameID(this));
		
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
					broadcastMessage(DvZ.getLanguage().getString("string_lobby_waiting","Waiting for the Game to start..."));
					broadcastMessage(DvZ.getLanguage().getString("string_lobby_players","-0-/-1- Players for Game to start!").replaceAll("-0-", ""+playerstate.size()).replaceAll("-1-", ""+plugin.getConfig().getInt("lobby_players", 20)));
				}
			}
		//Autostart{
			if(lobby_Player>0) {
				if(playerstate.size()>=lobby_Player) {
					broadcastMessage(DvZ.getLanguage().getString("string_game_start","Game starting in -0- Seconds!").replaceAll("-0-", ""+plugin.getConfig().getInt("lobby_starttime", 60)));
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
			
			if (time==60*5) broadcastMessage(DvZ.getLanguage().getString("string_starting_minutes","-0- Minutes left!").replaceAll("-0-", "5"));
			else if (time==60) broadcastMessage(DvZ.getLanguage().getString("string_starting_minute","-0- Minute left!").replaceAll("-0-", "1"));
			else if (time==10) broadcastMessage(DvZ.getLanguage().getString("string_starting_seconds","-0- Seconds left!").replaceAll("-0-", "10"));
			else if (time==5) broadcastMessage(DvZ.getLanguage().getString("string_starting_seconds","-0- Seconds left!").replaceAll("-0-", "5"));
			else if (time==4) broadcastMessage(DvZ.getLanguage().getString("string_starting_seconds","-0- Seconds left!").replaceAll("-0-", "4"));
			else if (time==3) broadcastMessage(DvZ.getLanguage().getString("string_starting_seconds","-0- Seconds left!").replaceAll("-0-", "3"));
			else if (time==2) broadcastMessage(DvZ.getLanguage().getString("string_starting_seconds","-0- Seconds left!").replaceAll("-0-", "2"));
			else if (time==1) broadcastMessage(DvZ.getLanguage().getString("string_starting_second","-0- Second left!").replaceAll("-0-", "1"));
			else if (time==0) startGame();//timeUp();
			
			if (time<=0) {
				dauer++;
				ticker++;
				
				if(DvZ.getStaticConfig().getString("global_stats", "true").equals("true"))
					updateGlobalStats();
				
				if (ticker==10) {
					ticker = 0;
					checkLoose();
				}
				if(ticker%2==0) {
					playerEffects();
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
				
				if (releasetime>=0 && state==2) {
					releasetime--;
					if(releasetime==0 && state==2) {
						release();
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

	//#######################################
	//Starte das wirkliche Spiel/oder geht weiter
	//#######################################
	//TODO - Start anders machen/moderator...
	private int taskid;
	public void startGame() {
		if (state==1) {
			final Game gea = this;
			
			plugin.newMainWorld(plugin.getGameID(gea));
			
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
						releasetime = plugin.getConfig().getInt("time_release",30)*60;
						
						DvZ.startedGames += 1;
						
						for(Map.Entry<String, Integer> e : playerstate.entrySet()){
							String players = e.getKey();
							int pstate = e.getValue();
							
							if (pstate==1) {
								playerstate.put(players, 2);
								Player player = Bukkit.getServer().getPlayer(players);
								player.getInventory().clear();
								player.sendMessage(DvZ.getLanguage().getString("string_choose","Choose your class!"));
								addDwarfItems(player);
							}
						}
						
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
			int state = e.getValue();
			boolean online = false;
			Player player = Bukkit.getServer().getPlayerExact(e.getKey());
			if (player!=null) online = true;
			
			if (state>=10 && state<30) {
				if (online) dwarf++; //else dwarfoff++;
				
				lastdwarf = e.getKey();
			}
			if (state>=30 && state<50) {
				if (online) mons++; else monsoff++;
			}
		}
		
		if (dwarf==0 && (mons>=1 || monsoff>=1)) {
			broadcastMessage(DvZ.getLanguage().getString("string_lose_dwarf","§4Game Over!§f No more Dwarves!"));
			if(lastdwarf!=null)
				broadcastMessage(DvZ.getLanguage().getString("string_last_dwarf","Last standing Dwarf - §e-0-§f! Congratulations!").replaceAll("-0-", lastdwarf));
			
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
		
		if (destr) {
			broadcastMessage(DvZ.getLanguage().getString("string_lose_monument","§4Game Over!§f The Monument has been destroyed!"));
			reset(true);
		}
	}
	
	private void updateGlobalStats() {
		int dwarf = 0;
		int mons = 0;
		
		for(Map.Entry<String, Integer> e : playerstate.entrySet()){
			if (isDwarf(e.getKey())) {
				dwarf++;
			}
			if (isMonster(e.getKey())) {
				mons++;
			}
		}
		
		StatManager.setGlobalStat(DvZ.getLanguage().getString("scoreboard_dwarves", "Dwarves"), dwarf);
		StatManager.setGlobalStat(DvZ.getLanguage().getString("scoreboard_monsters", "Monsters"), mons);
	}
	
	private void initPlayerEffects() {
		//monster
		List<String> monsterDayEffects = DvZ.getMonsterFile().getStringList("effects.day");
		List<String> monsterNightEffects = DvZ.getMonsterFile().getStringList("effects.night");
		List<String> monsterMidNightEffects = DvZ.getMonsterFile().getStringList("effects.midnight");
		
		//Day
		effectMonsterDay = new int[monsterDayEffects.size()][2];
		for(int i=0; i<monsterDayEffects.size(); i++) {
			String effect = monsterDayEffects.get(i);
			while(effect.startsWith(" ")) effect = effect.substring(1);
			while(effect.endsWith(" ")) effect = effect.substring(0, effect.length()-1);
				
			int eid = -1;
			int elevel = 0;
				
			String[] effectPart = effect.split(" ");
			if(effectPart.length>0) eid = Integer.parseInt(effectPart[0]);
			if(effectPart.length>1) elevel = Integer.parseInt(effectPart[1]);
			
			effectMonsterDay[i][0] = eid;
			effectMonsterDay[i][1] = elevel;
		}
		//Night
		effectMonsterNight = new int[monsterNightEffects.size()][2];
		for(int i=0; i<monsterNightEffects.size(); i++) {
			String effect = monsterNightEffects.get(i);
			while(effect.startsWith(" ")) effect = effect.substring(1);
			while(effect.endsWith(" ")) effect = effect.substring(0, effect.length()-1);
				
			int eid = -1;
			int elevel = 0;
				
			String[] effectPart = effect.split(" ");
			if(effectPart.length>0) eid = Integer.parseInt(effectPart[0]);
			if(effectPart.length>1) elevel = Integer.parseInt(effectPart[1]);
			
			effectMonsterNight[i][0] = eid;
			effectMonsterNight[i][1] = elevel;
		}
		//Night
		effectMonsterMidNight = new int[monsterMidNightEffects.size()][2];
		for(int i=0; i<monsterMidNightEffects.size(); i++) {
			String effect = monsterMidNightEffects.get(i);
			while(effect.startsWith(" ")) effect = effect.substring(1);
			while(effect.endsWith(" ")) effect = effect.substring(0, effect.length()-1);

			int eid = -1;
			int elevel = 0;

			String[] effectPart = effect.split(" ");
			if(effectPart.length>0) eid = Integer.parseInt(effectPart[0]);
			if(effectPart.length>1) elevel = Integer.parseInt(effectPart[1]);

			effectMonsterMidNight[i][0] = eid;
			effectMonsterMidNight[i][1] = elevel;
		}
		
		//dwarves
		effectDwarfAbove = new int[16][2];
		effectDwarfBelow = new int[16][2];
		for(int k=0; k<16; k++) {
			//above
			String dwarfEffects = DvZ.getClassFile().getString("effects.lightlevel.above."+k, "");
			
			effectDwarfAbove[k][0] = -1;
			effectDwarfAbove[k][1] = 0;
			
			if(!dwarfEffects.equals("")) {
				while(dwarfEffects.startsWith(" ")) dwarfEffects = dwarfEffects.substring(1);
				while(dwarfEffects.endsWith(" ")) dwarfEffects = dwarfEffects.substring(0, dwarfEffects.length()-1);
	
				int eid = -1;
				int elevel = 0;
	
				String[] effectPart = dwarfEffects.split(" ");
				if(effectPart.length>0) eid = Integer.parseInt(effectPart[0]);
				if(effectPart.length>1) elevel = Integer.parseInt(effectPart[1]);
	
				effectDwarfAbove[k][0] = eid;
				effectDwarfAbove[k][1] = elevel;
			}
			
			//below
			String dwarfEffects2 = DvZ.getClassFile().getString("effects.lightlevel.below."+k, "");

			effectDwarfBelow[k][0] = -1;
			effectDwarfBelow[k][1] = 0;
			
			if(!dwarfEffects2.equals("")) {
				while(dwarfEffects2.startsWith(" ")) dwarfEffects2 = dwarfEffects2.substring(1);
				while(dwarfEffects2.endsWith(" ")) dwarfEffects2 = dwarfEffects2.substring(0, dwarfEffects2.length()-1);

				int eid2 = -1;
				int elevel2 = 0;

				String[] effectPart2 = dwarfEffects2.split(" ");
				if(effectPart2.length>0) eid2 = Integer.parseInt(effectPart2[0]);
				if(effectPart2.length>1) elevel2 = Integer.parseInt(effectPart2[1]);

				effectDwarfBelow[k][0] = eid2;
				effectDwarfBelow[k][1] = elevel2;
			}
		}
	}
	
	private void playerEffects() {
		//MONSTERS:
		//----------------------------
		int id = plugin.getGameID(this);
		World w =  Bukkit.getServer().getWorld(plugin.getConfig().getString("world_prefix", "DvZ_")+"Main"+id+"");
		long time = w.getTime();
		int[][] effects = effectMonsterDay;
		//Day
		if(time>=0 && time<=12000) {
			effects = effectMonsterDay;
		//Night
		} else {
			//midnight
			if(time>=18000-500 && time<=18000+500) {
				effects = effectMonsterMidNight;
			} else {
				effects = effectMonsterNight;
			}
		}
		
		for(int i=0; i<effects.length; i++) {
			int effect = effects[i][0];
			int level = effects[i][1];
			
			if(effect!=-1) {
				addMonsterEffect(effect, level);
			}
		}
		//----------------------------
		addDwarfEffects();
	}
	
	private void addMonsterEffect(int id, int level) {
		for(Map.Entry<String, Integer> e : playerstate.entrySet()){
			int state = e.getValue();
			String playern = e.getKey();
			
			if(state>=Game.monsterMin && state<=Game.monsterMax) {
				Player player = Bukkit.getServer().getPlayerExact(playern);
				
				if(player!=null) {
					if(!hasHigherPotionEffect(player, id, level)) {
						player.addPotionEffect(new PotionEffect(PotionEffectType.getById(id), 3*20, level), true);
					}
				}
			}
		}
	}
	
	private void addDwarfEffects() {
		for(Map.Entry<String, Integer> e : playerstate.entrySet()){
			int state = e.getValue();
			String playern = e.getKey();
			
			if(state>=Game.dwarfMin && state<=Game.dwarfMax) {
				Player player = Bukkit.getServer().getPlayerExact(playern);
				if(player!=null) {
					int light = player.getLocation().getBlock().getLightLevel();

					//above
					for(int i=light-1; i>=0; i--) {
						int id = effectDwarfAbove[i][0];
						int level = effectDwarfAbove[i][1];
						if(id!=-1)
						if(!hasHigherPotionEffect(player, id, level)) {
							player.addPotionEffect(new PotionEffect(PotionEffectType.getById(id), 3*20, level), true);
						}
					}
					//below
					for(int i=light+1; i<16; i++) {
						int id = effectDwarfBelow[i][0];
						int level = effectDwarfBelow[i][1];
						if(id!=-1)
						if(!hasHigherPotionEffect(player, id, level)) {
							player.addPotionEffect(new PotionEffect(PotionEffectType.getById(id), 3*20, level), true);
						}
					}
				}
			}
		}
	}
	
	private boolean hasHigherPotionEffect(Player player, int id, int level) {
		if(player.hasPotionEffect(PotionEffectType.getById(id))) {
			PotionEffect[] effects = (PotionEffect[]) player.getActivePotionEffects().toArray(new PotionEffect[player.getActivePotionEffects().size()]);
			for(int i=0; i<effects.length; i++) {
				if(effects[i].getType()==PotionEffectType.getById(id)) {
					if(effects[i].getAmplifier()>level) {
						return true;
					}
				}
			}
		}
		return false;
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
					broadcastMessage(DvZ.getLanguage().getString("string_no_assasins","No Assasins have been chosen - Because there where not enough online Dwarves!!"));
					return;
				}
			while(!(playerstate.get(playern)>=dwarfMin && playerstate.get(playern)<=dwarfMax) || player==null) {
				playern = (String) rplayers[rand.nextInt(rplayers.length)];
				player = Bukkit.getServer().getPlayerExact(playern);
			}
			
			//Player player = Bukkit.getServer().getPlayerExact(playern);
			//if(player!=null) {
				player.sendMessage(DvZ.getLanguage().getString("string_become_assasin","You have been chosen to be a Assasin!"));
				
				playerstate.put(player.getName(), 90);
				
				//time
				int asstime = DvZ.getClassFile().getInt("assasin_time_minutes",5);
				if(asstime>0) {
					spell3time.put(player.getName(), asstime*60);
					player.sendMessage(DvZ.getLanguage().getString("string_become_assasin_time","If you don't kill someone within the next -0- minutes you will die!").replaceAll("-0-", ""+asstime));
				}
				
				//add assasin items to inventory
				PlayerInventory inv = player.getInventory();
				List<String> itemstrings = DvZ.getClassFile().getStringList("assasin_items");
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
		broadcastMessage(DvZ.getLanguage().getString("string_assasins","-0- Assasins have been chosen!!").replaceAll("-0-", ""+count));
	}
	
	//#######################################
	//Countdown Over
	//#######################################
	public void countdownEnd(String player, int countdown) {
		//assasin
		if(playerstate.get(player)==90) {
			if(countdown==3) {
				Player playern = Bukkit.getServer().getPlayerExact(player);
				if(playern!=null) {
					playern.damage(1000);
					playern.sendMessage(DvZ.getLanguage().getString("string_assasin_timeup","Your time is up!"));
				}
			}
		}
	}
	
	//#######################################
	//Dwarf Items hinzufügen
	//#######################################
	public void addDwarfItems(Player player) {
		Random rand = new Random();
		PlayerInventory inv = player.getInventory();
		
		resetCountdowns(player.getName());
		
		//costum dwarves
		if(plugin.getConfig().getString("new_classselection","true")!="true") {
			for(int i=1; i<=10; i++) {
				ItemStack cd = new ItemStack(2255+i, 1);
				ItemMeta cim = cd.getItemMeta();
				cim.setDisplayName(DvZ.getLanguage().getString("string_become","Become -0-").replaceAll("-0-", DvZ.getClassFile().getString("custom_d"+i+"_name","")));
				cd.setItemMeta(cim);
				
				if (rand.nextInt(100)<DvZ.getClassFile().getInt("custom_d"+i+"_chance",0)) {
					inv.addItem(cd); 
				}
			}
		}
		else
		{
			IconMenu im = new IconMenu(player.getName()+" - "+DvZ.getLanguage().getString("string_choose","Choose your class!"), 9, new GameOptionClickEventHandler(this) {
				
	            @Override
	            public void onOptionClick(IconMenu.OptionClickEvent event) {
	            	if(!isPlayer(event.getPlayer().getName())) {
	            		event.setWillClose(true);
	                    event.setWillDestroy(true);
	                    return;
	            	}
	            	
	            	boolean dwarf = false;
	    			
	    			//costum dwarves
	    			for(int i=1; i<=10; i++) {
	    				if(event.getItemID()==2255+i) { Classswitcher.becomeCustomDwarf(game, event.getPlayer(), i); dwarf=true; }
	    			}
	    			
	    			if (dwarf) {
	    				if(spawnDwarves!=null) {
	    					event.getPlayer().teleport(spawnDwarves);
	    				}
	    				
	    				event.setWillClose(true);
	                    event.setWillDestroy(true);
	    			}
	                //event.getPlayer().sendMessage("You have chosen " + event.getName());
	            }
	        },  plugin);
			int pos = 0;
			
			for(int i=1; i<=10; i++) {
				ItemStack cd = new ItemStack(2255+i, 1);
				ItemMeta cim = cd.getItemMeta();
				cim.setDisplayName(DvZ.getLanguage().getString("string_become","Become -0-").replaceAll("-0-", DvZ.getClassFile().getString("custom_d"+i+"_name","")));
				cd.setItemMeta(cim);
				
				if (rand.nextInt(100)<DvZ.getClassFile().getInt("custom_d"+i+"_chance",0)) {
					im.setOption(pos, cd, DvZ.getLanguage().getString("string_become","Become -0-").replaceAll("-0-", DvZ.getClassFile().getString("custom_d"+i+"_name","")), "");
					pos++;
				}
			}
			
			im.open(player);
		}
	}
	
	//#######################################
	//Monster Items hinzufügen
	//#######################################
	public void addMonsterItems(Player player) {
		Random rand = new Random();
		PlayerInventory inv = player.getInventory();

		resetCountdowns(player.getName());
		
		ItemStack[] monsterItems = new ItemStack[DvZ.monsterManager.getCount()];
		
		if(plugin.getConfig().getString("new_classselection","true")!="true") {
			for(int i=0; i<DvZ.monsterManager.getCount(); i++) {
				monsterItems[i] = new ItemStack(DvZ.monsterManager.getMonster(i).getClassItem(), 1, (short)DvZ.monsterManager.getMonster(i).getClassItemDamage());
				ItemMeta cim = monsterItems[i].getItemMeta();
				cim.setDisplayName(DvZ.getLanguage().getString("string_become","Become -0-").replaceAll("-0-", DvZ.monsterManager.getMonster(i).getName()));
				monsterItems[i].setItemMeta(cim);
			}
		}
		
		if(plugin.getConfig().getString("new_classselection","true")!="true") {
			for(int i=0; i<DvZ.monsterManager.getCount(); i++) {
				if(rand.nextInt(100)<DvZ.monsterManager.getMonster(i).getClassChance()) {
					inv.addItem(monsterItems[i]);
				}
			}
		}
		else
		{
			IconMenu icm = new IconMenu(player.getName()+" - "+DvZ.getLanguage().getString("string_choose","Choose your class!"), 18, new GameOptionClickEventHandler(this) {
				
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
	            	Player player = event.getPlayer();
	            	
	    			for(int i=0; i<DvZ.monsterManager.getCount(); i++) {
	    				CustomMonster cm = DvZ.monsterManager.getMonster(i);
	    				if(itemId==cm.getClassItem() && itemD==cm.getClassItemDamage()) {
	    					cm.becomeMonster(game, player);
	    					monster = true;
	    				}
	    			}
	    			
	    			if (monster) {
	    				if(spawnMonsters!=null) {
	    					player.teleport(spawnMonsters);
	    				}
	    				
	    				event.setWillClose(true);
	                    event.setWillDestroy(true);
	    			}
	            }
	        },  plugin);
			
			//adding
			int pos = 0;
			for(int i=0; i<DvZ.monsterManager.getCount(); i++) {
				if(rand.nextInt(100)<DvZ.monsterManager.getMonster(i).getClassChance()) {
					icm.setOption(pos, monsterItems[i]); 
					pos++;
				}
			}
			
			icm.open(player);
		}
	}
	
	//#######################################
	//Spieler hat rechtsgeklickt
	//#######################################
	public void playerRC(Player player, ItemStack item, Block block) {
		if(!isPlayer(player.getName())) return;
		if(item==null) return;
		int itemId = item.getTypeId();
		int itemD = item.getDurability();
		String pname = player.getName();
		
		if(getPlayerState(pname)==2) { //dwarf werden
			boolean dwarf = false;
			
			//costum dwarves
			for(int i=1; i<=10; i++) {
				if(itemId==2255+i) { Classswitcher.becomeCustomDwarf(this, player, i); dwarf=true; }
			}
			
			if (dwarf) {
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
		
		//costum dwarves
		for(int i=1; i<=10; i++) {
			if(getPlayerState(pname)==9+i && itemId==DvZ.getClassFile().getInt("custom_d"+i+"_spell_item",0)) Spellcontroller.spellCustomDwarf(this, player, i);
		}
		
		if(isDwarf(pname) && itemId==121) Spellcontroller.spellDisablePortal(this, player);
		if(isDwarf(pname) && itemId==388) Spellcontroller.spellEnderChest(this, player, eciTest);
		
		//custom items
		playerSpecialItemC(player, item, false, block, null);
		
		//Monster
		if(isMonster(pname) && itemId==358) Spellcontroller.spellTeleport(this, player);
		if(isMonster(pname) && itemId==370) Spellcontroller.spellSuizide(this, player);
		
		//custom monsters - rightclick
		int mId = getPlayerState(player.getName())-monsterMin;
		if(mId>=0 && mId<DvZ.monsterManager.getCount()) {
			if(block!=null) {
				DvZ.monsterManager.getMonster(mId).spellCast(this, item, player, block);
			} else {
				DvZ.monsterManager.getMonster(mId).spellCast(this, item, player);
			}
		}
		
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
		
		//custom items
		playerSpecialItemC(player, item, false, null, target);
		
		String pname = player.getName();
		//custom monsters - rightclick
		int mId = getPlayerState(pname)-monsterMin;
		if(mId>=0 && mId<DvZ.monsterManager.getCount()) {
			DvZ.monsterManager.getMonster(mId).spellCast(this, item, player, target);
		}
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
		
		//custom items
		playerSpecialItemC(player, item, true, block, null);
		
		if(itemId == 373 && isDwarf(pname)) {
			//changed from old hacky potionhandler to new bukkit functionallity
			if(ExperienceUtils.getCurrentExp(player)>=plugin.getConfig().getInt("dwarf_potion_exp", 2)) {
				ExperienceUtils.changeExp(player, -plugin.getConfig().getInt("dwarf_potion_exp", 2));
				ThrownPotion thrp = player.launchProjectile(ThrownPotion.class);
				thrp.setItem(item);
			} else {
				player.sendMessage(DvZ.getLanguage().getString("string_needexp","You don't have enough exp!"));
			}
			//Spellcontroller.spellLaunchPotion(this, player, itemD);
		}
	}
	
	//#######################################
	//Spieler hat geklickt custom item
	//#######################################
	public void playerSpecialItemC(Player player, ItemStack item, boolean left, Block block, Player target) {
		String pname = player.getName();
		
		if(isPlayer(pname)) {
			ItemMeta im = item.getItemMeta();
			if(im!=null)
			if(im.hasDisplayName()) {
				CustomItem ci = DvZ.itemManager.getItemByDisplayName(im.getDisplayName());
				if(ci!=null) {
					if(ci.isThisItem(item)) {
						if(block!=null)
							ci.cast(this, left, player, block);
						else if(target!=null)
							ci.cast(this, left, player, target);
						else
							ci.cast(this, left, player);
					}
				}
			}
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
		
		player.sendMessage(DvZ.getLanguage().getString("string_invulnarable","You are -0- seconds invulnarable!").replaceAll("-0-", ""+time));
		
		invultimer.put(player.getName(), time);
	}
	
	private void addMonsterMap(Player player) {
		//only monsters are allowed to get these items
		if(getPlayerState(player.getName())>=Game.monsterMin && getPlayerState(player.getName())<=Game.monsterMax) {
			PlayerInventory inv = player.getInventory();
			
			ItemStack it = new ItemStack(358, 1);
			ItemMeta im = it.getItemMeta();
			im.setDisplayName(DvZ.getLanguage().getString("string_spell_teleport","Teleport to Enderman Portal"));
			ArrayList<String> li4 = new ArrayList<String>();
			li4.add(DvZ.getLanguage().getString("string_used_seconds","Can be used every -0- Seconds!").replaceAll("-0-", ""+plugin.getConfig().getInt("spelltime_teleport",15)));
			im.setLore(li4);
			it.setItemMeta(im);
			inv.addItem(it);
			
			if(plugin.getConfig().getString("monster_suizidepill", "true")=="true") {
				it = new ItemStack(370, 1);
				im = it.getItemMeta();
				im.setDisplayName(DvZ.getLanguage().getString("string_spell_suizide","Suizidepill"));
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
	public boolean isDwarf(String player) {
		if(playerstate.containsKey(player)) {
			int pstate = playerstate.get(player);
			if((pstate>=Game.dwarfMin && pstate<=Game.dwarfMin)
			  || pstate==Game.assasinState || pstate==Game.dragonWarrior) {
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
			final File spawnD = new File(wf, "dvz_spawn_d.dat");
			final File spawnM = new File(wf, "dvz_spawn_m.dat");
			final File monF = new File(wf, "dvz_mon.dat");
			
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
				plugin.waitm.release(player);
			}
		}
		
		broadcastMessage(DvZ.getLanguage().getString("string_release", "The Monsters have been released!"));
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
		Object[] rplayers = playerstate.keySet().toArray();
		for(int i=0; i<rplayers.length; i++) {
			String playern = (String) rplayers[i];
			Player player = Bukkit.getServer().getPlayerExact(playern);
			World w = Bukkit.getServer().getWorld(plugin.getConfig().getString("world_prefix", "DvZ_")+"Main"+plugin.getGameID(this)+"");
			
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
