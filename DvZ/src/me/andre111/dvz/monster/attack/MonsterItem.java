package me.andre111.dvz.monster.attack;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.Game;
import me.andre111.dvz.monster.MonsterAttack;
import me.andre111.dvz.utils.ItemHandler;

public class MonsterItem extends MonsterAttack {
	private String item = "";
	private int times = 20;
	
	@Override
	public void setCastVar(int id, double var) {
		if(id==1) times = (int) Math.round(var);
	}
	@Override
	public void setCastVar(int id, String var) {
		if(id==0) item = var;
	}
	
	@Override
	public void spellCast(Game game, Player player) {	
		PlayerInventory inv = player.getInventory();
		for(int i=0; i<times; i++) {
			ItemStack it = ItemHandler.decodeItem(item);
			if(it!=null)
				inv.addItem(it);
		}
		
		DvZ.updateInventory(player);
	}
	
	@Override
	public int getType() {
		return 0;
	}
}
