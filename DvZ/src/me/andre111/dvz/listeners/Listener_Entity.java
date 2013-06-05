package me.andre111.dvz.listeners;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.Game;
import me.andre111.dvz.Spellcontroller;
import me.andre111.dvz.StatManager;
import me.andre111.dvz.dwarf.CustomDwarf;
import me.andre111.dvz.item.spell.ItemLaunch;
import me.andre111.dvz.monster.CustomMonster;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityCreatePortalEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

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
			
			Game game = plugin.getPlayerGame(player.getName());
			if (game!=null) {
				//Monster Droppen nix
				if (game.isMonster(player.getName())) {
					event.getDrops().clear();
				}
			}
		}
	}
	
	@EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
		if (event.isCancelled()) return;
		
		if (event.getCause() == DamageCause.FALL && event.getEntity() instanceof Player && Spellcontroller.jumpingNormal.contains((Player)event.getEntity())) {
			event.setCancelled(true);
			Spellcontroller.jumpingNormal.remove((Player)event.getEntity());
			if(Spellcontroller.jumping.contains((Player)event.getEntity())) {
				Spellcontroller.jumping.remove((Player)event.getEntity());
				Spellcontroller.spellIronGolemLand((Player)event.getEntity());
			}
		}
		//disabled monster damage
		if (event.isCancelled()) return;
		if(event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			Game game = plugin.getPlayerGame(player.getName());
			if(game!=null) {
				if(game.isMonster(player.getName()) || game.isDwarf(player.getName())) {
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
						if(game.isMonster(player.getName())) {
							int pid = game.getPlayerState(player.getName()) - Game.monsterMin;
							CustomMonster cm = DvZ.monsterManager.getMonster(pid);
							
							if(cm.isDamageDisabled(damage)) {
								event.setCancelled(true);
							}
						} else if(game.isDwarf(player.getName())) {
							int pid = game.getPlayerState(player.getName()) - Game.dwarfMin;
							CustomDwarf cd = DvZ.dwarfManager.getDwarf(pid);
							
							if(cd != null && cd.isDamageDisabled(damage)) {
								event.setCancelled(true);
							}
						}
					}
				}
			}
		}
    }
	
	@EventHandler(priority=EventPriority.LOWEST)
    public void onEntityDamageEntity(EntityDamageByEntityEvent event) {
		if (event.isCancelled()) return;

		if(event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
			//disable friendly fire
			Game game = plugin.getPlayerGame(((Player)event.getEntity()).getName());
			if (game!=null) {
				if (!plugin.getConfig().getString("friendly_fire","true").equals("true")) {
					String player = ((Player)event.getEntity()).getName();
					String damager = ((Player)event.getDamager()).getName();
					if((game.isDwarf(player) && game.isDwarf(damager)) ||
					   (game.isMonster(player) && game.isMonster(damager))) {
						event.setCancelled(true);
						return;
					}
				}
			}
			//spawn no pvp
			Player p = ((Player)event.getEntity());
			if (plugin.getConfig().getInt("spawn_nopvp",0)>0 && p.getLocation().distanceSquared(p.getWorld().getSpawnLocation())<=plugin.getConfig().getInt("spawn_nopvp",0)*plugin.getConfig().getInt("spawn_nopvp",0)) {
				event.setCancelled(true);
			}
			if (plugin.getConfig().getInt("spawn_nopvp",0)>0 && p.getLocation().getWorld()==Bukkit.getServer().getWorld(plugin.getConfig().getString("world_prefix", "DvZ_")+"Lobby")) {
				event.setCancelled(true);
			}
		}
		//bows
		if(event.getEntity() instanceof Player && event.getDamager() instanceof Arrow) {
			if(((Arrow)event.getDamager()).getShooter() instanceof Player) {
				//disable friendly fire
				Game game = plugin.getPlayerGame(((Player)event.getEntity()).getName());
				if (game!=null) {
					if (!plugin.getConfig().getString("friendly_fire","true").equals("true")) {
						String player = ((Player)event.getEntity()).getName();
						String damager = ((Player)((Arrow)event.getDamager()).getShooter()).getName();
						if((game.isDwarf(player) && game.isDwarf(damager)) ||
								   (game.isMonster(player) && game.isMonster(damager))) {
							event.setCancelled(true);
							return;
						}
					}
				}
				//spawn no pvp
				Player p = ((Player)event.getEntity());
				if (plugin.getConfig().getInt("spawn_nopvp",0)>0 && p.getLocation().distanceSquared(p.getWorld().getSpawnLocation())<=plugin.getConfig().getInt("spawn_nopvp",0)*plugin.getConfig().getInt("spawn_nopvp",0)) {
					event.setCancelled(true);
				}
				if (plugin.getConfig().getInt("spawn_nopvp",0)>0 && p.getLocation().getWorld()==Bukkit.getServer().getWorld(plugin.getConfig().getString("world_prefix", "DvZ_")+"Lobby")) {
					event.setCancelled(true);
				}
			}
		}
		
		if(event.isCancelled()) return;
		
		//IronGolem more damage/buffed monsters
		//irongolem deaktiviert, da custom monster existieren
		if(event.getDamager() instanceof Player) {
			Player dgm = (Player) event.getDamager();
			Game game = plugin.getPlayerGame(dgm.getName());
			if (game!=null) {
				if(game.isPlayer(dgm.getName())) {
					if(/*game.getPlayerState(dgm.getName())==35 ||*/ game.isBuffed(dgm.getName())) {
						event.setDamage(event.getDamage()*5);
					}
					//custom dwarf
					if(game.isDwarf(dgm.getName())) {
						int id = game.getPlayerState(dgm.getName()) - Game.dwarfMin;
						event.setDamage((int) Math.round(event.getDamage()*DvZ.dwarfManager.getDwarf(id).getDamageBuff()));
					}
					//custom monster
					if(game.isMonster(dgm.getName())) {
						int id = game.getPlayerState(dgm.getName()) - Game.monsterMin;
						event.setDamage((int) Math.round(event.getDamage()*DvZ.monsterManager.getMonster(id).getDamageBuff()));
					}
				}
			}
		}
		
		if((event.getDamager() instanceof Snowball)) {
			//Hurting Snowballs
			if (event.getDamager().getFallDistance() == Spellcontroller.identifier) {
				event.setDamage(Spellcontroller.sdamage);
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
	    
	    Game game = plugin.getPlayerGame(damager.getName());
	    if (game!=null) {
	    	if(game.isDwarf(player.getName()) && game.getPlayerState(damager.getName())==Game.assasinState) {
	    		game.setCountdown(damager.getName(), 3, -1);
	    	}
	    }
	}
	
	//Spielerteleports zur Lobby
	@EventHandler
	public void onEntityTeleport(EntityTeleportEvent event) {
		if(event.getEntityType()==EntityType.PLAYER) {
			World w = event.getTo().getWorld();
			World fw = event.getFrom().getWorld();
			World lobby = Bukkit.getServer().getWorld(plugin.getConfig().getString("world_prefix", "DvZ_")+"Lobby");
			World main = Bukkit.getServer().getWorld(plugin.getConfig().getString("world_prefix", "DvZ_")+"Main");
			
			if(w==lobby && fw!=main) {
				//TODO - disable to toquestion wich game to join
				//plugin.getGame(0).addPlayer(((Player)event.getEntity()).getName());
				//((Player)event.getEntity()).sendMessage(plugin.getLanguage().getString("string_self_added", "You have been added to the game!"));
			}
		}
	}
	
	//Potionsnowballs
	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event) {
		if((event.getEntity() instanceof Snowball)) {
			//Potions Snowballs
			if (event.getEntity().getFallDistance() == Spellcontroller.identifierp1) {
				Spellcontroller.spellPotion(event.getEntity().getLocation(), 1, 3);
			}
			if (event.getEntity().getFallDistance() == Spellcontroller.identifierp2) {
				Spellcontroller.spellPotion(event.getEntity().getLocation(), 2, 3);
			}
			if (event.getEntity().getFallDistance() == Spellcontroller.identifierp3) {
				Spellcontroller.spellPotion(event.getEntity().getLocation(), 3, 3);
			}
			if (event.getEntity().getFallDistance() == Spellcontroller.identifierp4) {
				Spellcontroller.spellPotion(event.getEntity().getLocation(), 4, 3);
			}
			if (event.getEntity().getFallDistance() == Spellcontroller.identifierp5) {
				Spellcontroller.spellPotion(event.getEntity().getLocation(), 5, 3);
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
	
	//fallingsand
	@EventHandler
	public void onEntityChangeBlock(EntityChangeBlockEvent event) {
		if(event.getEntityType()==EntityType.FALLING_BLOCK) {
			FallingBlock entity = (FallingBlock) event.getEntity();
			//falling sand hit
			if(entity.hasMetadata("dvz_falling_casting")) {
				ItemLaunch il = (ItemLaunch) entity.getMetadata("dvz_falling_casting").get(0).value();
				int gID = entity.getMetadata("dvz_falling_gameId").get(0).asInt();
				String playern = entity.getMetadata("dvz_falling_playername").get(0).asString();
				
				il.onHit(plugin.getGame(gID), Bukkit.getServer().getPlayerExact(playern), event.getBlock());
			}
			//disable blocks from fallingsand
			if(entity.hasMetadata("dvz_falling_noblock")) {
				event.setCancelled(true);
			}
		}
	}
	
	//update upcounters
	@EventHandler(priority=EventPriority.MONITOR)
	public void onEntityDamageEntityMonitor(EntityDamageByEntityEvent event) {
		if (event.isCancelled()) return;
		if(!(event.getEntity() instanceof Player)) return;

		StatManager.interruptDamage(((Player) event.getEntity()).getName());
	}
}
