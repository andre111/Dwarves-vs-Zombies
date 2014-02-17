package me.andre111.items.item.spell;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.Game;
import me.andre111.items.SpellItems;
import me.andre111.items.item.ItemSpell;
import me.andre111.items.item.SpellVariable;

public class ItemClassCheck extends ItemSpell {
	/*private String playername = "";
	private String type = "dwarf";
	int classid = 0;
	
	@Override
	public void setCastVar(int id, double var) {
		if(id==2) classid = (int) Math.round(var);
	}
	
	@Override
	public void setCastVar(int id, String var) {
		if(id==0) playername = var;
		else if(id==1) type = var;
	}
	
	@Override
	public void setCastVar(int id, SpellVariable var) {
		if(id==0) playername = var.getAsString();
		else if(id==1) type = var.getAsString();
		else if(id==2) classid = var.getAsInt();
	}
	
	@Override
	public boolean cast(Player player, Location loc, Player target, Block block) {
		Player pTarget = Bukkit.getPlayerExact(playername);
		if(playername.equals("")) {
			pTarget = player;
		}
		
		if(pTarget!=null) {
			return checkClass(pTarget);
		}
		
		return false;
	}
	
	//Check for playerclass
	private boolean checkClass(Player player) {
		Game game = DvZ.instance.getPlayerGame(player.getName());
		if(game==null) return false;
		
		//dwarves
		if(type.equals("dwarf") || type.equals("dwarves")) {
			if(game.isDwarf(player.getName(), true)) {
				int dId = game.getPlayerState(player.getName())-Game.dwarfMin;
				
				if(dId==classid) return true;
			}
		}
		//monsters
		if(type.equals("monster") || type.equals("monsters")) {
			if(game.isMonster(player.getName())) {
				int mId = game.getPlayerState(player.getName())-Game.monsterMin;
				
				if(mId==classid) return true;
			}
		}
		
		return false;
	}*/
	
	@Override
	public Varargs invoke(Varargs args) {
		if(args.narg()>=3) {
			LuaValue playerN = args.arg(1);
			LuaValue typeN = args.arg(2);
			LuaValue classidN = args.arg(3);
			
			if(playerN.isstring() && typeN.isstring() && classidN.isnumber()) {
				Player player = Bukkit.getPlayerExact(playerN.toString());
				String type = typeN.toString();
				int classid = classidN.toint();
				
				if(player!=null) {
					Game game = DvZ.instance.getPlayerGame(player.getName());
					if(game==null) return RETURN_FALSE;
					
					//dwarves
					if(type.equals("dwarf") || type.equals("dwarves")) {
						if(game.isDwarf(player.getName(), true)) {
							int dId = game.getPlayerState(player.getName())-Game.dwarfMin;
							
							if(dId==classid) return RETURN_TRUE;
						}
					}
					//monsters
					if(type.equals("monster") || type.equals("monsters")) {
						if(game.isMonster(player.getName())) {
							int mId = game.getPlayerState(player.getName())-Game.monsterMin;
							
							if(mId==classid) return RETURN_TRUE;
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
