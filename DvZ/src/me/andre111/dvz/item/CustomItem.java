package me.andre111.dvz.item;

import java.util.ArrayList;
import java.util.List;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.Game;
import me.andre111.dvz.config.ConfigManager;
import me.andre111.dvz.iface.IUpCounter;
import me.andre111.dvz.manager.StatManager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class CustomItem implements IUpCounter {
	private String internalName;
	
	private int id;
	private int damage;
	private String name;
	private ArrayList<String> lore = new ArrayList<String>();
	private boolean use;
	private boolean ignoreDamage;
	private boolean allowPlace;
	
	private String bookauthor;
	private List<String> bookpages = new ArrayList<String>();
	
	private boolean hasCounter;
	private int counterMax;
	private int counterStep;
	private boolean counterOverridable;
	private boolean counterInterruptMove;
	private boolean counterInterruptDamage;
	private boolean counterInterruptItem;
	
	private ArrayList<ItemEffect> effectR = new ArrayList<ItemEffect>();
	//private ItemSpell castR;
	private int cooldownR;
	private int manaCostR;
	
	private ItemSpell[] castsR;
	

	private ArrayList<ItemEffect> effectL = new ArrayList<ItemEffect>();
	//private ItemSpell castL;
	private int cooldownL;
	private int manaCostL;
	
	private ItemSpell[] castsL;
	
	private ArrayList<ItemEffect> effectEat = new ArrayList<ItemEffect>();
	//private ItemSpell castL;
	private int cooldownEat;
	private int manaCostEat;
	
	private ItemSpell[] castsEat;

	//actions:
	//0 = leftclick
	//1 = rigthclick
	//2 = eat
	public void cast(Game game, int actions, Player player) {
		ItemSpell[] castsTemp = castsR;
		if(actions==0) castsTemp = castsL;
		if(actions==2) castsTemp = castsEat;
		
		if(castsTemp != null) {
			if(cooldownManaCheck(game, actions, player)) return;
		
			castIntern(game, actions, player, null, null);
		}
	}
	public void cast(Game game, int actions, Player player, Block block) {
		ItemSpell[] castsTemp = castsR;
		if(actions==0) castsTemp = castsL;
		if(actions==2) castsTemp = castsEat;
		
		if(castsTemp != null) {
			if(cooldownManaCheck(game, actions, player)) return;
			
			castIntern(game, actions, player, block, null);
		}
	}
	public void cast(Game game, int actions, Player player, Player target) {
		ItemSpell[] castsTemp = castsR;
		if(actions==0) castsTemp = castsL;
		if(actions==2) castsTemp = castsEat;
		
		if(castsTemp != null) {
			if(cooldownManaCheck(game, actions, player)) return;
		
			castIntern(game, actions, player, null, target);
		}
	}
	
	private void castIntern(Game game, int actions, Player player, Block block, Player target) {
		if(isHasCounter()) {
			StatManager.setCounter(player.getName(), this, DvZ.instance.getGameID(game)+"::"+player.getName()+"::"+actions);
		} else {
			castUse(game, actions, player, block, target);
		}
	}
	
	private void castUse(Game game, int actions, Player player, Block block, Player target) {
		ItemSpell[] castsTemp = castsR;
		if(actions==0) castsTemp = castsL;
		if(actions==2) castsTemp = castsEat;
		
		if(castsTemp != null) {
			boolean[] states = new boolean[castsTemp.length];
			
			int pos = 0;
			for(ItemSpell castUse : castsTemp) {
				if(castUse != null) {
					//if(castUse.getType()==0) {
						putOnCoolDown(game, actions, player);
						if(block!=null) {
							states[pos] = castUse.cast(game, player, block, states);
							createEffects(block.getLocation(), actions, "Target");
						}
						else if(target!=null) {
							states[pos] = castUse.cast(game, player, target, states);
							createEffects(target.getLocation(), actions, "Target");
						}
						else {
							states[pos] = castUse.cast(game, player, states);
						}
						createEffects(player.getLocation(), actions, "Caster");
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
	private boolean cooldownManaCheck(Game game, int actions, Player player) {
		//cooldown
		int cd = game.getCustomCooldown(player.getName(), getCooldownName(actions));
		if(cd>0) {
			player.sendMessage(ConfigManager.getLanguage().getString("string_wait", "You have to wait -0- Seconds!").replace("-0-", ""+cd));
			
			return true;
		}
		
		//mana
		int cost = getManaCostR();
		if(actions==0) cost = getManaCostL();
		if(actions==2) cost = getManaCostEat();
		
		if(cost>0)
		if(game.getManaManager().getMana(player.getName())<cost) {
			player.sendMessage(ConfigManager.getLanguage().getString("string_needmana", "You need -0- Mana!").replace("-0-", ""+cost));
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
	
	private void putOnCoolDown(Game game, int action, Player player) {
		int time = cooldownR;
		if(action==0) time = cooldownL;
		if(action==2) time = cooldownEat;
		
		if(time>0) game.setCustomCooldown(player.getName(), getCooldownName(action), time);
	}
	
	private String getCooldownName(int actions) {
		return "citem_"+name+"_"+actions;
	}
	
	public void resetCoolDown(Game game, int action, Player player) {
		game.resetCustomCooldown(player.getName(), getCooldownName(action));
	}
	
	public void createEffects(Location loc, int action, String position) {
		//effects
		ArrayList<ItemEffect> effects = effectR;
		if(action==0) effects = effectL;
		if(action==2) effects = effectEat;
		
		for(ItemEffect st : effects) {
			if(st!=null)
			if(st.getLocation().equals(position))
				st.play(loc);
		}
	}
	
	public ItemStack getItemStack() {
		ItemStack it = new ItemStack(id, 1, (short) damage);
		ItemMeta im = it.getItemMeta();
		
		im.setDisplayName(name);
		im.setLore(lore);
		
		if(im instanceof BookMeta) {
			BookMeta bm = (BookMeta) im;
			
			if(!bookauthor.equals("")) {
				bm.setAuthor(bookauthor);
			}
			bm.setPages(bookpages);
		}
		
		it.setItemMeta(im);
		return it;
	}
	public boolean isThisItem(ItemStack it) {
		if(it.getTypeId()!=id) return false;
		if(!ignoreDamage && it.getDurability()!=damage) return false;
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
	public void setSizeEat(int size) {
		castsEat = new ItemSpell[size];
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
	public boolean isIgnoreDamage() {
		return ignoreDamage;
	}
	public void setIgnoreDamage(boolean ignoreDamage) {
		this.ignoreDamage = ignoreDamage;
	}
	public boolean isAllowPlace() {
		return allowPlace;
	}
	public void setAllowPlace(boolean allowPlace) {
		this.allowPlace = allowPlace;
	}
	public String getBookauthor() {
		return bookauthor;
	}
	public void setBookauthor(String bookauthor) {
		this.bookauthor = bookauthor;
	}
	public List<String> getBookpages() {
		return bookpages;
	}
	public void setBookpages(List<String> bookpages) {
		this.bookpages = bookpages;
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
	public void addEffectR(ItemEffect effect) {
		effectR.add(effect);
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
	public void addEffectL(ItemEffect effect) {
		effectL.add(effect);
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
	public void addEffectEat(ItemEffect effect) {
		effectEat.add(effect);
	}
	public ItemSpell getCastEat(int pos) {
		return castsEat[pos];
	}
	public void setCastEat(ItemSpell cast, int pos) {
		this.castsEat[pos] = cast;
	}
	public int getCooldownEat() {
		return cooldownEat;
	}
	public void setCooldownEat(int cooldownEat) {
		this.cooldownEat = cooldownEat;
	}
	public int getManaCostEat() {
		return manaCostEat;
	}
	public void setManaCostEat(int manaCostEat) {
		this.manaCostEat = manaCostEat;
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
	public void countUPincrease(String vars) {
		String[] split = vars.split("::");
		
		Game game = DvZ.instance.getGame(Integer.parseInt(split[0]));
		Player player = Bukkit.getServer().getPlayerExact(split[1]);
		int action = Integer.parseInt(split[2]);
		
		if(player!=null && game!=null) {
			createEffects(player.getLocation(), action, "CounterStep");
		}
	}
	@Override
	public void countUPinterrupt(String vars) {
		String[] split = vars.split("::");
		
		Game game = DvZ.instance.getGame(Integer.parseInt(split[0]));
		Player player = Bukkit.getServer().getPlayerExact(split[1]);
		int action = Integer.parseInt(split[2]);
		
		if(player!=null && game!=null) {
			createEffects(player.getLocation(), action, "CounterInterrupt");
		}
	}
	@Override
	public void countUPfinish(String vars) {
		String[] split = vars.split("::");
		
		Game game = DvZ.instance.getGame(Integer.parseInt(split[0]));
		Player player = Bukkit.getServer().getPlayerExact(split[1]);
		int action = Integer.parseInt(split[2]);
		
		if(player!=null && game!=null) {
			createEffects(player.getLocation(), action, "CounterFinish");
			
			castUse(game, action, player, null, null);
		}
	}
}
