package me.andre111.items.item.spell;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.Game;
import me.andre111.dvz.utils.PlayerHandler;
import me.andre111.items.SpellItems;
import me.andre111.items.item.ItemSpell;

import org.bukkit.entity.Player;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

public class ItemPortalTeleport extends ItemSpell {
	@Override
	public Varargs invoke(Varargs args) {
		if(args.narg()>=1) {
			LuaValue playerN = args.arg(1);
			
			if(playerN.isstring()) {
				Player player = PlayerHandler.getPlayerFromUUID(playerN.toString());
				
				if(player!=null) {
					Game game = DvZ.instance.getPlayerGame(player.getUniqueId());
					if(game!=null && game.enderActive && game.enderPortal!=null) {
						player.teleport(game.enderPortal);
						
						return RETURN_TRUE;
					}
				}
			}
		} else {
			SpellItems.log("Missing Argument for "+getClass().getCanonicalName());
		}
		
		return RETURN_FALSE;
	}
}
