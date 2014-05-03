package me.andre111.dvz.listeners;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.Game;
import me.andre111.dvz.config.ConfigManager;
import me.andre111.dvz.customclass.CustomClass;
import me.andre111.dvz.disguise.DisguiseSystemHandler;
import me.andre111.dvz.manager.BlockManager;
import me.andre111.dvz.manager.HighscoreManager;
import me.andre111.dvz.manager.PlayerScore;
import me.andre111.dvz.manager.StatManager;
import me.andre111.dvz.players.SpecialPlayer;
import me.andre111.dvz.teams.Team;
import me.andre111.dvz.utils.InventoryHandler;
import me.andre111.dvz.utils.PlayerHandler;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;


public class Listener_Player implements Listener  {
	private DvZ plugin;

	public Listener_Player(DvZ plugin){
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		//respawn fake zombies
		DvZ.item3DHandler.respawnAll(player);
		
		//notify updates update
		if (ConfigManager.getStaticConfig().getString("updateCheck", "true").equals("true") && player.isOp()) {
			//TODO - write with new Method: http://wiki.bukkit.org/ServerMods_API
			//plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new DvZUpdateNotifier(plugin, player));
		}
		
		//if the player is not in the game->ignore
		if(event.getPlayer().getLocation().getWorld()==Bukkit.getServer().getWorld(plugin.getConfig().getString("world_prefix", "DvZ_")+"Lobby") && (plugin.getPlayerGame(event.getPlayer().getUniqueId())==null))
			event.getPlayer().teleport(Bukkit.getServer().getWorlds().get(0).getSpawnLocation());
		
		if (plugin.getPlayerGame(player.getUniqueId())!=null
				&& ConfigManager.getStaticConfig().getString("hide_join_leave", "false").equals("true")) {
			event.setJoinMessage("");
		}
		
		if (plugin.getPlayerGame(player.getUniqueId())==null && ConfigManager.getStaticConfig().getString("autojoin_on_join", "true").equals("true")) {
			DvZ.sendPlayerMessageFormated(player, ConfigManager.getLanguage().getString("string_motd","Welcome to this §1DvZ§f Server!"));
			DvZ.sendPlayerMessageFormated(player, "--------------------------------");
			if(ConfigManager.getStaticConfig().getString("show_andre111_tag", "true").equals("true"))
				DvZ.sendPlayerMessageFormated(player, "Plugin by andre111");
			
			event.setJoinMessage(ConfigManager.getLanguage().getString("string_welcome","Welcome -0- to the Game!").replace("-0-", player.getDisplayName()));
			
			plugin.joinGame(player, true);
		} else if (plugin.getPlayerGame(player.getUniqueId())!=null) {
			int pstate = plugin.getPlayerGame(player.getUniqueId()).getPlayerState(player.getUniqueId());
			//redisguise
			CustomClass cm = DvZ.classManager.getClass(pstate-Game.classMin);
			if(cm!=null && cm.getDisguise()!=null && !cm.getDisguise().equals("")) {
				DisguiseSystemHandler.disguiseP(player, cm.getDisguise());
				DvZ.sendPlayerMessageFormated(player, ConfigManager.getLanguage().getString("string_redisguise","Redisguised you as a -0-!").replace("-0-", cm.getName()));
			}
			//player leave during start
			if(pstate==1 && plugin.getPlayerGame(player.getUniqueId()).isRunning()) {
				plugin.joinGame(player, plugin.getPlayerGame(player.getUniqueId()), true);
			}
		}
	}
	
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		
		if (plugin.getPlayerGame(player.getUniqueId())!=null
			&& ConfigManager.getStaticConfig().getString("hide_join_leave", "false").equals("true")) {
			event.setQuitMessage("");
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Game game = plugin.getPlayerGame(player.getUniqueId());
		
		if (game!=null) {
			//disable rightclick items during class selection
			if(game.getPlayerState(player.getUniqueId())==Game.pickClass) {
				event.setCancelled(true);
			}
			
			BlockManager.onInteract(event);
			
			Action action = event.getAction();
			ItemStack item = event.getItem();
			if (action==Action.RIGHT_CLICK_AIR) {
				game.playerRC(event, player, item, null);
			}
			else if(action==Action.RIGHT_CLICK_BLOCK) {
				game.playerRC(event, player, item, event.getClickedBlock());
			}
			else if(action==Action.LEFT_CLICK_AIR) {
				game.playerLC(player, item, null);
			}
			else if(action==Action.LEFT_CLICK_BLOCK) {
				game.playerLC(player, item, event.getClickedBlock());
				
				//"breaking" fire
				Block relative = event.getClickedBlock().getRelative(event.getBlockFace());
				if(relative.getType()==Material.FIRE) {
					game.playerBreakBlock(player, relative);
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();
		Game game = plugin.getPlayerGame(player.getUniqueId());
		
		if (game!=null) {
			Entity entity = event.getRightClicked();
			ItemStack item = event.getPlayer().getItemInHand();
			if(entity instanceof Player) {
				game.playerRCPlayer(player, item, (Player)entity);
			}
			
			//disable rightclick items during class selection
			if(game.getPlayerState(player.getUniqueId())==Game.pickClass) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
		Player player = event.getPlayer();
		Game game = plugin.getPlayerGame(player.getUniqueId());

		if (game!=null) {
			//disable rightclick items during class selection
			if(game.getPlayerState(player.getUniqueId())==Game.pickClass) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onPlayerPickUpItem(PlayerPickupItemEvent event) {
		//disable picking up Items flagged for only one player
		if (!event.getItem().getMetadata("dvz_onlyPickup").isEmpty()) {
			String pname = event.getItem().getMetadata("dvz_onlyPickup").get(0).asString();
			if (!pname.equals(event.getPlayer().getUniqueId().toString())) {
				event.setCancelled(true);
				return;
			}
		}
		
		Player player = event.getPlayer();
		Game game = plugin.getPlayerGame(player.getUniqueId());
		if (game!=null) {
			if(game.getPlayerState(player.getUniqueId())<4) {
				event.setCancelled(true);
				return;
			}
			//monster können nichts aufheben
			if(!game.getTeam(player.getUniqueId()).isCanPickupItems()) {
				event.setCancelled(true);
				return;
			}
		}
	}
	
	@EventHandler
	public void onPlayerItemDrop(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		Game game = plugin.getPlayerGame(player.getUniqueId());
		if (game!=null) {
			if(game.getPlayerState(player.getUniqueId())<4 && !player.isOp()) {
				event.setCancelled(true);
				return;
			}
			//monster können nichts droppen
			if(!game.getTeam(player.getUniqueId()).isCanDropItems() && !player.isOp()) {
				event.setCancelled(true);
				return;
			}
		}
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		final UUID puuid = player.getUniqueId();
		final Game game = plugin.getPlayerGame(player.getUniqueId());
		
		if (game!=null) {
			//Redisguise no longer needed->disguiscraft fix
			//game.redisguisePlayers();

			for(PotionEffect pet : player.getActivePotionEffects()) {
				player.removePotionEffect(pet.getType());
			}
																	//fix for getting killed when chosing monsterclass
			if(game.isPlayer(puuid)) {
				//deaths
				//TODO - readd deathcounter for assasins to not spawn
				/*if(game.isDwarf(puuid, true)) {
					game.deaths++;
				}*/
				
				//respawn at spawnpoint of dvzworld
				World w = Bukkit.getServer().getWorld(plugin.getConfig().getString("world_prefix", "DvZ_")+"Main"+plugin.getGameID(game)+"");
				final Location loc = game.getTeam(puuid).getSpawnLocation(w);
				event.setRespawnLocation(loc);
				
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					public void run() {
						Player player = PlayerHandler.getPlayerFromUUID(puuid);
						
						if(player!=null) {
							player.teleport(loc);
						}
					}
				}, 1);
				
				game.setPlayerState(puuid, Game.pickClass);
				game.setPlayerTeam(puuid, game.getTeam(puuid).getRespawnTeam());

				DvZ.sendPlayerMessageFormated(player, ConfigManager.getLanguage().getString("string_choose","Choose your class!"));
	
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					public void run() {
						Player player = PlayerHandler.getPlayerFromUUID(puuid);
						
						if(player!=null) {
							InventoryHandler.clearInv(player, false);
						
							game.addClassItems(player);
							
							DisguiseSystemHandler.undisguiseP(player);
						}
					}
				}, 2);
				
			}
		}
	}
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		if(event.isCancelled()) return;
		
		Player player = event.getPlayer();
		Game game = plugin.getPlayerGame(player.getUniqueId());
		
		if (game!=null) {
			//default
			//event.setFormat("§r<%1$s> %2$s");
			
			String prefix = "";
			String suffix = "";
			//normal classes
			int pstate = game.getPlayerState(player.getUniqueId());
			if(pstate>=Game.classMin && pstate<=Game.classMax) {
				int did = pstate - Game.classMin;
				CustomClass cd = DvZ.classManager.getClass(did);

				prefix = cd.getPrefix();
				suffix = cd.getSuffix();
			}
			if(pstate==Game.assasinState) {
				prefix = ConfigManager.getClassFile().getString("assassin_prefix", "");
				suffix = ConfigManager.getClassFile().getString("assassin_suffix", " the Assassin");
			}
			/*if(game.isMonster(player.getUniqueId())) {
				int did = pstate - Game.monsterMin;
				
				prefix = DvZ.monsterManager.getMonster(did).getPrefix();
				suffix = DvZ.monsterManager.getMonster(did).getSuffix();
			}*/
			//player specific
			if(DvZ.playerManager.getPlayer(player.getUniqueId())!=null) {
				SpecialPlayer sp = DvZ.playerManager.getPlayer(player.getUniqueId());
				
				if(!sp.getPrefix().equals("")) {
					prefix = sp.getPrefix();
				}
				if(!sp.getSuffix().equals("")) {
					suffix = sp.getSuffix();
				}
			}
			if(player.getName().equals("andre111") && ConfigManager.getStaticConfig().getString("show_andre111_tag", "true").equals("true")) {
				prefix = "";
				suffix = " the Plugin Author";
			}
			event.setFormat("§r<"+prefix+"%1$s"+suffix+"> %2$s");
			
			//game dedicated chat
			if(plugin.getConfig().getString("dedicated_chat", "true").equals("true")) {
				Set<Player> playerset = event.getRecipients();
				Iterator<Player> playerit = playerset.iterator();
				try{
					while(playerit.hasNext()) {
						Player p = playerit.next();
						if(!game.isPlayer(p.getUniqueId()) && !player.isOp()) {
							playerit.remove();
						}
					}
				} catch(ConcurrentModificationException e) {
				} catch(UnsupportedOperationException e) {
				}
			}
			//
		}
	}
	
	@EventHandler
	public void onPlayerPortal(PlayerPortalEvent event) {
		if (event.isCancelled()) return;
		
		event.setCancelled(plugin.getLobby().portalEvent(event));
	}
	
	@EventHandler
    public void onPLayerDeath(PlayerDeathEvent event)
    {
		Player p = event.getEntity();
		//Score/Stats
		if(plugin.getPlayerGame(p.getUniqueId())!=null && plugin.getPlayerGame(p.getUniqueId()).isRunning()) {
			PlayerScore pscore = HighscoreManager.getPlayerScore(p.getUniqueId());
			pscore.setDeaths(pscore.getDeaths()+1);
		}
		
		//is killer a player?
		if(event.getEntity().getKiller()==null) return;
		Player k;
		if(event.getEntity().getKiller() instanceof Player) {
			k = (Player)event.getEntity().getKiller();
		} else {
			return;
		}
		
		
		Game game = plugin.getPlayerGame(p.getUniqueId());
		if(game!=null) {
			//Score/Stats
			if(plugin.getPlayerGame(k.getUniqueId())!=null && plugin.getPlayerGame(k.getUniqueId()).isRunning()) {
				PlayerScore pscore = HighscoreManager.getPlayerScore(k.getUniqueId());
				pscore.setKills(pscore.getKills()+1);
			}
			
			Team team = game.getTeam(p.getUniqueId());
			//Team kteam = game.getTeam(k.getUniqueId());
			
			team.getEffectManager().killedPlayer(game, p);
			
			//Is dwarv
			/*if (game.isDwarf(p.getUniqueId(), true)) {
				//Is killer monster
				if (game.isMonster(k.getUniqueId())) {*/
					if (plugin.getConfig().getString("change_death_message", "true").equals("true") && team.isHideKills()) {
						event.setDeathMessage(ChatColor.YELLOW+ConfigManager.getLanguage().getString("string_chat_death", "-0- was killed by -1-!").replace("-0-", p.getName()).replace("-1-", team.getDisplayName()));
					}
				}
			//is Monster
			//} else if(game.isMonster(p.getUniqueId())) {
				//Is killer dwarv
				//if (game.isDwarf(k.getUniqueId(), true)) {
					//Item stats
					//----
					//TODO - maybe change back to only monsters, but still supporting teams?
					if (plugin.getConfig().getString("item_stats", "true").equals("true")) {
						if (k.getItemInHand().getAmount() > 0) {
							ItemStack it = k.getItemInHand();
							ItemMeta im = it.getItemMeta();
							if(im.hasLore()) {
								String lore1 = im.getLore().get(0);
								if(lore1.startsWith(ConfigManager.getLanguage().getString("string_stats_kills","Kills")+": ")) {
									int count = Integer.parseInt(lore1.replace(ConfigManager.getLanguage().getString("string_stats_kills","Kills")+": ", ""));
									count += 1;
									ArrayList<String> li = new ArrayList<String>();
									li.add(ConfigManager.getLanguage().getString("string_stats_kills","Kills")+": "+count);
									im.setLore(li);
								}
							} else {
								ArrayList<String> li = new ArrayList<String>();
								li.add(ConfigManager.getLanguage().getString("string_stats_kills","Kills")+": "+1);
								im.setLore(li);
							}
							it.setItemMeta(im);
							k.setItemInHand(it);
							DvZ.updateInventory(k);
						}
					}
					//----
					
					//dwarf kill buffs
					//DvZ.effectManager.dwarfKilledMonster(game, k);
				//}
			//}
		//}
    }
	
	//Item Monster Damage listener maybe
	/* @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        Player p = null;
        if (event.getDamager() instanceof Player) {
            p = (Player) event.getDamager();
        } else if (event.getDamager() instanceof Arrow && ((Arrow) event.getDamager()).getShooter() instanceof Player) {
            p = (Player) ((Arrow) event.getDamager()).getShooter();
        }
        if (p != null) {
            if (p.getItemInHand().getAmount() > 0*/
	
	//Crafting
	@EventHandler
	public void onPrepareCraft(PrepareItemCraftEvent event) {
		Game game = null;
		List<HumanEntity> hList = event.getViewers();
		for(HumanEntity he : hList) {
			if(plugin.getPlayerGame(he.getUniqueId())!=null) {
				game = plugin.getPlayerGame(he.getUniqueId());
				break;
			}
		}
		
		if(game != null) {
			ItemStack result = event.getRecipe().getResult();
			
			if(ConfigManager.isCraftDisabled(result.getType(), game.getGameType())) {
				event.getInventory().setResult(new ItemStack(Material.AIR));
			}
		}
	}
	@EventHandler
	public void onCraft(CraftItemEvent event) {
		if(event.isCancelled()) return;
		
		Game game = null;
		List<HumanEntity> hList = event.getViewers();
		for(HumanEntity he : hList) {
			if(plugin.getPlayerGame(he.getUniqueId())!=null) {
				game = plugin.getPlayerGame(he.getUniqueId());
				break;
			}
		}
		
		if(game != null) {
			ItemStack result = event.getRecipe().getResult();
			
			if(ConfigManager.isCraftDisabled(result.getType(), game.getGameType())) {
				event.setCancelled(true);
			}
		}
	}
	
	//manasystem on sneak?
	@EventHandler
	public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
		if(event.isCancelled()) return;
		
		Player p  = event.getPlayer();
		Game game = plugin.getPlayerGame(p.getUniqueId());
		if(game==null) return;
		Team team = game.getTeam(p.getUniqueId());
		//don't spam packets when the monsters are not released->WaitingMEnu
		if(team!=null && !team.isReleased()) return;
		
		if(event.isSneaking()) {
			StatManager.show(p);
		} else {
			StatManager.hide(p, false);
		}
	}
	@EventHandler
	public void onPlayerInventoryClose(InventoryCloseEvent event) {
		Player p  = (Player) event.getPlayer();
		Game game = plugin.getPlayerGame(p.getUniqueId());
		if(game==null) return;
		Team team = game.getTeam(p.getUniqueId());
		//don't spam packets when the monsters are not released->WaitingMEnu
		if(team!=null && !team.isReleased()) return;
		
		StatManager.onInventoryClose(p);
	}
}
