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
		if(refill) mana.put(player, maxM);
	}
	
	public void setManaRegen(String player, int regen) {
		manaRegen.put(player, regen);
	}
	
	public int getMana(String player) {
		if(!mana.containsKey(player)) return 0;
		
		return mana.get(player);
	}
}
