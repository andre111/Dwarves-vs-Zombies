package me.andre111.dvz.item;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.Game;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class ItemSpell {
	
	public void setCastVar(int id, double var) {
	}
	public void setCastVar(int id, String var) {
	}
	
	public boolean cast(Game game, Player player, boolean[] states) {
		//required other attacks to succed
		if(require!=-1) {
			if(!states[require]) return false;
		}
		
		return cast(game, player);
	}
	public boolean cast(Game game, Player player, Block block, boolean[] states) {
		//required other attacks to succed
		if(require!=-1) {
			if(!states[require]) return false;
		}

		return cast(game, player, block);
	}
	public boolean cast(Game game, Player player, Player target, boolean[] states) {
		//required other attacks to succed
		if(require!=-1) {
			if(!states[require]) return false;
		}

		return cast(game, player, target);
	}
	
	public boolean cast(Game game, Player player) {
		return false;
	}
	public boolean cast(Game game, Player player, Block block) {
		return false;
	}
	public boolean cast(Game game, Player player, Player target) {
		return false;
	}
	//casted by another spell on that location
	public boolean cast(Game game, Player player, Location loc) {
		return false;
	}
	
	//Type of Attack:
	//0: Simple Cast
	//1: Needs Target Block
	//2: Needs Target Player
	/*public int getType() {
		return 0;
	}*/
	
	
	private String itemName = "";
	private boolean left = false;
	private int require = -1;
	
	public void setItemName(String name) {
		itemName = name;
	}
	public String getItemName() {
		return itemName;
	}
	public boolean isLeft() {
		return left;
	}
	public void setLeft(boolean left) {
		this.left = left;
	}
	public void setRequire(int r) {
		require = r;
	}
	public int getRequire() {
		return require;
	}
	
	public CustomItem getItem() {
		return DvZ.itemManager.getItemByName(getItemName());
	}
	public void resetCoolDown(Game game, Player player) {
		getItem().resetCoolDown(game, isLeft(), player);
	}
}
