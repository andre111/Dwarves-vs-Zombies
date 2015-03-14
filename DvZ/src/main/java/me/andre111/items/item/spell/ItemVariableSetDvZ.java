package me.andre111.items.item.spell;

import java.util.UUID;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.Game;
import me.andre111.dvz.utils.PlayerHandler;
import me.andre111.items.SpellItems;
import me.andre111.items.item.ItemSpell;
import me.andre111.items.lua.LUAHelper;

import org.bukkit.entity.Player;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

public class ItemVariableSetDvZ extends ItemSpell {
	//TODO - document teamvalue add(third argument)
	@Override
	public Varargs invoke(Varargs args) {
		if(args.narg()>=3) {
			LuaValue playerN = LUAHelper.getInternalValue(args.arg(1));
			LuaValue valueN = args.arg(2);
			LuaValue teamN = args.arg(3);
			
			if(playerN.isuserdata(UUID.class) && valueN.isstring() && teamN.isstring()) {
				Player player = PlayerHandler.getPlayerFromUUID((UUID) playerN.touserdata(UUID.class));
				String value = valueN.toString();
				String team = teamN.toString();
				
				LuaValue[] returnValue = new LuaValue[2];
				returnValue[0] = LuaValue.TRUE;
				
				//Locations
				if(value.equalsIgnoreCase("monument")) {
					if(player!=null && DvZ.instance.getPlayerGame(player.getUniqueId())!=null) {
						Game game = DvZ.instance.getPlayerGame(player.getUniqueId());
						
						if(game.teamSetup.getTeam(team)!=null && game.teamSetup.getTeam(team).hasMonument()) {
							returnValue[1] = LuaValue.userdataOf(game.teamSetup.getTeam(team).getMonumentLocation());
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
