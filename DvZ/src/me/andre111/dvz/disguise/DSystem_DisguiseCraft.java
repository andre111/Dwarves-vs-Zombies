package me.andre111.dvz.disguise;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.Game;
import me.andre111.items.SpellItems;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import pgDev.bukkit.DisguiseCraft.DisguiseCraft;
import pgDev.bukkit.DisguiseCraft.api.DisguiseCraftAPI;
import pgDev.bukkit.DisguiseCraft.api.PlayerUndisguiseEvent;
import pgDev.bukkit.DisguiseCraft.disguise.Disguise;
import pgDev.bukkit.DisguiseCraft.disguise.DisguiseType;
import pgDev.bukkit.DisguiseCraft.listeners.PlayerInvalidInteractEvent;

public class DSystem_DisguiseCraft implements DSystem, Listener {
	private DisguiseCraftAPI api;
	private DvZ plugin;
	
	@Override
	public void initListeners(DvZ plugin) {
		this.plugin = plugin;
		api = DisguiseCraft.getAPI();
		
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@Override
	public void disguiseP(Player player, String disguise) {
		disguiseP(player, new Disguise(api.newEntityID(), "", DisguiseType.fromString(disguise)));
	}
	
	public void disguiseP(Player player, Disguise disguise) {
		if(api.isDisguised(player)) {
			api.changePlayerDisguise(player, disguise);
		} else {
			api.disguisePlayer(player, disguise);
		}
	}

	@Override
	public void undisguiseP(Player player) {
		if(api.isDisguised(player)) api.undisguisePlayer(player);
	}

	@Override
	public void redisguiseP(Player player) {
		if( api.isDisguised(player)) {
			Disguise dg = api.getDisguise(player);
			api.undisguisePlayer(player);
			api.disguisePlayer(player, dg);
		}
	}

	@Override
	public int newEntityID() {
		return api.newEntityID();
	}

	
	//rightclicking disguises
	@EventHandler
	public void onPlayerInvalidInteractEntity(final PlayerInvalidInteractEvent event) {
		Bukkit.getScheduler().runTask(plugin, new Runnable() {
			public void run() {
				Player player = event.getPlayer();
				if(player!=null) {
					Game game = plugin.getPlayerGame(player.getName());

					if (game!=null) {
						DisguiseCraft dc = (DisguiseCraft) Bukkit.getPluginManager().getPlugin("DisguiseCraft"); //TODO - maybe a better way of getting Disguisecraft(or when the API changes use it)

						Player target = dc.disguiseIDs.get(event.getTarget());
						ItemStack item = event.getPlayer().getItemInHand();

						//TODO - this should somehow be handled by the SpellItems Plugin
						SpellItems.playerSpecialItemC(player, item, 1, null, target);
						game.playerRCPlayer(player, item, target);
					}
				}
			}
		});
	}
	//TODO - remove temporary workaround when disguisecraft fixes it
	//cancel the event if the player is monster or dragon
	//to circuvent the disguisecraft permissions
	@EventHandler
	public void onPlayerUndisguise(PlayerUndisguiseEvent event) {
		Player p = event.getPlayer();
		if(p==null) return;
		
		Game game = plugin.getPlayerGame(p.getName());
		if(game!=null) {
			if(game.getState()>1)
				if(game.isMonster(p.getName()) || game.getPlayerState(p.getName())>Game.dragonMin) {
					event.setCancelled(true);
				}
		}
	}
}
