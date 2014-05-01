/*package me.andre111.dvz.disguise;

import java.util.ArrayList;
import java.util.List;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.Game;
import me.andre111.items.SpellItems;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import com.lenis0012.bukkit.btm.BeTheMob;
import com.lenis0012.bukkit.btm.api.Api;
import com.lenis0012.bukkit.btm.api.Disguise;
import com.lenis0012.bukkit.btm.events.PlayerInteractDisguisedEvent;
import com.lenis0012.bukkit.btm.events.PlayerUndisguiseEvent;

public class DSystem_BeTheMob implements DSystem, Listener {
	private Api api;
	private DvZ plugin;
	private int nextID = Integer.MIN_VALUE;
	
	@Override
	public void initListeners(DvZ plugin) {
		api = BeTheMob.getApi();

		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@Override
	public void disguiseP(Player player, DvZDisguiseType disguise) {
		List<String> extras = new ArrayList<String>();
		if(disguise.isBaby()) {
			extras.add("-baby");
		}
		
		switch(disguise) {
		case VILLAGER_ZOMBIE:
			DvZ.log("Entity Subtypes are not supported by BeTheMob - using normal Zombie!");
			Disguise dis1 = api.createDisguise(player, player.getLocation(), disguise.getEntityType(), extras);
			api.addDisguise(player, dis1);
			break;
		case WITHER_SKELETON:
			DvZ.log("Entity Subtypes are not supported by BeTheMob - using normal Skeleton!");
			Disguise dis2 = api.createDisguise(player, player.getLocation(), disguise.getEntityType(), extras);
			api.addDisguise(player, dis2);
			break;
		//----------
		//SPECIAL
		//----------
		//HORSES
		case UNDEAD_HORSE:
		case SKELETON_HORSE:
			DvZ.log("Entity Subtypes are not supported by BeTheMob - using normal Horse!");
			Disguise disH = api.createDisguise(player, player.getLocation(), disguise.getEntityType(), extras);
			api.addDisguise(player, disH);
			break;
		//CHARGED
		case CHARGED_CREEPER:
			extras.add("-charged");
			
			Disguise disCC = api.createDisguise(player, player.getLocation(), disguise.getEntityType(), extras);
			api.addDisguise(player, disCC);
			break;
		default:
			Disguise dis3 = api.createDisguise(player, player.getLocation(), disguise.getEntityType(), extras);
			api.addDisguise(player, dis3);
			break;
		}
	}
	@Override
	public void disguiseP(Player player, String disguise) {
		EntityType et = EntityType.fromName(disguise);
		if(et==null) et = EntityType.valueOf(disguise);
		if(et==null) {
			DvZ.log("Unknown Entity-Disguise: "+disguise);
			return;
		}
		
		Disguise dis = api.createDisguise(player, player.getLocation(), et, null);
		api.addDisguise(player, dis);
	}

	@Override
	public void undisguiseP(Player player) {
		if(api.isDisguised(player)) {
			api.removeDisguise(player);
		}
	}

	@Override
	public void redisguiseP(Player player) {
		if(api.isDisguised(player)) {
			Disguise dis = api.getDisguise(player);
			api.removeDisguise(player);
			api.addDisguise(player, dis);
		}
	}

	@Override
	public int newEntityID() {
		return nextID++;
	}

	//rightclicking disguises
	@EventHandler
	public void onPlayerInvalidInteractEntity(final PlayerInteractDisguisedEvent event) {
		Bukkit.getScheduler().runTask(plugin, new Runnable() {
			public void run() {
				Player player = event.getPlayer();
				if(player!=null) {
					Game game = plugin.getPlayerGame(player.getUniqueId());

					if (game!=null) {
						Player target = event.getDisguised().getPlayer();
						ItemStack item = event.getPlayer().getItemInHand();

						//TODO - this should somehow be handled by the SpellItems Plugin
						SpellItems.playerSpecialItemC(player, item, 1, null, target);
						game.playerRCPlayer(player, item, target);
					}
				}
			}
		});
	}
	
	//TODO - check if this is really needed
	@EventHandler
	public void onPlayerUndisguise(PlayerUndisguiseEvent event) {
		Player p = event.getPlayer();
		Game game = plugin.getPlayerGame(p.getUniqueId());
		if(game!=null) {
			if(game.getState()>1) {
				CustomClass cc = game.getClass(p.getUniqueId());
				if(cc.getDisguise()!=null && !cc.getDisguise().equals("")) {
					event.setCancelled(true);
				}
			}
		}
	}
}*/
