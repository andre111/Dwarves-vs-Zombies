package me.andre111.dvz.item.spell;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.Game;
import me.andre111.dvz.config.ConfigManager;
import me.andre111.dvz.item.ItemSpell;

public class ItemBlink extends ItemSpell {
	private int range = 75;
	private boolean isReset = true;
	
	@Override
	public void setCastVar(int id, double var) {
		if(id==0) range = (int) Math.round(var);
		else if(id==1) isReset = (var==1);
	}
	
	@Override
	public boolean cast(Game game, Player player) {	
		BlockIterator iter; 
		try {
			iter = new BlockIterator(player, range>0&&range<150?range:150);
		} catch (IllegalStateException e) {
			iter = null;
		}
		Block prev = null;
		Block found = null;
		Block b;
		if (iter != null) {
			while (iter.hasNext()) {
				b = iter.next();
				if (DvZ.transparent.contains((byte)b.getTypeId())) {
					prev = b;
				} else {
					found = b;
					break;
				}
			}
		}

		if (found != null) {
			Location loc = null;
			if (range > 0 && !(found.getLocation().distanceSquared(player.getLocation()) < range*range)) {
			} else if (DvZ.isPathable(found.getRelative(0,1,0)) && DvZ.isPathable(found.getRelative(0,2,0))) {
				// try to stand on top
				loc = found.getLocation();
				loc.setY(loc.getY() + 1);
			} else if (prev != null && DvZ.isPathable(prev) && DvZ.isPathable(prev.getRelative(0,1,0))) {
				// no space on top, put adjacent instead
				loc = prev.getLocation();
			}
			if (loc != null) {
				loc.setX(loc.getX()+.5);
				loc.setZ(loc.getZ()+.5);
				loc.setPitch(player.getLocation().getPitch());
				loc.setYaw(player.getLocation().getYaw());
				player.teleport(loc);
				getItem().createEffects(loc.clone(), isLeft(), "Teleport");
				player.sendMessage(ConfigManager.getLanguage().getString("string_blink","You blink away!"));
				return true;
			} else {
				player.sendMessage(ConfigManager.getLanguage().getString("string_cannot_blink","You cannot blink there!"));
				if(isReset) {
					resetCoolDown(game, player);
				}
				return false;
			}
		} else {
			player.sendMessage(ConfigManager.getLanguage().getString("string_cannot_blink","You cannot blink there!"));
			if(isReset) {
				resetCoolDown(game, player);
			}
			return false;
		}
	}
	
	@Override
	public boolean cast(Game game, Player player, Block block) {
		return cast(game, player);
	}
	@Override
	public boolean cast(Game game, Player player, Player target) {
		return cast(game, player);
	}
	
	@Override
	public boolean cast(Game game, Player player, Location loc) {
		if (loc != null) {
			loc.setX(loc.getX()+.5);
			loc.setZ(loc.getZ()+.5);
			loc.setPitch(player.getLocation().getPitch());
			loc.setYaw(player.getLocation().getYaw());
			player.getWorld().playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 1);
			player.getWorld().playEffect(player.getLocation(), Effect.ENDER_SIGNAL, 0);
			player.teleport(loc);
			player.getWorld().playEffect(player.getLocation(), Effect.ENDER_SIGNAL, 0);
			player.sendMessage(ConfigManager.getLanguage().getString("string_blink","You blink away!"));
			
			return true;
		}
		
		if(isReset) {
			resetCoolDown(game, player);
		}
		return false;
	}
}
