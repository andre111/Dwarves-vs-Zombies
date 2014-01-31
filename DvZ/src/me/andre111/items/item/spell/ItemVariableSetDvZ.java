package me.andre111.items.item.spell;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.Game;
import me.andre111.items.item.ItemSpell;
import me.andre111.items.item.SpellVariable;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class ItemVariableSetDvZ extends ItemSpell {
	private int variable = 0;
	private String value = "";
	
	@Override
	public void setCastVar(int id, double var) {
		if(id==0) variable = (int) Math.round(var);
	}
	
	@Override
	public void setCastVar(int id, String var) {
		if(id==1) value = var;
	}
	
	@Override
	public void setCastVar(int id, SpellVariable var) {
		if(id==0) variable = var.getAsInt();
		else if(id==1) value = var.getAsString();
	}
	
	@Override
	public boolean cast(Player player, Location loc, Player target, Block block) {
		//Locations
		if(value.equalsIgnoreCase("monument")) {
			if(player!=null && DvZ.instance.getPlayerGame(player.getName())!=null) {
				Game game = DvZ.instance.getPlayerGame(player.getName());
				
				if(game.monumentexists) {
					getVariables().put(variable, new SpellVariable(SpellVariable.LOCATION, game.monument));
					return true;
				}
			}
		}
		
		return false;
	}
}
