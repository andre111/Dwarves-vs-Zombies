package me.andre111.dvz.disguise;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.Game;
import me.andre111.dvz.customclass.CustomClass;
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
	public void initListeners(DvZ p) {
		this.plugin = p;
		api = DisguiseCraft.getAPI();
		
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@Override
	public void disguiseP(Player player, DvZDisguiseType disguise) {
		DisguiseType dt = DisguiseType.fromString(disguise.getNames().get(0));
		Disguise dis = null;
		
		switch(disguise) {
		case ZOMBIE:
			dt = DisguiseType.Zombie;
			break;
		case VILLAGER_ZOMBIE:
			dt = DisguiseType.Zombie;
			dis = new Disguise(api.newEntityID(), "", dt);
			dis.setSingleData("infected");
			break;
		case SKELETON:
			dt = DisguiseType.Skeleton;
			break;
		case WITHER_SKELETON:
			dt = DisguiseType.Skeleton;
			dis = new Disguise(api.newEntityID(), "", dt);
			dis.setSingleData("wither");
			break;
		case CREEPER:
			dt = DisguiseType.Creeper;
			break;
		case SLIME:
			dt = DisguiseType.Slime;
			break;
		case WITCH:
			dt = DisguiseType.Witch;
			break;
		case BLAZE:
			dt = DisguiseType.Blaze;
			break;
		case GHAST:
			dt = DisguiseType.Ghast;
			break;
		case MAGMA_CUBE:
			dt = DisguiseType.MagmaCube;
			break;
		case SILVERFISH:
			dt = DisguiseType.Silverfish;
			break;
		//NEUTRAL
		case SPIDER:
			dt = DisguiseType.Spider;
			break;
		case CAVE_SPIDER:
			dt = DisguiseType.CaveSpider;
			break;
		case ENDERMAN:
			dt = DisguiseType.Enderman;
			break;
		case WOLF:
			dt = DisguiseType.Wolf;
			break;
		case ZOMBIE_PIGMAN:
			dt = DisguiseType.PigZombie;
			break;
		//TAMABLE
		case OCELOT:
			dt = DisguiseType.Ocelot;
			break;
		case HORSE:
			dt = DisguiseType.Horse;
			break;
		//PASSIVE
		case CHICKEN:
			dt = DisguiseType.Chicken;
			break;
		case COW:
			dt = DisguiseType.Cow;
			break;
		case PIG:
			dt = DisguiseType.Pig;
			break;
		case SHEEP:
			dt = DisguiseType.Sheep;
			break;
		case SQUID:
			dt = DisguiseType.Squid;
			break;
		case BAT:
			dt = DisguiseType.Bat;
			break;
		case VILLAGER:
			dt = DisguiseType.Villager;
			break;
		case MOOSHROOM:
			dt = DisguiseType.MushroomCow;
			break;
		//UTILITY
		case SNOW_GOLEM:
			dt = DisguiseType.Snowman;
			break;
		case IRON_GOLEM:
			dt = DisguiseType.IronGolem;
			break;
		//BOSSES
		case ENDER_DRAGON:
			dt = DisguiseType.EnderDragon;
			break;
		case WITHER:
			dt = DisguiseType.Wither;
			break;
		//UNUSED
		case GIANT:
			dt = DisguiseType.Giant;
			break;
		//----------
		//SPECIAL
		//----------
		//HORSES
		case UNDEAD_HORSE:
			dt = DisguiseType.Horse;
			dis = new Disguise(api.newEntityID(), "", dt);
			dis.setSingleData("undead");
			break;
		case SKELETON_HORSE:
			dt = DisguiseType.Horse;
			dis = new Disguise(api.newEntityID(), "", dt);
			dis.setSingleData("skeletal");
			break;
		//CHARGED
		case CHARGED_CREEPER:
			dt = DisguiseType.Creeper;
			dis = new Disguise(api.newEntityID(), "", dt);
			dis.setSingleData("charged");
			break;
		}
		
		if(dis==null) {
			dis = new Disguise(api.newEntityID(), "", dt);
		}
		if(disguise.isBaby()) {
			dis.addSingleData("baby");
		}
		disguiseP(player, dis);
	}
	@Override
	public void disguiseP(Player player, String disguise) {
		DisguiseType dt = DisguiseType.fromString(disguise);
		if(dt==null) {
			DvZ.log("Unknown Entity-Disguise: "+disguise);
		}
		disguiseP(player, new Disguise(api.newEntityID(), "", dt));
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
		if(plugin==null) {
			DvZ.log("Warning: DvZ-Plugin==null - Ignoring rightclick on disguise!");
			return;
		}
		
		Bukkit.getScheduler().runTask(plugin, new Runnable() {
			public void run() {
				Player player = event.getPlayer();
				if(player!=null) {
					Game game = plugin.getPlayerGame(player.getUniqueId());

					if (game!=null) {
						DisguiseCraft dc = (DisguiseCraft) Bukkit.getPluginManager().getPlugin("DisguiseCraft"); //TODO - maybe a better way of getting Disguisecraft(or when the API changes use it)

						Player target = dc.disguiseIDs.get(event.getTarget());
						ItemStack item = event.getPlayer().getItemInHand();

						//TODO - this should somehow be handled by the SpellItems Plugin
						SpellItems.playerSpecialItemC(player, item, 1, null, target);
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
		
		Game game = plugin.getPlayerGame(p.getUniqueId());
		if(game!=null) {
			if(game.isRunning()) {
				CustomClass cc = game.getClass(p.getUniqueId());
				if(cc.getDisguise()!=null && !cc.getDisguise().equals("")) {
					event.setCancelled(true);
				}
			}
		}
	}
}
