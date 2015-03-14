package me.andre111.items.item.spell;

import java.util.UUID;

import me.andre111.dvz.manager.HighscoreManager;
import me.andre111.dvz.manager.PlayerScore;
import me.andre111.dvz.utils.PlayerHandler;
import me.andre111.items.SpellItems;
import me.andre111.items.item.ItemSpell;
import me.andre111.items.lua.LUAHelper;

import org.bukkit.entity.Player;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

public class ItemDvZClassPoint extends ItemSpell {
	@Override
	public Varargs invoke(Varargs args) {
		if(args.narg()>=2) {
			LuaValue playerN = LUAHelper.getInternalValue(args.arg(1));
			LuaValue pointsN = args.arg(2);
			
			if(playerN.isuserdata(UUID.class) && pointsN.isnumber()) {
				Player player = PlayerHandler.getPlayerFromUUID((UUID) playerN.touserdata(UUID.class));
				int points = pointsN.toint();
				
				if(player!=null) {
					PlayerScore pscore = HighscoreManager.getPlayerScore(player.getUniqueId());
					pscore.setClasspoints(pscore.getClasspoints()+points);
					
					return RETURN_TRUE;
				}
			}
		} else {
			SpellItems.log("Missing Argument for "+getClass().getCanonicalName());
		}
		
		return RETURN_FALSE;
	}
}
