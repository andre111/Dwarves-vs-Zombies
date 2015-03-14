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

public class ItemCrystalStorage extends ItemSpell {
	@Override
	public Varargs invoke(Varargs args) {
		if(args.narg()>=2) {
			LuaValue playerN = LUAHelper.getInternalValue(args.arg(1));
			LuaValue globalN = args.arg(2);
			
			if(playerN.isuserdata(UUID.class) && globalN.isboolean()) {
				Player player = PlayerHandler.getPlayerFromUUID((UUID) playerN.touserdata(UUID.class));
				boolean global = globalN.toboolean();
				
				if(player!=null) {
					Game game = DvZ.instance.getPlayerGame(player.getUniqueId());
					if(game==null) return RETURN_FALSE;
					
					player.openInventory(game.getCrystalChest(player.getUniqueId(), global));
					
					return RETURN_TRUE;
				}
			}
		} else {
			SpellItems.log("Missing Argument for "+getClass().getCanonicalName());
		}
		
		return RETURN_FALSE;
	}
}
