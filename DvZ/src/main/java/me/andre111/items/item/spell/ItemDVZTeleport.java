package me.andre111.items.item.spell;

import java.util.Random;
import java.util.UUID;

import me.andre111.dvz.utils.PlayerHandler;
import me.andre111.items.SpellItems;
import me.andre111.items.item.ItemSpell;
import me.andre111.items.lua.LUAHelper;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

public class ItemDVZTeleport extends ItemSpell {
	private Random rand = new Random();
	
	@Override
	public Varargs invoke(Varargs args) {
		if(args.narg()>=4) {
			LuaValue locN = LUAHelper.getInternalValue(args.arg(1));
			LuaValue playerN = LUAHelper.getInternalValue(args.arg(2));
			LuaValue minDistanceN = args.arg(3);
			LuaValue maxDistanceN = args.arg(4);
			
			if(locN.isuserdata(Location.class) && playerN.isuserdata(UUID.class) && minDistanceN.isnumber() && maxDistanceN.isnumber()) {
				Location loc = (Location) locN.touserdata(Location.class);
				Player player = PlayerHandler.getPlayerFromUUID((UUID) playerN.touserdata(UUID.class));
				int minDistance = minDistanceN.toint();
				int maxDistance = maxDistanceN.toint();
				
				if(player!=null && loc!=null) {
					castIntern(loc, player, minDistance, maxDistance);
					
					return RETURN_TRUE;
				}
			}
		} else {
			SpellItems.log("Missing Argument for "+getClass().getCanonicalName());
		}
		
		return RETURN_FALSE;
	}
	
	private void castIntern(Location location, Player player, int minDistance, int maxDistance) {
		Location loc = location.clone();
		loc.add(minDistance+rand.nextInt(maxDistance-minDistance), minDistance+rand.nextInt(maxDistance-minDistance), minDistance+rand.nextInt(maxDistance-minDistance));
		loc = loc.getWorld().getHighestBlockAt(loc).getLocation();

		while(loc.getBlock().getType()!=Material.AIR && loc.getY()<256) {
			loc.setY(loc.getY()+1);
		}
		loc.setY(loc.getY()+1);
		player.teleport(loc);
	}
}
