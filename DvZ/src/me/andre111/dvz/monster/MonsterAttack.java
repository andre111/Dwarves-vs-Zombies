package me.andre111.dvz.monster;

import me.andre111.dvz.Game;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class MonsterAttack {
	private int id;

	public void setCastVar(int id, double var) {
	}
	public void setCastVar(int id, String var) {
	}
	
	//TODO - implement cast methods
	//###################################
	public void spellCast(Game game, Player player) {	
	}
	public void spellCast(Game game, Player player, Block target) {	
		spellCast(game, player);
	}
	public void spellCast(Game game, Player player, Player target) {
		spellCast(game, player);
	}
	public void spellCastFarTargeted(Game game, Player player, Block target) {	
	}
	public void spellCastOnLocation(Game game, Player player, Location target) {
	}
	//###################################
	
	//Type of Attack:
	//0: Simple Cast
	//1: Needs Target Block
	//2: Needs Target Player
	//3: Needs far away target Block
	public int getType() {
		return 0;
	}
	
	//for attack type 3
	public int getRange() {
		return 10;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
}
