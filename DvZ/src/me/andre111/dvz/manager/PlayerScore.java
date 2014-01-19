package me.andre111.dvz.manager;

import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;

public class PlayerScore {
	private int points;

	public void save(YamlConfiguration file, String player) {
		file.set(player, "");
		file.set(player+".points", points);
	}
	public static PlayerScore load(String name, MemorySection section) {
		PlayerScore pscore = new PlayerScore();
			pscore.setPoints(section.getInt("points"));
		return pscore;
	}
	
	public int getPoints() {
		return points;
	}
	public void setPoints(int points) {
		this.points = points;
	}
}
