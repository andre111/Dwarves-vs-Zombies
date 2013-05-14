package me.andre111.dvz.item;

import org.bukkit.Location;

public class ItemEffect {
	private String location;
	
	//the location when and where to play the effect
	public void setLocation(String loc) {
		location = loc;
	}
	public String getLocation() {
		return location;
	}
	
	//set arguments and other stuff
	public void setVars(String vars) {
	}
	
	//play the effect at the given location
	public void play(Location loc) {
	}
}
