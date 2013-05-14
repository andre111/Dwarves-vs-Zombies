package me.andre111.dvz.item.spell;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.andre111.dvz.Game;
import me.andre111.dvz.item.ItemSpell;
import me.andre111.dvz.utils.PlayerHandler;

public class ItemPotionEffect extends ItemSpell {
	private boolean self = true;
	private ArrayList<String> effects = new ArrayList<String>();
	
	@Override
	public void setCastVar(int id, double var) {
		if(id==0) self = (var==1);
	}
	
	@Override
	public void setCastVar(int id, String var) {
		if(id>0) effects.add(var);
	}
	
	@Override
	public boolean cast(Game game, Player player) {
		if(!self) return false;
		
		addEffects(player);
		
		return true;
	}
	@Override
	public boolean cast(Game game, Player player, Block block) {
		return cast(game, player);
	}
	@Override
	public boolean cast(Game game, Player player, Player target) {
		Player p = player;
		if(!self) p = target;
		
		addEffects(p);
		
		return true;
	}
	@Override
	//casted by another spell on that location
	public boolean cast(Game game, Player player, Location loc) {
		return cast(game, player);
	}
	
	private void addEffects(Player player) {
		for(String st : effects) {
			String[] split = st.split(":");
			
			int id = Integer.parseInt(split[0]);
			int duration = Integer.parseInt(split[1]);
			int level = 0;
			if(split.length>2) level = Integer.parseInt(split[2]);
			
			if(!PlayerHandler.hasHigherPotionEffect(player, id, level)) {
				player.addPotionEffect(new PotionEffect(PotionEffectType.getById(id), duration, level), true);
			}
		}
	}
}
