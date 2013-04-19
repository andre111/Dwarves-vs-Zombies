package me.andre111.dvz.dragon;

import org.bukkit.entity.Entity;

public interface Dragon {

	public Entity getEntity();
	
	public int getMana();
	public void setMana(int mana);
	
	public int getID();
	public void setID(int id);
	
	public void init();
}
