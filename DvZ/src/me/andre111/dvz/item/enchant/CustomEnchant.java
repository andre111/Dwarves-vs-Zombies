package me.andre111.dvz.item.enchant;

import java.util.ArrayList;
import java.util.List;

import me.andre111.dvz.Game;
import me.andre111.dvz.item.ItemEffect;
import me.andre111.dvz.item.ItemSpell;
import me.andre111.dvz.volatileCode.DynamicClassFunctions;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CustomEnchant {
	private String internalName;
	
	private int id;
	private String name;
	
	private ArrayList<ItemEffect> effects = new ArrayList<ItemEffect>();
	private ItemSpell[] casts;
	
	
	public void applyToPlayer(Player player) {
		
	}

	public ItemStack enchantItem(ItemStack it, int level) {
		ItemMeta im = it.getItemMeta();
		List<String> st = im.getLore();
		
		if(st==null) st = new ArrayList<String>();
		st.add(0, ChatColor.GRAY+getName()+" "+getLevelName(level));
		
		im.setLore(st);
		it.setItemMeta(im);
		
		return DynamicClassFunctions.addGlow(it);
	}
	
	public String getLevelName(int level) {
		//TODO - create real levels for Enchantments
		return "I";
	}
	
	public void cast(Game game, Player player, Player target) {
		if(casts != null) {
			boolean[] states = new boolean[casts.length];
			
			int pos = 0;
			for(ItemSpell castUse : casts) {
				if(castUse != null) {
					states[pos] = castUse.cast(game, player, target, states);
					
					createEffects(target.getLocation(), "Target");
					createEffects(player.getLocation(), "Caster");
				}
				
				pos += 1;
			}
		}
	}
	
	public void createEffects(Location loc, String position) {
		//effects
		for(ItemEffect st : effects) {
			if(st!=null)
			if(st.getLocation().equals(position))
				st.play(loc);
		}
	}


	public String getInternalName() {
		return internalName;
	}
	public void setInternalName(String internalName) {
		this.internalName = internalName;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void addEffect(ItemEffect effect) {
		effects.add(effect);
	}
	public void setSize(int size) {
		casts = new ItemSpell[size];
	}
	public ItemSpell getCast(int pos) {
		return casts[pos];
	}
	public void setCast(ItemSpell cast, int pos) {
		this.casts[pos] = cast;
	}
}
