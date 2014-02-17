package me.andre111.items.item.spell;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.Game;
import me.andre111.items.SpellItems;
import me.andre111.items.item.ItemSpell;
import me.andre111.items.item.SpellVariable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

public class ItemVariableSetDvZ extends ItemSpell {
	/*private int variable = 0;
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
	}*/
	
	@Override
	public Varargs invoke(Varargs args) {
		if(args.narg()>=2) {
			LuaValue playerN = args.arg(1);
			LuaValue valueN = args.arg(2);
			
			if(playerN.isstring() && valueN.isstring()) {
				Player player = Bukkit.getPlayerExact(playerN.toString());
				String value = valueN.toString();
				
				LuaValue[] returnValue = new LuaValue[2];
				returnValue[0] = LuaValue.TRUE;
				
				//Locations
				if(value.equalsIgnoreCase("monument")) {
					if(player!=null && DvZ.instance.getPlayerGame(player.getName())!=null) {
						Game game = DvZ.instance.getPlayerGame(player.getName());
						
						if(game.monumentexists) {
							returnValue[1] = LuaValue.userdataOf(game.monument);
							return LuaValue.varargsOf(returnValue);
						}
					}
				}
			}
		} else {
			SpellItems.log("Missing Argument for "+getClass().getCanonicalName());
		}
		
		return RETURN_FALSE;
	}
}
