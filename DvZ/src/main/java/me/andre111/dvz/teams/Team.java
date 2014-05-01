package me.andre111.dvz.teams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.Game;
import me.andre111.dvz.config.ConfigManager;
import me.andre111.dvz.manager.EffectManager;
import me.andre111.dvz.utils.PlayerHandler;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class Team {
	private String name;
	private String displayName;
	private String respawnTeam;
	private boolean released;
	private boolean hasMonument;
	private ArrayList<Integer> classes = new ArrayList<Integer>();
	private ArrayList<String> rel_enemy = new ArrayList<String>();
	private ArrayList<String> rel_friendly = new ArrayList<String>();
	private ArrayList<String> deathCommands = new ArrayList<String>();
	private boolean friendlyFire;
	private boolean canPickupItems;
	private boolean canDropItems;
	private boolean deathDropItems;
	private boolean invulnerable;
	private boolean hideKills;

	private Location spawnLocation;
	private Location monumentLocation;
	private int monumentHealth;
	private String monumentName;
	private String monumentBarTeam;
	
	//monument distance counting down to 0
	private HashMap<UUID, Integer> monDistance = new HashMap<UUID, Integer>();
	private int ticker = 0;
	
	private EffectManager effectManager;

	public boolean isEnemy(Team other) {
		for(String st : rel_enemy) {
			if(st.equals(other.getName())) {
				return true;
			}
		}

		return false;
	}
	public boolean isFriendly(Team other) {
		for(String st : rel_friendly) {
			if(st.equals(other.getName())) {
				return true;
			}
		}

		return false;
	}
	public void tick(GameTeamSetup setup, Game game) {
		ticker++;
		
		//Check players
		ArrayList<UUID> teamPlayers = game.getTeamPlayers(this);
		if(teamPlayers.isEmpty()) {
			setup.performCommands(deathCommands);
			return;
		}
		//TODO - maybe chonge back to every 2 seconds than every second?
		if(ticker>=20)
			effectManager.playerEffects(game);
		effectManager.killEffects(game);
		
		if(!hasMonument()) {
			return;
		}

		//monument destroyed
		boolean destr = false;
		int destroyed = 0;

		Block block = monumentLocation.getWorld().getBlockAt(monumentLocation);
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
		setMonumentHealth(100 - (int) Math.round((100/4)*destroyed));

		if(destr) {
			setup.performCommand("lose "+getName());
			return;
		}

		//monument distance
		if(ticker>=20) {
			ticker = 0;
			for(UUID playern : teamPlayers) {
				Player player = PlayerHandler.getPlayerFromUUID(playern);
	
				if(player!=null) {
					Location tempPLoc = player.getLocation().clone();
					tempPLoc.setY(monumentLocation.getY());
					if(tempPLoc.getWorld()==monumentLocation.getWorld() && tempPLoc.distanceSquared(monumentLocation)>ConfigManager.getStaticConfig().getInt("max_monument_distance", 200)*ConfigManager.getStaticConfig().getInt("max_monument_distance", 200)) {
						int current = ConfigManager.getStaticConfig().getInt("max_monument_counter", 10);
						if(monDistance.containsKey(playern)) {
							current = monDistance.get(playern) - 1;
						}
	
						if(current>0) {
							monDistance.put(playern, current);
							DvZ.sendPlayerMessageFormated(player, ConfigManager.getLanguage().getString("max_monument_warning", "&4WARNING: Get closer to the monument or you will loose points!"));
							DvZ.sendPlayerMessageFormated(player, ConfigManager.getLanguage().getString("max_monument_wtime", "&4Time remaining: -0- Seconds!").replace("-0-", ""+current));
						} else {
							player.teleport(getSpawnLocation(game.getWorld()));
	
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
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getRespawnTeam() {
		return respawnTeam;
	}
	public void setRespawnTeam(String respawnTeam) {
		this.respawnTeam = respawnTeam;
	}
	public boolean isReleased() {
		return released;
	}
	public void setReleased(boolean released) {
		this.released = released;
	}
	public boolean isHasMonument() {
		return hasMonument;
	}
	public void setHasMonument(boolean hasMonument) {
		this.hasMonument = hasMonument;
	}
	public boolean isCanPickupItems() {
		return canPickupItems;
	}
	public void setCanPickupItems(boolean canPickupItems) {
		this.canPickupItems = canPickupItems;
	}
	public boolean isCanDropItems() {
		return canDropItems;
	}
	public void setCanDropItems(boolean canDropItems) {
		this.canDropItems = canDropItems;
	}
	public boolean isDeathDropItems() {
		return deathDropItems;
	}
	public void setDeathDropItems(boolean deathDropItems) {
		this.deathDropItems = deathDropItems;
	}
	public ArrayList<Integer> getClasses() {
		return classes;
	}
	public void addClass(Integer classA) {
		classes.add(classA);
	}
	public ArrayList<String> getEnemy() {
		return rel_enemy;
	}
	public void addEnemy(String enemy) {
		rel_enemy.add(enemy);
	}
	public ArrayList<String> getFriendly() {
		return rel_friendly;
	}
	public void addFriendly(String friendly) {
		rel_friendly.add(friendly);
	}
	public ArrayList<String> getDeathCommands() {
		return deathCommands;
	}
	public void addDeathCommand(String deathCommand) {
		deathCommands.add(deathCommand);
	}
	public boolean isFriendlyFire() {
		return friendlyFire;
	}
	public void setFriendlyFire(boolean friendlyFire) {
		this.friendlyFire = friendlyFire;
	}
	public Location getSpawnLocation(World w) {
		return spawnLocation!=null ? spawnLocation : w.getSpawnLocation();
	}
	public void setSpawnLocation(Location spawnLocation) {
		this.spawnLocation = spawnLocation;
	}
	public Location getMonumentLocation() {
		return monumentLocation;
	}
	public void setMonumentLocation(Location monumentLocation) {
		this.monumentLocation = monumentLocation;
	}
	public boolean hasMonument() {
		if(hasMonument) {
			return monumentLocation!=null;
		}

		return false;
	}
	public int getMonumentHealth() {
		return monumentHealth;
	}
	public void setMonumentHealth(int monumentHealth) {
		this.monumentHealth = monumentHealth;
	}
	public boolean isInvulnerable() {
		return invulnerable;
	}
	public void setInvulnerable(boolean invulnerable) {
		this.invulnerable = invulnerable;
	}
	public String getMonumentName() {
		return monumentName;
	}
	public void setMonumentName(String monumentName) {
		this.monumentName = monumentName;
	}
	public String getMonumentBarTeam() {
		return monumentBarTeam;
	}
	public void setMonumentBarTeam(String monumentBarTeam) {
		this.monumentBarTeam = monumentBarTeam;
	}
	public EffectManager getEffectManager() {
		return effectManager;
	}
	public void setEffectManager(EffectManager effectManager) {
		this.effectManager = effectManager;
	}
	public boolean isHideKills() {
		return hideKills;
	}
	public void setHideKills(boolean hideKills) {
		this.hideKills = hideKills;
	}
}
