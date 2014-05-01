package me.andre111.dvz.teams;

import java.util.ArrayList;
import java.util.Random;

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
			team.setDisplayName(teamSec.getString(st+".name", ""));
			team.setRespawnTeam(teamSec.getString(st+".respawn", st));
			team.setReleased(!teamSec.getBoolean(st+".needRelease", false));
			team.setHasMonument(teamSec.getBoolean(st+".hasMonument", false));
			team.setMonumentName(teamSec.getString(st+".monumentName", ""));
			team.setMonumentBarTeam(teamSec.getString(st+".monumentBar", ""));
			for(Integer classes : teamSec.getIntegerList(st+".classes")) {
				team.addClass(classes);
			}
			for(String friendly : teamSec.getStringList(st+".relation.friendly")) {
				team.addFriendly(friendly);
			}
			for(String hostile : teamSec.getStringList(st+".relation.hostile")) {
				team.addEnemy(hostile);
			}
			for(String commands : teamSec.getStringList(st+".onDeath")) {
				team.addDeathCommand(commands);
			}
			team.setFriendlyFire(teamSec.getBoolean(st+".friendlyFire", false));
			team.setInvulnerable(teamSec.getBoolean(st+".invulnerable", false));
			team.setSpawnBuff(teamSec.getInt(st+".spawnBuff", 0));
			team.setCanPickupItems(teamSec.getBoolean(st+".items.pickup", true));
			team.setCanDropItems(teamSec.getBoolean(st+".items.drop", true));
			team.setDeathDropItems(teamSec.getBoolean(st+".items.deathDrop", true));
			team.setHideKills(teamSec.getBoolean(st+".hideKills", false));
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
			timer.setMaxTime(timerSec.getInt(st+".time", 20*60*1));
			timer.setShowDisplay(timerSec.getBoolean(st+".showDisplay", true));
			timer.setDisplay(timerSec.getString(st+".display", ""));
			for(String command : timerSec.getStringList(st+".onEnd")) {
				timer.addCommand(command);
			}
			
			counters.add(timer);
		}
		
		//start
		for(String st : config.getStringList("gamestart.startteams")) {
			startTeams.add(st);
		}
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
					team.setSpawnLocation(loc);
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
		for(String command : commands) {
			performCommand(command);
		}
	}
	public void performCommand(String command) {
		String[] split = command.split(" ");
		
		//TODO - add commands
		if(split[0].equals("win")) {
			Team team = getTeam(split[1]);
			if(team!=null) {
				DvZ.instance.getGame(gameID).win(team);
			}
		} else if(split[0].equals("lose")) {
			
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
	
	public String getStartTeam() {
		if(startTeams.isEmpty()) {
			return NO_TEAM;
		}
		
		int pos = rand.nextInt(startTeams.size());
		return startTeams.get(pos);
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
