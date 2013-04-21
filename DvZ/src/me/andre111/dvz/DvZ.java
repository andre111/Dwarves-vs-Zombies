package me.andre111.dvz;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.andre111.dvz.dragon.DragonAttackListener;
import me.andre111.dvz.dragon.DragonAttackManager;
import me.andre111.dvz.dragon.DragonDeathListener;
import me.andre111.dvz.item.ItemManager;
import me.andre111.dvz.listeners.Listener_Block;
import me.andre111.dvz.listeners.Listener_Entity;
import me.andre111.dvz.listeners.Listener_Game;
import me.andre111.dvz.listeners.Listener_Player;
import me.andre111.dvz.monster.MonsterManager;
import me.andre111.dvz.utils.FileHandler;
import me.andre111.dvz.utils.Invulnerability;
import me.andre111.dvz.utils.ItemHandler;
import me.andre111.dvz.utils.Metrics;
import me.andre111.dvz.utils.Metrics.Graph;
import me.andre111.dvz.utils.MovementStopper;
import me.andre111.dvz.utils.WaitingMenu;
import me.andre111.dvz.volatileCode.DynamicClassFunctions;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import pgDev.bukkit.DisguiseCraft.DisguiseCraft;
import pgDev.bukkit.DisguiseCraft.api.DisguiseCraftAPI;
import pgDev.bukkit.DisguiseCraft.disguise.Disguise;

public class DvZ extends JavaPlugin {
	public static DvZ instance;
	//public Game game;
	private Game[] games = new Game[10];
	private GameDummy gameDummy = new GameDummy();
	public static int startedGames = 0;
	private Lobby lobby;
	public static DisguiseCraftAPI api;
	
	public static DragonAttackManager dragonAtManager;
	public static DragonDeathListener dragonDeath;
	public static DragonAttackListener attackListener;
	public static MovementStopper moveStop;
	public static Invulnerability inVul;
	
	public static MonsterManager monsterManager;
	public static ItemManager itemManager;
	
	public static Logger logger;
	public static String prefix = "[Dwarves vs Zombies] ";
	
	private static String lang = "en_EN";
	private static FileConfiguration configfile;
	private static FileConfiguration langfile;
	private static FileConfiguration dragonsfile;
	private static FileConfiguration classfile;
	private static FileConfiguration monsterfile;
	private static FileConfiguration itemfile;
	
	private ArrayList<Integer> disabledCrafts = new ArrayList<Integer>();
	
	public WaitingMenu waitm;
	
	 @Override
	 public void onLoad() {
		 logger = Logger.getLogger("Minecraft");

		 // Dynamic package detection
		 if (!DynamicClassFunctions.setPackages()) {
			 logger.log(Level.WARNING, "NMS/OBC package could not be detected, using " + DynamicClassFunctions.nmsPackage + " and " + DynamicClassFunctions.obcPackage);
		 }
		 DynamicClassFunctions.setClasses();
		 DynamicClassFunctions.setMethods();
		 DynamicClassFunctions.setFields();
	 }
	
	@Override
	public void onEnable() {
		DvZ.instance = this;
		
		initConfig();
		
		//Disguisecraft check
		if (this.getConfig().getString("disable_dcraft_check", "false")!="true") {
		if (!Bukkit.getPluginManager().isPluginEnabled("DisguiseCraft"))
		{
			Bukkit.getServer().getConsoleSender().sendMessage(prefix+" "+ChatColor.RED+"DisguiseCraft could not be found, disabling...");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		}
		DvZ.api = DisguiseCraft.getAPI();
		Spellcontroller.plugin = this;
		Classswitcher.plugin = this;
		ItemHandler.plugin = this;
		
		dragonAtManager = new DragonAttackManager();
		dragonAtManager.loadAttacks();
		dragonDeath = new DragonDeathListener(this);
		attackListener = new DragonAttackListener(this);
		moveStop = new MovementStopper(this);
		inVul = new Invulnerability(this);
		
		monsterManager = new MonsterManager();
		monsterManager.loadMonsters();
		itemManager = new ItemManager();
		itemManager.loadItems();
		
		try {
		    Metrics metrics = new Metrics(this);
		    
		    // Plot the total amount of protections
		    Graph graph = metrics.createGraph("Running Games");
		    
		    graph.addPlotter(new Metrics.Plotter("Running Games") {

		    	@Override
		    	public int getValue() {
		    		DvZ.startedGames = 0;
		    		return DvZ.startedGames;
		    	}

		    });
		    
		    metrics.start();
		    metrics.enable();
		} catch (IOException e) {
		    // Failed to submit the stats :-(
		}
		
		if(getConfig().getString("use_lobby", "true").equals("true"))
			Bukkit.getServer().createWorld(new WorldCreator(this.getConfig().getString("world_prefix", "DvZ_")+"Lobby"));
		File f = new File(Bukkit.getServer().getWorldContainer().getPath()+"/"+this.getConfig().getString("world_prefix", "DvZ_")+"Worlds/");
		if(!f.exists()) f.mkdirs();
		for (int i=0; i<10; i++) {
			File f2 = new File(Bukkit.getServer().getWorldContainer().getPath()+"/"+this.getConfig().getString("world_prefix", "DvZ_")+"Main"+i+"/");
			if(f2.exists()) FileHandler.deleteFolder(f2);
		}
		//w_main = Bukkit.getServer().createWorld(new WorldCreator(this.getConfig().getString("world_prefix", "DvZ_")+"Main"));
		
		lobby = new Lobby(this);
		
		new Listener_Player(this);
		new Listener_Block(this);
		new Listener_Entity(this);
		new Listener_Game(this);
		
		waitm = new WaitingMenu(this);
		
		//init and reset games
		for(int i=0; i<games.length; i++) {
			if(getConfig().getInt("game"+i, 1)==1) {
				games[i] = new Game(this);
				games[i].reset(false);
			}
		}
		//
		
		CommandExecutorDvZ command = new CommandExecutorDvZ(this);
		getCommand("dvztest").setExecutor(command);
		getCommand("dvz").setExecutor(command);
		getCommand("dvz_start").setExecutor(command);
		getCommand("dvz_dwarf").setExecutor(command);
		getCommand("dvz_monster").setExecutor(command);
		getCommand("dvz_info").setExecutor(command);
		getCommand("dvz_reset").setExecutor(command);
		getCommand("dvz_monument").setExecutor(command);
		getCommand("dvz_dragon").setExecutor(command);
		getCommand("dvz_add").setExecutor(command);
		getCommand("dvz_assasin").setExecutor(command);
		getCommand("dvz_join").setExecutor(command);
		getCommand("dvz_joini").setExecutor(command);
		getCommand("dvz_leave").setExecutor(command);
		getCommand("dvz_saveworld").setExecutor(command);
		getCommand("dvz_createworld").setExecutor(command);
		getCommand("dvz_release").setExecutor(command);
		
		//Run Main Game Managment
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				for(int i=0; i<games.length; i++) {
					if (games[i]!=null) {
						games[i].tick();
					}
				}
		    }
		}, 20, 20);
		//
	}
	
	@Override
	public void onDisable() {
		
	}
	
	public static void log(String s) {
		logger.info(prefix+s);
	}
	
	private void initConfig() {
		if (!new File(getDataFolder(), "config.yml").exists()) {
			saveResource("config.yml", false);
			log("Generating default config.");
			saveDefaultConfig();
		}
		//language
		if (!new File(getDataFolder(), "lang_en_EN.yml").exists()) {
			saveResource("lang_en_EN.yml", false);
		}
		if (!new File(getDataFolder(), "lang_de_DE.yml").exists()) {
			saveResource("lang_de_DE.yml", false);
		}
		//Classes and stuff
		if (!new File(getDataFolder(), "dragons.yml").exists()) {
			saveResource("dragons.yml", false);
		}
		if (!new File(getDataFolder(), "classes.yml").exists()) {
			saveResource("classes.yml", false);
		}
		if (!new File(getDataFolder(), "monster.yml").exists()) {
			saveResource("monster.yml", false);
		}
		if (!new File(getDataFolder(), "items.yml").exists()) {
			saveResource("items.yml", false);
		}
		DvZ.lang = this.getConfig().getString("language", "en_EN");
		DvZ.langfile = YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), "lang_"+lang+".yml"));
		DvZ.configfile = YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), "config.yml"));
		DvZ.dragonsfile = YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), "dragons.yml"));
		DvZ.classfile =  YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), "classes.yml"));
		DvZ.monsterfile =  YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), "monster.yml"));
		DvZ.itemfile = YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), "items.yml"));
	
		for(int id : DvZ.configfile.getIntegerList("disables_crafts")) {
			disabledCrafts.add(id);
		}
	}
	
	public static FileConfiguration getLanguage() {
		return langfile;
	}
	
	public static FileConfiguration getDragonsFile() {
		return dragonsfile;
	}
	
	public static FileConfiguration getClassFile() {
		return classfile;
	}
	
	public static FileConfiguration getMonsterFile() {
		return monsterfile;
	}
	
	public static FileConfiguration getItemFile() {
		return itemfile;
	}
	
	public static FileConfiguration getStaticConfig() {
		return configfile;
	}
	
	public void saveWorld(CommandSender sender, String name) {
		Date dt = new Date();
		SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd_HH-mm-ss" );
		String timer = df.format( dt );
		
		File world = new File(Bukkit.getServer().getWorldContainer().getPath()+"/"+name+"/");
		File cworld = new File(Bukkit.getServer().getWorldContainer().getPath()+"/"+name+"_sDvZ_"+timer+"/");
		boolean failed = false;
		try {
			FileHandler.copyFolder(world, cworld);
		} catch (IOException e) {
			sender.sendMessage(getLanguage().getString("string_save_fail","Could not save the World!"));
			failed = true;
		}
		if(!failed) {
			sender.sendMessage(getLanguage().getString("string_save_succes","Saved a Copy of the World!"));
		}
	}
	
	public void createWorld(CommandSender sender, String name) {
		File world = new File(Bukkit.getServer().getWorldContainer().getPath()+"/"+name+"/");
		
		String free = getFreeWorld();
		
		File cworld = new File(Bukkit.getServer().getWorldContainer().getPath()+"/"+this.getConfig().getString("world_prefix", "DvZ_")+"Worlds/"+free+"/");
		boolean failed = false;
		try {
			FileHandler.copyFolder(world, cworld);
			File uidf = new File(cworld, "uid.dat");
			if(uidf.exists()) uidf.delete();
		} catch (IOException e) {
			sender.sendMessage(getLanguage().getString("string_save_fail","Could not save the World!"));
			failed = true;
		}
		if(!failed) {
			sender.sendMessage(getLanguage().getString("string_save_succes","Saved a Copy of the World!"));
		}
		
		maxWorld = getFreeWorld();
	}
	
	private String getFreeWorld() {
		int akt = 0;
		
		File f;
		do {
			akt++;
			f = new File(Bukkit.getServer().getWorldContainer().getPath()+"/"+this.getConfig().getString("world_prefix", "DvZ_")+"Worlds/"+akt+"/");
		} while(f.exists());
		
		return ""+akt;
	}
	
	public void resetMainWorld(final int id) {
		final World w = Bukkit.getServer().getWorld(getConfig().getString("world_prefix", "DvZ_")+"Main"+id+"");
		if(w!=null) {
			final String wname = w.getName();
			w.setAutoSave(false);
			
			for ( Player player : w.getPlayers() ) {
				ItemHandler.clearInv(player);
				if(DvZ.getStaticConfig().getString("use_lobby", "true").equals("true"))
					player.teleport(Bukkit.getServer().getWorld(getConfig().getString("world_prefix", "DvZ_")+"Lobby").getSpawnLocation());
				else
					player.teleport(Bukkit.getServer().getWorlds().get(0).getSpawnLocation());
				player.sendMessage(getLanguage().getString("string_tp_reset", "World is resetting - You have been teleported to the Lobby"));
			}
		
			Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
				public void run() {
					if(w!=null) {
						File wf = new File(Bukkit.getServer().getWorldContainer().getPath()+"/"+getConfig().getString("world_prefix", "DvZ_")+"Main"+id+"/");
						
						DynamicClassFunctions.bindRegionFiles();
						DynamicClassFunctions.forceUnloadWorld(w);
						DynamicClassFunctions.clearWorldReference(wname);
						
						FileHandler.deleteFolder(wf);
						
					}
				}
			}, 40);
		}
	}
	
	//TODO - Neue Main Welt generieren
	private Random mapRandom = new Random();
	private String maxWorld = "";
	public void newMainWorld(final int id) {
		if(maxWorld.equals("")) {
			maxWorld = getFreeWorld();
		}
		
		int max = Integer.parseInt(maxWorld)-1;
		
		if(max>0) {
			int pos = mapRandom.nextInt(max)+1;
			
			File worldfile = new File(Bukkit.getServer().getWorldContainer().getPath()+"/"+this.getConfig().getString("world_prefix", "DvZ_")+"Worlds/"+pos+"/");
			File mainfile = new File(Bukkit.getServer().getWorldContainer().getPath()+"/"+this.getConfig().getString("world_prefix", "DvZ_")+"Main"+id+"/");
			try {
				FileHandler.copyFolder(worldfile, mainfile);
			} catch (IOException e) {
				e.printStackTrace();
			}
			games[id].loadGameInfo();
			Bukkit.getServer().createWorld(new WorldCreator(DvZ.getStaticConfig().getString("world_prefix", "DvZ_")+"Main"+id+""));
		} else {
			games[id].broadcastMessage("No saved DvZ world found! Cannot start the Game! Autogenerating a DvZ world will come in Version 1.6!");
			//Bukkit.getServer().createWorld(new WorldCreator(this.getConfig().getString("world_prefix", "DvZ_")+"Main"));
		}
	}
	
	//TODO - remove temporary workaround
	@SuppressWarnings("deprecation")
	public static void updateInventory(Player player) {
		player.updateInventory();
	}
	
	//#######################################
	//Bekomme Spiel in dem das der Spieler ist
	//#######################################
	public Game getPlayerGame(String player) {
		for(int i=0; i<games.length; i++) {
			if (games[i]!=null) {
				if (games[i].isPlayer(player))
				{
					return games[i];
				}
			}
		}
		
		return null;
	}
	
	public int getGameID(Game game) {
		for(int i=0; i<games.length; i++) {
			if (games[i]==game) {
				return i;
			}
		}
		
		return -1;
	}
	
	public Game getGame(int id) {
		return games[id];
	}

	public GameDummy getDummy() {
		return gameDummy;
	}
	
	public Lobby getLobby() {
		return lobby;
	}
	
	public int getRunningGameCount() {
		int count = 0;
		
		for(int i=0; i<games.length; i++) {
			if(games[i]!=null)
			if(games[i].isRunning()) {
				count++;
			}
		}
		
		return count;
	}
	
    public void joinGame(Player player, Game game) {
    	joinGame(player, game, false);
	}
	
	public void joinGame(Player player, Game game, boolean autojoin) {
		game.setPlayerState(player.getName(), 1);
		if(DvZ.getStaticConfig().getString("use_lobby", "true").equals("true"))
			player.teleport(Bukkit.getServer().getWorld(getConfig().getString("world_prefix", "DvZ_")+"Lobby").getSpawnLocation());
		ItemHandler.clearInv(player);

		player.sendMessage(getLanguage().getString("string_self_added","You have been added to the game!"));

		//autoadd player
		if(game.getState()>1) {
			if (getConfig().getString("autoadd_players","false")=="true" || autojoin) {
				if(!game.released) {
					game.setPlayerState(player.getName(), 2);
					player.getInventory().clear();
					player.sendMessage(getLanguage().getString("string_choose","Choose your class!"));
					game.addDwarfItems(player);

					game.broadcastMessage(getLanguage().getString("string_autoadd","Autoadded -0- as a Dwarf to the Game!").replaceAll("-0-", player.getDisplayName()));
				} else {
					game.setPlayerState(player.getName(), 3);
					player.getInventory().clear();
					player.sendMessage(getLanguage().getString("string_choose","Choose your class!"));
					game.addMonsterItems(player);

					game.broadcastMessage(getLanguage().getString("string_autoadd_m","Autoadded -0- as a Monster to the Game!").replaceAll("-0-", player.getDisplayName()));
				}
			}
		}
	}
	
	public static void disguiseP(Player player, Disguise disguise) {
		if(api.isDisguised(player)) {
			api.changePlayerDisguise(player, disguise);
		} else {
			api.disguisePlayer(player, disguise);
		}
	}
	
	public boolean isCraftDisabled(int id) {
		return disabledCrafts.contains(id);
	}
	
	public static boolean isPathable(Block block) {
        return isPathable(block.getType());
	}
	public static boolean isPathable(Material material) {
		return
				material == Material.AIR ||
				material == Material.SAPLING ||
				material == Material.WATER ||
				material == Material.STATIONARY_WATER ||
				material == Material.POWERED_RAIL ||
				material == Material.DETECTOR_RAIL ||
				material == Material.LONG_GRASS ||
				material == Material.DEAD_BUSH ||
				material == Material.YELLOW_FLOWER ||
				material == Material.RED_ROSE ||
				material == Material.BROWN_MUSHROOM ||
				material == Material.RED_MUSHROOM ||
				material == Material.TORCH ||
				material == Material.FIRE ||
				material == Material.REDSTONE_WIRE ||
				material == Material.CROPS ||
				material == Material.SIGN_POST ||
				material == Material.LADDER ||
				material == Material.RAILS ||
				material == Material.WALL_SIGN ||
				material == Material.LEVER ||
				material == Material.STONE_PLATE ||
				material == Material.WOOD_PLATE ||
				material == Material.REDSTONE_TORCH_OFF ||
				material == Material.REDSTONE_TORCH_ON ||
				material == Material.STONE_BUTTON ||
				material == Material.SNOW ||
				material == Material.SUGAR_CANE_BLOCK ||
				material == Material.VINE ||
				material == Material.WATER_LILY ||
				material == Material.NETHER_STALK ||
				material == Material.TRIPWIRE_HOOK ||
				material == Material.TRIPWIRE ||
				material == Material.POTATO ||
				material == Material.CARROT ||
				material == Material.WOOD_BUTTON;
	}
	public final static HashSet<Byte> transparent = new HashSet<Byte>();
	static {
		transparent.add((byte)0);
		transparent.add((byte)6);
		transparent.add((byte)8);
		transparent.add((byte)9);
		transparent.add((byte)27);
		transparent.add((byte)28);
		transparent.add((byte)31);
		transparent.add((byte)32);
		transparent.add((byte)37);
		transparent.add((byte)38);
		transparent.add((byte)39);
		transparent.add((byte)40);
		transparent.add((byte)50);
		transparent.add((byte)51);
		transparent.add((byte)55);
		transparent.add((byte)59);
		transparent.add((byte)63);
		transparent.add((byte)65);
		transparent.add((byte)66);
		transparent.add((byte)68);
		transparent.add((byte)69);
		transparent.add((byte)70);
		transparent.add((byte)71);
		transparent.add((byte)75);
		transparent.add((byte)76);
		transparent.add((byte)77);
		transparent.add((byte)78);
		transparent.add((byte)83);
		transparent.add((byte)106);
		transparent.add((byte)111);
		transparent.add((byte)115);
		transparent.add((byte)131);
		transparent.add((byte)132);
		transparent.add((byte)141);
		transparent.add((byte)142);
		transparent.add((byte)143);
	}
	
	public static int scheduleRepeatingTask(final Runnable task, int delay, int interval) {
		return Bukkit.getScheduler().scheduleSyncRepeatingTask(DvZ.instance, task, delay, interval);
	}

	public static void cancelTask(int taskId) {
		Bukkit.getScheduler().cancelTask(taskId);
	}
}
