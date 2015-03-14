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

//TODO - document team changes
public class ItemClassCheck extends ItemSpell {
	@Override
	public Varargs invoke(Varargs args) {
		if(args.narg()>=3) {
			LuaValue playerN = LUAHelper.getInternalValue(args.arg(1));
			//LuaValue typeN = args.arg(2);
			LuaValue classidN = args.arg(2);
			LuaValue teamN = args.arg(3);
			
			if(playerN.isuserdata(UUID.class) && classidN.isstring() && teamN.isstring()) {
				Player player = PlayerHandler.getPlayerFromUUID((UUID) playerN.touserdata(UUID.class));
				String classid = classidN.toString();
				String team = teamN.toString();
				
				if(player!=null) {
					Game game = DvZ.instance.getPlayerGame(player.getUniqueId());
					if(game==null) return RETURN_FALSE;
					
					//if(game.isDwarf(player.getUniqueId(), true)) {
						String dId = game.getClass(player.getUniqueId()).getInternalName();
						String pteam = game.playerteam.get(player.getUniqueId());
						
						if((dId.equals(classid) || classid.equals("")) && pteam.equals(team)) return RETURN_TRUE;
					//}
				}
			}
		} else {
			SpellItems.log("Missing Argument for "+getClass().getCanonicalName());
		}
		
		return RETURN_FALSE;
	}
}
