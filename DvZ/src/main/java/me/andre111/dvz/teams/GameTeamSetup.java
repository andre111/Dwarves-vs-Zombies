package me.andre111.dvz.teams;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.config.DVZFileConfiguration;
import me.andre111.dvz.manager.EffectManager;

public class GameTeamSetup {
	public static final String NO_TEAM = "-1_no_team_found!";
	
	private int gameID;
	private Random rand;
	
	private ArrayList<Team> teams = new ArrayList<Team>();
	private ArrayList<GameTimer> counters = new ArrayList<GameTimer>();
	
	private ArrayList<String> startTeams = new ArrayList<String>();
	private boolean evenTeamSplit;
	
	public GameTeamSetup(int gid) {
		gameID = gid;
		rand = new Random();
	}
	
	public void loadSetup(DVZFileConfiguration config) {
		teams.clear();
		counters.clear();
		startTeams.clear();
		
		//load setup
		ConfigurationSection teamSec = config.getConfigurationSection("teams");
		for(String st : teamSec.getKeys(false)) {
			Team team = new Team();
			team.setName(st);
			team.setDisplayName(config.getString("teams."+st+".name", ""));
			team.setRespawnTeam(config.getString("teams."+st+".respawn", st));
			team.setReleased(!config.getBoolean("teams."+st+".needRelease", false));
			team.setHasMonument(config.getBoolean("teams."+st+".hasMonument", false));
			team.setMonumentName(config.getString("teams."+st+".monumentName", ""));
			team.setMonumentBarTeam(config.getString("teams."+st+".monumentBar", ""));
			for(String classes : config.getStringList("teams."+st+".classes")) {
				team.addClass(classes);
			}
			for(String friendly : config.getStringList("teams."+st+".relation.friendly")) {
				team.addFriendly(friendly);
			}
			for(String hostile : config.getStringList("teams."+st+".relation.hostile")) {
				team.addEnemy(hostile);
			}
			for(String commands : config.getStringList("teams."+st+".onDeath")) {
				team.addDeathCommand(commands);
			}
			for(String commands : config.getStringList("teams."+st+".onMonumentDestroy")) {
				team.addMonumentDestroyCommand(commands);
			}
			team.setFriendlyFire(config.getBoolean("teams."+st+".friendlyFire", false));
			team.setInvulnerable(config.getBoolean("teams."+st+".invulnerable", false));
			team.setSpawnBuff(config.getInt("teams."+st+".spawnBuff", 0));
			for(String items : config.getStringList("teams."+st+".spawnBuffItems")) {
				team.addSpawnBuffItem(items);
			}
			team.setCanPickupItems(config.getBoolean("teams."+st+".items.pickup", true));
			team.setCanDropItems(config.getBoolean("teams."+st+".items.drop", true));
			team.setDeathDropItems(config.getBoolean("teams."+st+".items.deathDrop", true));
			team.setHideKills(config.getBoolean("teams."+st+".hideKills", false));
			team.setSelectInLobby(config.getBoolean("teams."+st+".selectInLobby", true));
			EffectManager effects = new EffectManager(team);
			effects.loadEffects(teamSec, st);
			team.setEffectManager(effects);
			
			teams.add(team);
		}
		
		//timers
		ConfigurationSection timerSec = config.getConfigurationSection("timer");
		for(String st : timerSec.getKeys(false)) {
			GameTimer timer = new GameTimer(this);
			timer.setName(st);
			timer.setMaxTime(config.getInt("timer."+st+".time", 20*60*1));
			timer.setShowDisplay(config.getBoolean("timer."+st+".showDisplay", true));
			timer.setDisplay(config.getString("timer."+st+".display", ""));
			for(String command : config.getStringList("timer."+st+".onEnd")) {
				timer.addCommand(command);
			}
			
			counters.add(timer);
		}
		
		//start
		for(String st : config.getStringList("gamestart.startteams")) {
			startTeams.add(st);
		}
		evenTeamSplit = config.getBoolean("gamestart.eventeamsplit", true);
		for(String st : config.getStringList("gamestart.starttimers")) {
			GameTimer timer = getTimer(st);
			if(timer!=null) {
				timer.start();
			}
		}
	}
	
	public void loadLocations(DVZFileConfiguration config, World world) {
		for(Team team : teams) {
			if(config.contains(team.getName()+".spawn")) {
				team.setSpawnLocation(loadLocation(config.getConfigurationSection(team.getName()+".spawn"), world));
			}
			if(team.isHasMonument()) {
				if(config.contains(team.getName()+".monument")) {
					Location loc = loadLocation(config.getConfigurationSection(team.getName()+".monument"), world);
					team.setMonumentLocation(loc);
					DvZ.instance.getGame(gameID).createMonument(loc, false);
				}
			}
		}
	}
	
	public Location loadLocation(ConfigurationSection section, World world) {
		double x = section.getDouble("x", 0);
		double y = section.getDouble("y", 0);
		double z = section.getDouble("z", 0);
		
		float yaw = (float) section.getDouble("yaw", 0f);
		float pitch = (float) section.getDouble("pitch", 0f);
		
		return new Location(world, x, y, z, yaw, pitch);
	}
	
	public void tick() {
		for(GameTimer timer : counters) {
			timer.tick();
		}
		for(Team team : teams) {
			team.tick(this, DvZ.instance.getGame(gameID));
		}
	}
	
	public void performCommands(ArrayList<String> commands) {
		ArrayList<Team> winTeams = new ArrayList<Team>();
		ArrayList<Team> loseTeams = new ArrayList<Team>();
		for(String command : commands) {
			//multi win/lose
			if(command.startsWith("win ")) {
				String[] split = command.split(" ");
				Team team = getTeam(split[1]);
				if(team!=null) {
					winTeams.add(team);
				}
			} else if(command.startsWith("lose ")) {
				String[] split = command.split(" ");
				Team team = getTeam(split[1]);
				if(team!=null) {
					loseTeams.add(team);
				}
			//Normal commands
			} else {
				performCommand(command);
			}
		}
		
		//multi win/lose
		if(!winTeams.isEmpty() || !loseTeams.isEmpty()) {
			DvZ.instance.getGame(gameID).multiWinLose(winTeams, loseTeams);
		}
	}
	public void performCommand(String command) {
		String[] split = command.split(" ");
		
		if(split[0].equals("win")) {
			Team team = getTeam(split[1]);
			if(team!=null) {
				DvZ.instance.getGame(gameID).win(team);
			}
		} else if(split[0].equals("lose")) {
			Team team = getTeam(split[1]);
			if(team!=null) {
				DvZ.instance.getGame(gameID).lose(team);
			}
		} else if(split[0].equals("release")) {
			Team team = getTeam(split[1]);
			if(team!=null) {
				team.setReleased(true);
				DvZ.instance.getGame(gameID).release(team);
			}
		} else if(split[0].equals("starttimer")) {
			GameTimer timer = getTimer(split[1]);
			if(timer!=null) {
				timer.start();
			}
		} else if(split[0].equals("setstartteam")) {
			startTeams.clear();
			startTeams.add(split[1]);
		} else if(split[0].equals("addstartteam")) {
			startTeams.add(split[1]);
		} else if(split[0].equals("removestartteam")) {
			startTeams.remove(split[1]);
		} else if(split[0].equals("set")) {
			Team team = getTeam(split[1]);
			if(team!=null) {
				if(split[2].equals("invulnerable")) {
					team.setInvulnerable(Boolean.parseBoolean(split[3]));
				}
			}
		} else if(split[0].equals("assassins")) {
			Team team = getTeam(split[1]);
			if(team!=null) {
				int percentage = Integer.parseInt(split[2]);
				DvZ.instance.getGame(gameID).addAssasins(team, percentage);
			}
		} else if(split[0].equals("command")) {
			if(split.length>1) {
				StringBuilder stbuilder = new StringBuilder();
				stbuilder.append(split[1]);
				for(int i=2; i<split.length; i++) {
					stbuilder.append(" ");
					stbuilder.append(split[i]);
				}
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), stbuilder.toString());
			}
		}
	}
	
	public boolean isTimerDisplayed() {
		for(GameTimer counter : counters) {
			if(counter.isShowDisplay() && counter.isStarted() && counter.getTime()>0) {
				return true;
			}
		}
		
		return false;
	}
	public String getTimerDisplay() {
		for(GameTimer counter : counters) {
			if(counter.isShowDisplay() && counter.isStarted() && counter.getTime()>0) {
				return counter.getDisplay();
			}
		}
		
		return "";
	}
	public int getTimerDisplayVar() {
		for(GameTimer counter : counters) {
			if(counter.isShowDisplay() && counter.isStarted() && counter.getTime()>0) {
				return counter.getTime();
			}
		}
		
		return 0;
	}
	public String getDisplayedTimer() {
		for(GameTimer counter : counters) {
			if(counter.isShowDisplay() && counter.isStarted() && counter.getTime()>0) {
				return counter.getName();
			}
		}
		
		return "";
	}
	
	public String getStartTeam() {
		if(startTeams.isEmpty()) {
			return NO_TEAM;
		}
		
		if(evenTeamSplit) {
			int lowest = 0;
			int lowestVar = 100000000;
			for(int i=0; i<startTeams.size(); i++) {
				int count = DvZ.instance.getGame(gameID).getTeamPlayers(getTeam(startTeams.get(i))).size();
				if(count<lowestVar) {
					lowest = i;
					lowestVar = count;
				}
			}
			return startTeams.get(lowest);
		} else {
			int pos = rand.nextInt(startTeams.size());
			return startTeams.get(pos);
		}
	}
	
	public ArrayList<Team> getTeams() {
		return teams;
	}
	public Team getTeam(String name) {
		for(Team team : teams) {
			if(team.getName().equals(name)) {
				return team;
			}
		}
		return null;
	}
	public GameTimer getTimer(String name) {
		for(GameTimer counter : counters) {
			if(counter.getName().equals(name)) {
				return counter;
			}
		}
		return null;
	}
}
