package me.andre111.dvz.disguise;

public enum SupportedDisguises {
	NOONE("Noone"),
	DISGUISECRAFT("DisguiseCraft"),
	//BETHEMOB("BeTheMob"),
	LIBSDISGUISES("Lib's Disguises");
	
	private String name;
	private SupportedDisguises(String n) {
		name = n;
	}
	
	public String getName() {
		return name;
	}
}
