package me.andre111.dvz.disguise;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.Game;
import me.andre111.dvz.customclass.CustomClass;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.DisguiseConfig;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.CreeperWatcher;
import me.libraryaddict.disguise.events.UndisguiseEvent;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class DSystem_LibsDisguises implements DSystem, Listener {
	private int nextID = Integer.MIN_VALUE;
	
	@Override
	public void initListeners(DvZ plugin) {
		DisguiseConfig.setHearSelfDisguise(true);
		
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@Override
	public void disguiseP(Player player, DvZDisguiseType disguise) {
		switch(disguise) {
		case VILLAGER_ZOMBIE:
			DisguiseAPI.disguiseToAll(player, new MobDisguise(DisguiseType.ZOMBIE_VILLAGER, !disguise.isBaby(), true));
			break;
		case WITHER_SKELETON:
			DisguiseAPI.disguiseToAll(player, new MobDisguise(DisguiseType.WITHER_SKELETON, !disguise.isBaby(), true));
			break;
		//----------
		//SPECIAL
		//----------
		//HORSES
		case UNDEAD_HORSE:
			DisguiseAPI.disguiseToAll(player, new MobDisguise(DisguiseType.UNDEAD_HORSE, !disguise.isBaby(), true));
			break;
		case SKELETON_HORSE:
			DisguiseAPI.disguiseToAll(player, new MobDisguise(DisguiseType.SKELETON_HORSE, !disguise.isBaby(), true));
			break;
		//CHARGED
		case CHARGED_CREEPER:
			MobDisguise mdis = new MobDisguise(DisguiseType.CREEPER, !disguise.isBaby(), true);
			((CreeperWatcher) mdis.getWatcher()).setPowered(true);
			DisguiseAPI.disguiseToAll(player, mdis);
			break;
		default:
			DisguiseAPI.disguiseToAll(player, new MobDisguise(DisguiseType.getType(disguise.getEntityType()), !disguise.isBaby(), true));
			break;
		}
	}
	@Override
	public void disguiseP(Player player, String disguise) {
		//EntityType et = EntityType.fromName(disguise);
		//if(et==null) et = EntityType.valueOf(disguise);
		EntityType et = EntityType.valueOf(disguise);
		if(et==null) {
			DvZ.log("Unknown Entity-Disguise: "+disguise);
			return;
		}
		
		DisguiseAPI.disguiseToAll(player, new MobDisguise(DisguiseType.getType(et), true, true));
	}

	@Override
	public void undisguiseP(Player player) {
		DisguiseAPI.undisguiseToAll(player);
	}

	@Override
	public void redisguiseP(Player player) {
		Disguise dis = DisguiseAPI.getDisguise(player);
		DisguiseAPI.undisguiseToAll(player);
		DisguiseAPI.disguiseToAll(player, dis);
	}

	@Override
	public int newEntityID() {
		return nextID++;
	}
	
	//TODO - maybe needs rightclick detection

	//TODO - check if this is really needed
	@EventHandler
	public void onPlayerUndisguise(UndisguiseEvent event) {
		Entity e = event.getEntity();
		if(!(e instanceof Player)) return;
		
		Player p = (Player) event.getEntity();
		Game game = DvZ.instance.getPlayerGame(p.getUniqueId());
		if(game!=null) {
			if(game.getState()>1) {
				CustomClass cc = game.getClass(p.getUniqueId());
				if(cc.getDisguise()!=null && !cc.getDisguise().equals("")) {
					event.setCancelled(true);
				}
			}
		}
	}
}
