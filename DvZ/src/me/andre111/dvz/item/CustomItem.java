package me.andre111.dvz.item;

import java.util.ArrayList;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.Game;
import me.andre111.dvz.StatManager;
import me.andre111.dvz.iface.IUpCounter;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CustomItem implements IUpCounter {
	private String internalName;
	
	private int id;
	private int damage;
	private String name;
	private ArrayList<String> lore = new ArrayList<String>();
	private boolean use;
	
	private boolean hasCounter;
	private int counterMax;
	private int counterStep;
	private boolean counterOverridable;
	private boolean counterInterruptMove;
	private boolean counterInterruptDamage;
	private boolean counterInterruptItem;
	
	private ArrayList<String> effectsR = new ArrayList<String>();
	private ArrayList<String> soundsR = new ArrayList<String>();
	//private ItemSpell castR;
	private int cooldownR;
	private int manaCostR;
	
	private ItemSpell[] castsR;
	
	private ArrayList<String> effectsL = new ArrayList<String>();
	private ArrayList<String> soundsL = new ArrayList<String>();
	//private ItemSpell castL;
	private int cooldownL;
	private int manaCostL;
	
	private ItemSpell[] castsL;
	
	//TODO - add option to ignore damage(for bows or swords or something)
	
	public void cast(Game game, boolean left, Player player) {
		if(cooldownManaCheck(game, left, player)) return;
		
		castIntern(game, left, player, null, null);
	}
	public void cast(Game game, boolean left, Player player, Block block) {
		if(cooldownManaCheck(game, left, player)) return;
		
		castIntern(game, left, player, block, null);
	}
	public void cast(Game game, boolean left, Player player, Player target) {
		if(cooldownManaCheck(game, left, player)) return;
		
		castIntern(game, left, player, null, target);
	}
	
	private void castIntern(Game game, boolean left, Player player, Block block, Player target) {
		if(isHasCounter()) {
			StatManager.setCounter(player.getName(), this, DvZ.instance.getGameID(game)+"::"+player.getName()+"::"+left);
		} else {
			castUse(game, left, player, block, target);
		}
	}
	
	private void castUse(Game game, boolean left, Player player, Block block, Player target) {
		ItemSpell[] castsTemp = castsR;
		if(left) castsTemp = castsL;
		
		if(castsTemp != null) {
			boolean[] states = new boolean[castsTemp.length];
			
			int pos = 0;
			for(ItemSpell castUse : castsTemp) {
				if(castUse != null) {
					//if(castUse.getType()==0) {
						putOnCoolDown(game, left, player);
						if(block!=null) {
							states[pos] = castUse.cast(game, player, block, states);
							createEffects(block.getLocation(), left, "Target");
						}
						else if(target!=null) {
							states[pos] = castUse.cast(game, player, target, states);
							createEffects(target.getLocation(), left, "Target");
						}
						else {
							states[pos] = castUse.cast(game, player, states);
						}
						createEffects(player.getLocation(), left, "Caster");
					//}
				}
				
				pos += 1;
			}
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
	private boolean cooldownManaCheck(Game game, boolean left, Player player) {
		//cooldown
		int cd = game.getCustomCooldown(player.getName(), getCooldownName(left));
		if(cd>0) {
			player.sendMessage(DvZ.getLanguage().getString("string_wait", "You have to wait -0- Seconds!").replaceAll("-0-", ""+cd));
			
			return true;
		}
		
		//mana
		int cost = getManaCostR();
		if(left) cost = getManaCostL();
		
		if(cost>0)
		if(game.getManaManager().getMana(player.getName())<cost) {
			player.sendMessage(DvZ.getLanguage().getString("string_needmana", "You need -0- Mana!").replaceAll("-0-", ""+cost));
			return true;
		}
		
		game.getManaManager().substractMana(player.getName(), cost);
		
		//substract items
		if(isUse()) {
			ItemStack item = player.getItemInHand();
			if(item.getAmount()-1==0) 
				item.setType(Material.AIR);
			else
				item.setAmount(item.getAmount()-1);
			
			player.setItemInHand(item);
		}
		
		//everything ok
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
	
	public void resetCoolDown(Game game, boolean left, Player player) {
		game.resetCustomCooldown(player.getName(), getCooldownName(left));
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
		if(im.hasLore()) {
			if(!im.getLore().equals(lore)) return false;
		} else {
			if(lore.size()>0) return false;
		}
		
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
	public boolean isUse() {
		return use;
	}
	public void setUse(boolean use) {
		this.use = use;
	}
	public boolean isHasCounter() {
		return hasCounter;
	}
	public void setHasCounter(boolean hasCounter) {
		this.hasCounter = hasCounter;
	}
	public int getCounterMax() {
		return counterMax;
	}
	public void setCounterMax(int counterMax) {
		this.counterMax = counterMax;
	}
	public int getCounterStep() {
		return counterStep;
	}
	public void setCounterStep(int counterStep) {
		this.counterStep = counterStep;
	}
	public boolean isCounterOverridable() {
		return counterOverridable;
	}
	public void setCounterOverridable(boolean counterOverridable) {
		this.counterOverridable = counterOverridable;
	}
	public boolean isCounterInterruptMove() {
		return counterInterruptMove;
	}
	public void setCounterInterruptMove(boolean counterInterruptMove) {
		this.counterInterruptMove = counterInterruptMove;
	}
	public boolean isCounterInterruptDamage() {
		return counterInterruptDamage;
	}
	public void setCounterInterruptDamage(boolean counterInterruptDamage) {
		this.counterInterruptDamage = counterInterruptDamage;
	}
	public boolean isCounterInterruptItem() {
		return counterInterruptItem;
	}
	public void setCounterInterruptItem(boolean counterInterruptItem) {
		this.counterInterruptItem = counterInterruptItem;
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
	public int getManaCostR() {
		return manaCostR;
	}
	public void setManaCostR(int manaCostR) {
		this.manaCostR = manaCostR;
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
	public int getManaCostL() {
		return manaCostL;
	}
	public void setManaCostL(int manaCostL) {
		this.manaCostL = manaCostL;
	}
	
	
	//Upcounter methods and fields
	@Override
	public int countUPgetMax() {
		return counterMax;
	}
	@Override
	public int countUPperSecond() {
		return counterStep;
	}
	@Override
	public boolean countUPOverridable() {
		return counterOverridable;
	}
	@Override
	public boolean countUPinterruptMove() {
		return counterInterruptMove;
	}
	@Override
	public boolean countUPinterruptDamage() {
		return counterInterruptDamage;
	}
	@Override
	public boolean countUPinterruptItemChange() {
		return counterInterruptItem;
	}
	@Override
	public void countUPinterrupt() {}
	@Override
	public void countUPfinish(String vars) {
		String[] split = vars.split("::");
		
		Game game = DvZ.instance.getGame(Integer.parseInt(split[0]));
		Player player = Bukkit.getServer().getPlayerExact(split[1]);
		boolean left = Boolean.parseBoolean(split[2]);
		
		if(player!=null && game!=null) {
			castUse(game, left, player, null, null);
		}
	}
}
