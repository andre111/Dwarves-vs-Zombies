package me.andre111.dvz;

import java.util.HashMap;
import java.util.Map;

public class ManaManager {
	private HashMap<String, Integer> mana = new HashMap<String, Integer>();
	private HashMap<String, Integer> manaRegen = new HashMap<String, Integer>();
	private HashMap<String, Integer> maxMana = new HashMap<String, Integer>();
	
	public void tick() {
		//regenerate mana
		for(Map.Entry<String, Integer> e : mana.entrySet()){
			String player = e.getKey();
			int m = e.getValue();
			int maxm = maxMana.get(player);
			
			if(m<maxm) {
				m += manaRegen.get(player);
				if(m>maxm) m = maxm;
				
				changedMana(player, m);
				
				mana.put(player, m);
			}
		}
	}
	
	public void reset() {
		mana.clear();
		manaRegen.clear();
		maxMana.clear();
	}
	
	public void setMaxMana(String player, int maxM, boolean refill) {
		maxMana.put(player, maxM);
		if(refill) {
			mana.put(player, maxM);
			changedMana(player, maxM);
		}
	}
	
	public void setManaRegen(String player, int regen) {
		manaRegen.put(player, regen);
	}
	
	public int getMana(String player) {
		if(!mana.containsKey(player)) return 0;
		
		return mana.get(player);
	}
	
	public void substractMana(String player, int ammount) {
		int value = 0;
		if(mana.containsKey(player)) value = mana.get(player);
		
		value -= ammount;
		if(value<0) value = 0;
		
		mana.put(player, value);
		changedMana(player, value);
	}
	
	//update mana stat
	private void changedMana(String player, int ammount) {
		StatManager.setStat(player, DvZ.getLanguage().getString("scoreboard_mana", "§5Mana"), ammount);
	}
}
