package me.andre111.dvz.manager;

import java.util.UUID;

import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;

public class PlayerScore {
	//TODO - old pointsystem, maybe remove for the new one
	private int points;
	
	private int kills;
	private int deaths;
	private int victories;
	private int losses;
	
	private int classpoints;
	

	public void save(YamlConfiguration file, UUID player) {
		file.set(player+"", "");
		file.set(player+".points", points);
		file.set(player+".kills", kills);
		file.set(player+".deaths", deaths);
		file.set(player+".victories", victories);
		file.set(player+".losses", losses);
		file.set(player+".classpoints", classpoints);
	}
	public static PlayerScore load(UUID name, MemorySection section) {
		PlayerScore pscore = new PlayerScore();
			pscore.setPoints(section.getInt("points", 0));
			pscore.setKills(section.getInt("kills", 0));
			pscore.setDeaths(section.getInt("deaths", 0));
			pscore.setVictories(section.getInt("victories", 0));
			pscore.setLosses(section.getInt("losses", 0));
			pscore.setClasspoints(section.getInt("classpoints", 0));
		return pscore;
	}
	
	public int getCalculatedScore() {
		return kills+victories*20+classpoints*4;
	}
	
	public int getPoints() {
		return points;
	}
	public void setPoints(int points) {
		this.points = points;
	}
	public int getKills() {
		return kills;
	}
	public void setKills(int kills) {
		this.kills = kills;
	}
	public int getDeaths() {
		return deaths;
	}
	public void setDeaths(int deaths) {
		this.deaths = deaths;
	}
	public int getVictories() {
		return victories;
	}
	public void setVictories(int victories) {
		this.victories = victories;
	}
	public int getLosses() {
		return losses;
	}
	public void setLosses(int losses) {
		this.losses = losses;
	}
	public int getClasspoints() {
		return classpoints;
	}
	public void setClasspoints(int classpoints) {
		this.classpoints = classpoints;
	}
}
