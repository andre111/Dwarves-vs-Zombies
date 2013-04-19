package me.andre111.dvz.item;

import java.util.ArrayList;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.Game;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CustomItem {
	private String internalName;
	
	private int id;
	private int damage;
	private String name;
	private ArrayList<String> lore = new ArrayList<String>();
	
	private ArrayList<String> effectsR = new ArrayList<String>();
	private ArrayList<String> soundsR = new ArrayList<String>();
	//private ItemSpell castR;
	private int cooldownR;
	
	private ItemSpell[] castsR;
	
	private ArrayList<String> effectsL = new ArrayList<String>();
	private ArrayList<String> soundsL = new ArrayList<String>();
	//private ItemSpell castL;
	private int cooldownL;
	
	
	private ItemSpell[] castsL;
	
	public void cast(Game game, boolean left, Player player) {
		if(cooldownCheck(game, left, player)) return;
		
		/*ItemSpell castUse = castR;
		if(left) castUse = castL;
		
		if(castUse.getType()==0) {
			putOnCoolDown(game, left, player);
			createEffects(player.getLocation(), left, "Caster");
			castUse.cast(game, player);
		} */
		
		ItemSpell[] castsTemp = castsR;
		if(left) castsTemp = castsL;
		
		boolean[] states = new boolean[castsTemp.length];
		
		int pos = 0;
		for(ItemSpell castUse : castsTemp) {
			if(castUse != null) {
				//if(castUse.getType()==0) {
					putOnCoolDown(game, left, player);
					createEffects(player.getLocation(), left, "Caster");
					states[pos] = castUse.cast(game, player, states);
				//}
			}
			
			pos += 1;
		}
	}
	public void cast(Game game, boolean left, Player player, Block block) {
		if(cooldownCheck(game, left, player)) return;
		
		/*ItemSpell castUse = castR;
		if(left) castUse = castL;
		
		if(castUse.getType()==1) {
			putOnCoolDown(game, left, player);
			createEffects(player.getLocation(), left, "Caster");
			createEffects(block.getLocation(), left, "Target");
			castUse.cast(game, player, block);
		} else cast(game, left, player);*/
		
		ItemSpell[] castsTemp = castsR;
		if(left) castsTemp = castsL;
		
		boolean[] states = new boolean[castsTemp.length];
		
		int pos = 0;
		for(ItemSpell castUse : castsTemp) {
			if(castUse != null) {
				//if(castUse.getType()==1) {
					putOnCoolDown(game, left, player);
					createEffects(player.getLocation(), left, "Caster");
					createEffects(block.getLocation(), left, "Target");
					states[pos] = castUse.cast(game, player, block, states);
				//} else cast(game, left, pos, player);
			}
			
			pos += 1;
		}
	}
	public void cast(Game game, boolean left, Player player, Player target) {
		if(cooldownCheck(game, left, player)) return;
		
		/*ItemSpell castUse = castR;
		if(left) castUse = castL;
		
		if(castUse.getType()==2) {
			putOnCoolDown(game, left, player);
			createEffects(player.getLocation(), left, "Caster");
			createEffects(target.getLocation(), left, "Target");
			castUse.cast(game, player, target);
		} else cast(game, left, player);*/
		
		ItemSpell[] castsTemp = castsR;
		if(left) castsTemp = castsL;
		
		boolean[] states = new boolean[castsTemp.length];
		
		int pos = 0;
		for(ItemSpell castUse : castsTemp) {
			if(castUse != null) {
				//if(castUse.getType()==2) {
					putOnCoolDown(game, left, player);
					createEffects(player.getLocation(), left, "Caster");
					createEffects(target.getLocation(), left, "Target");
					states[pos] = castUse.cast(game, player, target, states);
				//} else cast(game, left, pos, player);
			}
			
			pos += 1;
		}
	}
	
	/*private boolean cast(Game game, boolean left, int pos, Player player) {
		ItemSpell castUse = castsR[pos];
		if(left) castUse = castsL[pos];
		
		if(castUse.getType()==0) {
			putOnCoolDown(game, left, player);
			createEffects(player.getLocation(), left, "Caster");
			castUse.cast(game, player);
			return true;
		} 
		
		return false;
	}*/
	
	//is the item currently on cooldown
	private boolean cooldownCheck(Game game, boolean left, Player player) {
		int cd = game.getCustomCooldown(player.getName(), getCooldownName(left));
		if(cd>0) {
			player.sendMessage(DvZ.getLanguage().getString("string_wait", "You have to wait -0- Seconds!").replaceAll("-0-", ""+cd));
			return true;
		}
		
		return false;
	}
	
	private void putOnCoolDown(Game game, boolean left, Player player) {
		int time = cooldownR;
		if(left) time = cooldownL;
		
		if(time>0) game.setCustomCooldown(player.getName(), getCooldownName(left), time);
	}
	
	private String getCooldownName(boolean left) {
		String lr = "R";
		if(left) lr = "L";
		
		return "citem_"+name+"_"+lr;
	}
	
	public void createEffects(Location loc, boolean left, String position) {
		//effects
		ArrayList<String> effects = effectsR;
		if(left) effects = effectsL;
		
		for(String st : effects) {
			String[] split = st.split(":");
			int dt = 0;
			String cst = "Caster";
			if(split.length>1) dt = Integer.parseInt(split[1]);
			if(split.length>2) cst = split[2];
			
			if(cst.equals(position))
				loc.getWorld().playEffect(loc, Effect.valueOf(split[0]), dt);
		}
		//sounds
		ArrayList<String> sounds = soundsR;
		if(left) sounds = soundsL;
		
		for(String st : sounds) {
			String[] split = st.split(":");
			float volume = 1F;
			float pitch = 1F;
			String cst = "Caster";
			if(split.length>1) volume = Float.parseFloat(split[1]);
			if(split.length>2) pitch = Float.parseFloat(split[2]);
			if(split.length>3) cst = split[3];

			if(cst.equals(position))
				loc.getWorld().playSound(loc, Sound.valueOf(split[0]), volume, pitch);
		}
	}
	
	public ItemStack getItemStack() {
		ItemStack it = new ItemStack(id, 1, (short) damage);
		ItemMeta im = it.getItemMeta();
		
		im.setDisplayName(name);
		im.setLore(lore);
		
		it.setItemMeta(im);
		return it;
	}
	public boolean isThisItem(ItemStack it) {
		if(it.getTypeId()!=id) return false;
		if(it.getDurability()!=damage) return false;
		ItemMeta im = it.getItemMeta();
		if(!im.getDisplayName().equals(name)) return false;
		if(!im.getLore().equals(lore)) return false;
		
		return true;
	}
	
	public void setSizeR(int size) {
		castsR = new ItemSpell[size];
	}
	public void setSizeL(int size) {
		castsL = new ItemSpell[size];
	}
	
	public String getInternalName() {
		return internalName;
	}
	public void setInternalName(String internalName) {
		this.internalName = internalName;
	}
	public void setID(int id) {
		this.id = id;
	}
	public void setDamage(int damage) {
		this.damage = damage;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void addLore(String nlore) {
		lore.add(nlore);
	}
	public void addEffectR(String effect) {
		effectsR.add(effect);
	}
	public void addSoundR(String sound) {
		soundsR.add(sound);
	}
	public ItemSpell getCastR(int pos) {
		return castsR[pos];
	}
	public void setCastR(ItemSpell cast, int pos) {
		this.castsR[pos] = cast;
	}
	public int getCooldownR() {
		return cooldownR;
	}
	public void setCooldownR(int cooldownR) {
		this.cooldownR = cooldownR;
	}
	public void addEffectL(String effect) {
		effectsL.add(effect);
	}
	public void addSoundL(String sound) {
		soundsL.add(sound);
	}
	public ItemSpell getCastL(int pos) {
		return castsL[pos];
	}
	public void setCastL(ItemSpell cast, int pos) {
		this.castsL[pos] = cast;
	}
	public int getCooldownL() {
		return cooldownL;
	}
	public void setCooldownL(int cooldownL) {
		this.cooldownL = cooldownL;
	}
}
