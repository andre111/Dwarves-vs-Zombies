package me.andre111.items.item.spell;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.Game;
import me.andre111.dvz.utils.PlayerHandler;
import me.andre111.items.SpellItems;
import me.andre111.items.item.ItemSpell;

import org.bukkit.entity.Player;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

//TODO - document team changes
public class ItemClassCheck extends ItemSpell {
	/*private String playername = "";
	private String type = "dwarf";
	int classid = 0;*/
	
	@Override
	public Varargs invoke(Varargs args) {
		if(args.narg()>=3) {
			LuaValue playerN = args.arg(1);
			//LuaValue typeN = args.arg(2);
			LuaValue classidN = args.arg(2);
			LuaValue teamN = args.arg(3);
			
			if(playerN.isstring() && classidN.isnumber() && teamN.isstring()) {
				Player player = PlayerHandler.getPlayerFromUUID(playerN.toString());
				int classid = classidN.toint();
				String team = teamN.toString();
				
				if(player!=null) {
					Game game = DvZ.instance.getPlayerGame(player.getUniqueId());
					if(game==null) return RETURN_FALSE;
					
					//if(game.isDwarf(player.getUniqueId(), true)) {
						int dId = game.getPlayerState(player.getUniqueId())-Game.classMin;
						String pteam = game.playerteam.get(player.getUniqueId());
						
						if(dId==classid && pteam.equals(team)) return RETURN_TRUE;
					//}
				}
			}
		} else {
			SpellItems.log("Missing Argument for "+getClass().getCanonicalName());
		}
		
		return RETURN_FALSE;
	}
}
