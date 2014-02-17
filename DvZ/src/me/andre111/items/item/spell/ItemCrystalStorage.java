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

public class ItemCrystalStorage extends ItemSpell {
	/*private boolean global = false;
	
	@Override
	public void setCastVar(int id, double var) {
		if(id==0) global = (var==1);
	}
	
	@Override
	public void setCastVar(int id, SpellVariable var) {
		if(id==0) global = var.getAsIntBoolean();
	}
	
	@Override
	public boolean cast(Player player, Location loc, Player target, Block block) {
		if(player==null) return false;
		
		Game game = DvZ.instance.getPlayerGame(player.getName());
		if(game==null) return false;
		
		player.openInventory(game.getCrystalChest(player.getName(), global));
		return true;
	}*/
	
	@Override
	public Varargs invoke(Varargs args) {
		if(args.narg()>=2) {
			LuaValue playerN = args.arg(1);
			LuaValue globalN = args.arg(2);
			
			if(playerN.isstring() && globalN.isboolean()) {
				Player player = Bukkit.getPlayerExact(playerN.toString());
				boolean global = globalN.toboolean();
				
				if(player!=null) {
					Game game = DvZ.instance.getPlayerGame(player.getName());
					if(game==null) return RETURN_FALSE;
					
					player.openInventory(game.getCrystalChest(player.getName(), global));
					
					return RETURN_TRUE;
				}
			}
		} else {
			SpellItems.log("Missing Argument for "+getClass().getCanonicalName());
		}
		
		return RETURN_FALSE;
	}
}
