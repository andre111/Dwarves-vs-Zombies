package me.andre111.dvz.listeners;

import java.util.UUID;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.Game;
import me.andre111.dvz.customclass.CustomClass;
import me.andre111.dvz.teams.Team;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCreatePortalEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTeleportEvent;

public class Listener_Entity implements Listener {
	private DvZ plugin;

	public Listener_Entity(DvZ plugin){
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		if (event.getEntityType()==EntityType.PLAYER) {
			Player player = (Player)event.getEntity();

			Game game = plugin.getPlayerGame(player.getUniqueId());
			if (game!=null) {
				//Monster Droppen nix
				if (!game.getTeam(player.getUniqueId()).isDeathDropItems()) {
					event.getDrops().clear();
				}
			}
		}
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.isCancelled()) return;

		//disabled monster damage
		if (event.isCancelled()) return;
		if(event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			Game game = plugin.getPlayerGame(player.getUniqueId());
			if(game!=null) {
				if(game.isPlayer(player.getUniqueId())) {
					String damage = "";

					if(event.getCause() == DamageCause.CONTACT) {
						damage = "contact";
					} else if(event.getCause() == DamageCause.DROWNING) {
						damage = "drown";
					} else if(event.getCause() == DamageCause.ENTITY_EXPLOSION || event.getCause() == DamageCause.BLOCK_EXPLOSION) {
						damage = "explosion";
					} else if(event.getCause() == DamageCause.FALL) {
						damage = "fall";
					} else if(event.getCause() == DamageCause.FIRE || event.getCause() == DamageCause.FIRE_TICK) {
						damage = "fire";
					} else if(event.getCause() == DamageCause.LAVA) {
						damage = "lava";
					} else if(event.getCause() == DamageCause.POISON) {
						damage = "poison";
					} else if(event.getCause() == DamageCause.STARVATION) {
						damage = "starve";
					} else if(event.getCause() == DamageCause.WITHER) {
						damage = "wither";
					}

					if(!damage.equals("")) {
						CustomClass cd = game.getClass(player.getUniqueId());

						if(cd != null && cd.isDamageDisabled(damage)) {
							event.setCancelled(true);
						}
					}
				}

				//graceperiode
				if(game.getTeam(player.getUniqueId()).isInvulnerable()) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(priority=EventPriority.LOWEST)
	public void onEntityDamageEntity(EntityDamageByEntityEvent event) {
		if (event.isCancelled()) return;

		if(event.getEntity() instanceof Player) {
			Player pplayer = (Player) event.getEntity();
			
			Player ddamager = null;
			if(event.getDamager() instanceof Player) {
				ddamager = (Player)event.getDamager();
			//bows, snowballs and more
			} else if(event.getDamager() instanceof Projectile && ((Projectile)event.getDamager()).getShooter() instanceof Player) {
				ddamager = (Player) ((Projectile)event.getDamager()).getShooter();
			}
			
			if(ddamager!=null) {
				//disable friendly fire
				Game game = plugin.getPlayerGame(pplayer.getUniqueId());
				if (game!=null) {
					if (!plugin.getConfig().getBoolean("friendly_fire", true)) {
						UUID playerUUID = pplayer.getUniqueId();
						UUID damagerUUID = ddamager.getUniqueId();

						Team pTeam = game.getTeam(playerUUID);
						Team dTeam = game.getTeam(damagerUUID);

						if(pTeam.isFriendly(dTeam) || (pTeam.getName().equals(dTeam.getName()) && !pTeam.isFriendlyFire() && !game.getPlayerState(damagerUUID).equals(Game.STATE_ASSASSIN) && !game.getPlayerState(playerUUID).equals(Game.STATE_ASSASSIN))) {
							event.setCancelled(true);
							return;
						}
					}
				}
				
				//TODO - this spawn no pvp seems a bit wiered and even breaking things? - is it needed?
				if (plugin.getConfig().getInt("spawn_nopvp",0)>0 && pplayer.getLocation().distanceSquared(pplayer.getWorld().getSpawnLocation())<=plugin.getConfig().getInt("spawn_nopvp",0)*plugin.getConfig().getInt("spawn_nopvp",0)) {
					event.setCancelled(true);
				}
				if (plugin.getConfig().getInt("spawn_nopvp",0)>0 && pplayer.getLocation().getWorld()==Bukkit.getServer().getWorld(plugin.getConfig().getString("world_prefix", "DvZ_")+"Lobby")) {
					event.setCancelled(true);
				}
			}
		}

		if(event.isCancelled()) return;

		//IronGolem more damage/buffed monsters
		//irongolem deaktiviert, da custom monster existieren
		if(event.getDamager() instanceof Player) {
			Player dgm = (Player) event.getDamager();
			Game game = plugin.getPlayerGame(dgm.getUniqueId());
			if (game!=null) {
				if(game.isPlayer(dgm.getUniqueId())) {
					if(/*game.getPlayerState(dgm.getUniqueId())==35 ||*/ game.isBuffed(dgm.getUniqueId())) {
						event.setDamage(event.getDamage()*5);
					}

					event.setDamage(event.getDamage()*game.getClass(dgm.getUniqueId()).getDamageBuff());

					//Dwarf kill effects
					event.setDamage(event.getDamage()*game.getTeam(dgm.getUniqueId()).getEffectManager().getKillMultiplier(game, dgm.getUniqueId()));
				}
			}
		}
	}

	//Assasin kills
	@EventHandler(priority = EventPriority.HIGH)
	public void onEntityDamage2(EntityDamageEvent e) {
		if(e.isCancelled()) return;
		if(!(e instanceof EntityDamageByEntityEvent))
			return;

		EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
		if(!(event.getDamager() instanceof Player))
			return;
		if(!(event.getEntity() instanceof Player))
			return;

		Player player = (Player) event.getEntity();
		Player damager = (Player) event.getDamager();

		//is kill?
		if(e.getDamage()<player.getHealth())
			return;

		Game game = plugin.getPlayerGame(damager.getUniqueId());
		if (game!=null) {
			if(/*game.isDwarf(player.getUniqueId(), true) && */game.getPlayerState(damager.getUniqueId()).equals(Game.STATE_ASSASSIN)) {
				game.resetCustomCooldown(damager.getUniqueId(), "assassin_time");
			}
		}
	}

	//Spielerteleports zur Lobby
	@EventHandler
	public void onEntityTeleport(EntityTeleportEvent event) {
		if(event.getEntityType()==EntityType.PLAYER) {
			World w = event.getTo().getWorld();
			World fw = event.getFrom().getWorld();
			World lobby = Bukkit.getWorld(plugin.getConfig().getString("world_prefix", "DvZ_")+"Lobby");
			World main = Bukkit.getWorld(plugin.getConfig().getString("world_prefix", "DvZ_")+"Main");

			if(w==lobby && fw!=main) {
				//TODO - disable to toquestion wich game to join
				//plugin.getGame(0).addPlayer(((Player)event.getEntity()).getName());
				//DvZ.sendPlayerMessageFormated(((Player)event.getEntity()), plugin.getLanguage().getString("string_self_added", "You have been added to the game!"));
			}
		}
	}

	//Enderdragon - disable portals
	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityCreatePortalEvent(EntityCreatePortalEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof EnderDragon)
		{ 
			if (entity.getWorld().getEnvironment() != Environment.THE_END) event.setCancelled(true); 
		}
	}
}
