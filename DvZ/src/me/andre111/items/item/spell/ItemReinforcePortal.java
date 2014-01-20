package me.andre111.items.item.spell;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.Game;
import me.andre111.dvz.config.ConfigManager;
import me.andre111.items.item.ItemSpell;

public class ItemReinforcePortal extends ItemSpell {
	
	@Override
	public boolean cast(Player player, Location loc, Player target, Block block) {
		if(player==null) return false;
		
		return castIntern(player, loc);
	}
	
	private boolean castIntern(Player player, Location loc) {
		Game game = DvZ.instance.getPlayerGame(player.getName());
		
		if(game.enderPortal!=null) {
			return reinforePortal(game, player);
		}
		
		return false;
	}
	
	private boolean reinforePortal(Game game, Player player) {
		if(player.getName().equals(game.enderMan)) {
			Location nloc = game.enderPortal;
			World w = nloc.getWorld();
			
			w.strikeLightningEffect(nloc);
			for(int i=0; i<10; i++) {
				ItemStack it = new ItemStack(Material.ENDER_STONE, 1);
				ItemMeta im = it.getItemMeta();
				im.setDisplayName(ConfigManager.getLanguage().getString("string_spell_disableportal","Disable Portal"));
				ArrayList<String> li = new ArrayList<String>();
				li.add(ConfigManager.getLanguage().getString("string_used_seconds","Can be used every -0- Seconds!").replace("-0-", ""+ConfigManager.getStaticConfig().getInt("spelltime_disableportal",3)));
				im.setLore(li);
				it.setItemMeta(im);
				
				w.dropItem(nloc, it);
			}
			
			player.teleport(nloc);
			player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 95000, -20));
			player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 95000, 4));
			
			game.enderActive = true;
			
			game.broadcastMessage(ConfigManager.getLanguage().getString("string_portal_reinforce","The Portal has been reinforced!"));
			
			return true;
		}
		return false;
	}
}
