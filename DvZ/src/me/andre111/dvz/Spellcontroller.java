package me.andre111.dvz;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import me.andre111.dvz.utils.Animation;
import me.andre111.dvz.utils.ExperienceUtils;
import me.andre111.dvz.utils.ItemHandler;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Silverfish;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public class Spellcontroller {
	public static DvZ plugin;
	
	//Custom Dwarves
	public static void spellCustomDwarf(Game game, Player player, int cd) {
		if(game.getCountdown(player.getName(), 1)==0) {
			int id = DvZ.getClassFile().getInt("custom_d"+cd+"_spell_need", 0);
			int data = DvZ.getClassFile().getInt("custom_d"+cd+"_spell_needdata", 0);
			int count = DvZ.getClassFile().getInt("custom_d"+cd+"_spell_count", 0);
			
			if(countItems(player, id, data)>=count) {
				removeItems(player, id, data, count);
				game.setCountdown(player.getName(), 1, DvZ.getClassFile().getInt("custom_d"+cd+"_spell_time",0));
				
				player.giveExp(DvZ.getClassFile().getInt("custom_d"+cd+"_spell_exp",0));
				
				World w = player.getWorld();
				Location loc = player.getLocation();
				//Random rand = new Random();
				PlayerInventory inv = player.getInventory();
				
				String insinv = DvZ.getClassFile().getString("custom_d"+cd+"_spell_inv","true");
				
				List<String> itemstrings = DvZ.getClassFile().getStringList("custom_d"+cd+"_spell_get");
				for(int i=0; i<itemstrings.size(); i++) {
					ItemStack it = ItemHandler.decodeItem(itemstrings.get(i));
					
					if(it!=null) {
						if(insinv=="true") {
							inv.addItem(it);
						} else {
							w.dropItem(loc, it);
						}
					}
				}
				
				DvZ.updateInventory(player);
			} else {
				player.sendMessage(DvZ.getClassFile().getString("custom_d"+cd+"_spell_fail",""));
			}
		} else {
			player.sendMessage(DvZ.getLanguage().getString("string_wait","You have to wait -0- Seconds!").replace("-0-", ""+game.getCountdown(player.getName(), 1)));
		}
	}
	
	public static float identifierp1 = (float)Math.random() * 20F;
	public static float identifierp2 = (float)Math.random() * 20F;
	public static float identifierp3 = (float)Math.random() * 20F;
	public static float identifierp4 = (float)Math.random() * 20F;
	public static float identifierp5 = (float)Math.random() * 20F;
	public static void spellLaunchPotion(Game game, Player player, int mdata) {
		if(ExperienceUtils.getCurrentExp(player)>=plugin.getConfig().getInt("dwarf_potion_exp", 2)) {
			float dist = 0;
			int id = 0;
			switch(mdata) {
			case 8421: { dist = identifierp1; id = 1; break; }
			case 11449: { dist = identifierp2; id = 2; break; }
			case 16274: { dist = identifierp3; id = 3; break; }
			case 16310: { dist = identifierp4; id = 4; break; }
			case 8259: { dist = identifierp5; id = 5; break; }
			default: break;
			}
			if(dist!=0) {
				ExperienceUtils.changeExp(player, -plugin.getConfig().getInt("dwarf_potion_exp", 2));
				Snowball sb = player.launchProjectile(Snowball.class);
				sb.setFallDistance(dist);
				spellPotion(player.getLocation(), id, 1);
			}
		} else {
			player.sendMessage(DvZ.getLanguage().getString("string_needexp","You don't have enough exp!"));
		}
	}
	
	public static void spellPotion(Location loc, int id, int range) {
	    PotionEffect pf = new PotionEffect(PotionEffectType.HEAL, 1, 0);
	    int data = 0;
	    switch(id) {
        case 1: { data = 8421; pf = new PotionEffect(PotionEffectType.HEAL, 2, 1); break; }
        case 2: { data = 11449; pf = new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*120, 1); break; }
        case 3: { data = 16274; pf = new PotionEffect(PotionEffectType.SPEED, 20*120, 0); break; }
        case 4: { data = 16310; pf = new PotionEffect(PotionEffectType.NIGHT_VISION, 20*120, 1); break; }
        case 5: { data = 8259; pf = new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20*120, 0); break; }
        default: break;
	    }
		for(Player p : loc.getWorld().getPlayers()) {
	        Location playerLocation = p.getLocation();
	       
	        if(loc.distance(playerLocation) <= range) {
	        	p.addPotionEffect(pf);
	        	p.playEffect(p.getLocation(), Effect.POTION_BREAK, data);
	        }
	    }
	}
	
	public static void spellDisablePortal(Game game, Player player) {
		if(game.getCountdown(player.getName(), 4)==0) {
			game.setCountdown(player.getName(), 4, plugin.getConfig().getInt("spelltime_disableportal",3));
			
			if(game.enderPortal!=null) {
				game.enderActive = false;
				
				game.broadcastMessage(DvZ.getLanguage().getString("string_portal_disable","The Portal has been disabled!"));
			} else {
				game.setCountdown(player.getName(), 4, 0);
				player.sendMessage(DvZ.getLanguage().getString("string_teleport_noportal","The Enderman Portal does not exist yet!"));
			}
		} else {
			player.sendMessage(DvZ.getLanguage().getString("string_wait","You have to wait -0- Seconds!").replace("-0-", ""+game.getCountdown(player.getName(), 4)));
		}
	}
	
	public static void spellEnderChest(Game game, Player player, Inventory chest, Inventory groupchest) {
		if(plugin.getConfig().getString("crystal_storage", "0").equals("1")) {
			player.openInventory(chest);
			player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20*3, 0));
			player.getWorld().playSound(player.getLocation(), Sound.CHEST_OPEN, 1, 1);
		} else if(plugin.getConfig().getString("crystal_storage", "0").equals("2")) {
			player.openInventory(groupchest);
			player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20*3, 0));
			player.getWorld().playSound(player.getLocation(), Sound.CHEST_OPEN, 1, 1);
		}
	}
	
	//###################################
	//Monster
	//###################################
	public static void spellCreeper(Game game, Player player) {
		if(game.getCountdown(player.getName(), 1)==0) {
			game.setCountdown(player.getName(), 1, plugin.getConfig().getInt("spelltime_creeper",10));
			
			World w = player.getWorld();
			Location loc = player.getLocation();
			
			player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
			player.damage(10000);
			
			w.createExplosion(loc, 6);
			w.createExplosion(loc, 6);
		} else {
			player.sendMessage(DvZ.getLanguage().getString("string_wait","You have to wait -0- Seconds!").replace("-0-", ""+game.getCountdown(player.getName(), 1)));
		}
	}
	
	public static void spellSpiderBite1(Game game, Player player, Player target) {
		if(game.getCountdown(player.getName(), 1)==0) {
			game.setCountdown(player.getName(), 1, plugin.getConfig().getInt("spelltime_spiderbite1",0));
			
			target.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 300, 0));
			target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 300, 0));
		} else {
			player.sendMessage(DvZ.getLanguage().getString("string_wait","You have to wait -0- Seconds!").replace("-0-", ""+game.getCountdown(player.getName(), 1)));
		}
	}
	
	public static void spellSpiderBite2(Game game, Player player, Player target) {
		if(game.getCountdown(player.getName(), 2)==0) {
			game.setCountdown(player.getName(), 2, plugin.getConfig().getInt("spelltime_spiderbite2",0));
			
			target.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 60, 4));
		} else {
			player.sendMessage(DvZ.getLanguage().getString("string_wait","You have to wait -0- Seconds!").replace("-0-", ""+game.getCountdown(player.getName(), 2)));
		}
	}
	
	public static void spellIronGolemSmash(Game game, Player player, Block block) {
		if(game.getCountdown(player.getName(), 1)==0) {
			game.setCountdown(player.getName(), 1, plugin.getConfig().getInt("spelltime_ironsmash",1));
			
			World w = block.getWorld();
			Location loc = block.getLocation();
			w.createExplosion(loc, 2);
			w.playSound(loc, Sound.IRONGOLEM_THROW, 1, 1);
		} else {
			player.sendMessage(DvZ.getLanguage().getString("string_wait","You have to wait -0- Seconds!").replace("-0-", ""+game.getCountdown(player.getName(), 1)));
		}
	}
	
	//rest in Listener_Entity onEntityDamaged
	private static double forwardVelocity = 40 / 10D;
    private static double upwardVelocity = 15 / 10D;
    public final static HashSet<Player> jumping = new HashSet<Player>();
	public static void spellIronGolemLeap(Game game, Player player) {
		if(game.getCountdown(player.getName(), 2)==0) {
			game.setCountdown(player.getName(), 2, plugin.getConfig().getInt("spelltime_ironjump",30));
			
            spellLeap(player, forwardVelocity, upwardVelocity, 1, true);
            jumping.add(player);
		} else {
			player.sendMessage(DvZ.getLanguage().getString("string_wait","You have to wait -0- Seconds!").replace("-0-", ""+game.getCountdown(player.getName(), 2)));
		}
	}
	
	public static void spellIronGolemLand(Player player) {
		World w = player.getWorld();
		Location loc = player.getLocation();
		w.createExplosion(loc, 0);
		Game game = plugin.getPlayerGame(player.getName());
		if (game!=null)
				game.broadcastMessage(DvZ.getLanguage().getString("string_iron_near","An Iron Golem is nearby!"));
	}
	
	public final static HashSet<Player> jumpingNormal = new HashSet<Player>();
	public static void spellLeap(Player player, double forward, double upward, float power, boolean diasableDamage) {
		Vector v = player.getLocation().getDirection();
        v.setY(0).normalize().multiply(forward*power).setY(upward*power);
        player.setVelocity(v);
        if(diasableDamage)
        	jumpingNormal.add(player);
	}
	
	public static void spellSnowGolemSnow(Game game, Player player) {
		if(game.getCountdown(player.getName(), 1)==0) {
			game.setCountdown(player.getName(), 1, plugin.getConfig().getInt("spelltime_snowgolem",60));
			
			Random rand = new Random();
			PlayerInventory inv = player.getInventory();
			for(int i=0; i<20; i++) {
				inv.addItem(new ItemStack(332, rand.nextInt(9)+8));
			}
			player.sendMessage(DvZ.getLanguage().getString("string_got_snow","You got some Snowballs"));
			
			DvZ.updateInventory(player);
		} else {
			player.sendMessage(DvZ.getLanguage().getString("string_wait","You have to wait -0- Seconds!").replace("-0-", ""+game.getCountdown(player.getName(), 1)));
		}
	}
	
	//rest in Listener_Entity onEntitydamagedEntity
	public static float identifier = (float)Math.random() * 20F;
	public static int sdamage = 8;
	public static void spellSnowGolemThrow(Game game, Player player) {
		if(game.getCountdown(player.getName(), 2)==0) {
			if(countItems(player, 332, 0)>=96) {
				removeItems(player, 332, 0, 96);
				game.setCountdown(player.getName(), 2, plugin.getConfig().getInt("spelltime_snowgolemthrow",0));
			
				 Random rand = new Random();
                 Vector mod;
                 for (int i = 0; i < 250; i++) {
                	 Snowball snowball = player.launchProjectile(Snowball.class);
                	 snowball.setFallDistance(identifier); // tag the snowballs
                	 mod = new Vector((rand.nextDouble() - .5) * 15 / 10.0, (rand.nextDouble() - .5) * 5 / 10.0, (rand.nextDouble() - .5) * 15 / 10.0);
                	 snowball.setVelocity(snowball.getVelocity().add(mod));
                 }
			} else {
				player.sendMessage(DvZ.getLanguage().getString("string_need_snow","You need 96 Snowballs!"));
			}
		} else {
			player.sendMessage(DvZ.getLanguage().getString("string_wait","You have to wait -0- Seconds!").replace("-0-", ""+game.getCountdown(player.getName(), 2)));
		}
	}
	
	public static void spellBroodLay(Game game, Player player) {
		if(game.getCountdown(player.getName(), 1)==0) {
			if(countItems(player, 383, 0)>=1) {
				removeItems(player, 383, 0, 1);
				game.setCountdown(player.getName(), 1, plugin.getConfig().getInt("spelltime_broodlay",3));
				
				World w = player.getWorld();
				Location loc = player.getLocation();
				int x = loc.getBlockX();
				int y = loc.getBlockY();
				int z = loc.getBlockZ();
				for(int xx=-1; xx<=1; xx++) {
					for(int yy=-1; yy<=1; yy++) {
						for(int zz=-1; zz<=1; zz++) {
							Block block = w.getBlockAt(x+xx, y+yy, z+zz);
							int bid = block.getTypeId();
							if(bid==1 || bid==4 || bid==98) {
								block.setTypeId(97);
							}
						}
					}
				}
				
				game.broadcastMessage(DvZ.getLanguage().getString("string_brood_lay","A Broodmother is laying her eggs!"));
			} else {
				player.sendMessage(DvZ.getLanguage().getString("string_need_egg","You need an Egg to Infect!"));
			}
		} else {
			player.sendMessage(DvZ.getLanguage().getString("string_wait","You have to wait -0- Seconds!").replace("-0-", ""+game.getCountdown(player.getName(), 1)));
		}
	}
	
	public static void spellBroodRoar(Game game, Player player) {
		if(game.getCountdown(player.getName(), 2)==0) {
			game.setCountdown(player.getName(), 2, plugin.getConfig().getInt("spelltime_broodroar",3));
			
			int range = Math.round(15);
            List<Entity> entities = player.getNearbyEntities(range, range, range);
            for (Entity e : entities) {
            	if (e instanceof Silverfish) {
            		((Silverfish)e).damage(0, player);
            	}
            }
			
            game.broadcastMessage(DvZ.getLanguage().getString("string_brood_roar","A Broodmother roars!"));
		} else {
			player.sendMessage(DvZ.getLanguage().getString("string_wait","You have to wait -0- Seconds!").replace("-0-", ""+game.getCountdown(player.getName(), 2)));
		}
	}
	
	public static void spellEnderBlink(Game game, Player player) {
		if(game.getCountdown(player.getName(), 1)==0) {
			game.setCountdown(player.getName(), 1, plugin.getConfig().getInt("spelltime_enderblink",18));
			
			int range = 75;
			BlockIterator iter; 
			try {
				iter = new BlockIterator(player, range>0&&range<150?range:150);
			} catch (IllegalStateException e) {
				iter = null;
			}
			Block prev = null;
			Block found = null;
			Block b;
			if (iter != null) {
				while (iter.hasNext()) {
					b = iter.next();
					if (DvZ.transparent.contains((byte)b.getTypeId())) {
						prev = b;
					} else {
						found = b;
						break;
					}
				}
			}

			if (found != null) {
				Location loc = null;
				if (range > 0 && !(found.getLocation().distanceSquared(player.getLocation()) < range*range)) {
				} else if (DvZ.isPathable(found.getRelative(0,1,0)) && DvZ.isPathable(found.getRelative(0,2,0))) {
					// try to stand on top
					loc = found.getLocation();
					loc.setY(loc.getY() + 1);
				} else if (prev != null && DvZ.isPathable(prev) && DvZ.isPathable(prev.getRelative(0,1,0))) {
					// no space on top, put adjacent instead
					loc = prev.getLocation();
				}
				if (loc != null) {
					loc.setX(loc.getX()+.5);
					loc.setZ(loc.getZ()+.5);
					loc.setPitch(player.getLocation().getPitch());
					loc.setYaw(player.getLocation().getYaw());
					player.getWorld().playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 1);
					player.getWorld().playEffect(player.getLocation(), Effect.ENDER_SIGNAL, 0);
					player.teleport(loc);
					player.getWorld().playEffect(player.getLocation(), Effect.ENDER_SIGNAL, 0);
					player.sendMessage(DvZ.getLanguage().getString("string_blink","You blink away!"));
				} else {
					player.sendMessage(DvZ.getLanguage().getString("string_cannot_blink","You cannot blink there!"));
					game.setCountdown(player.getName(), 1, 0);
				}
			} else {
				player.sendMessage(DvZ.getLanguage().getString("string_cannot_blink","You cannot blink there!"));
				game.setCountdown(player.getName(), 1, 0);
			}
		} else {
			player.sendMessage(DvZ.getLanguage().getString("string_wait","You have to wait -0- Seconds!").replace("-0-", ""+game.getCountdown(player.getName(), 1)));
		}
	}
	
	public static void spellEnderPortal(Game game, Player player) {
		if(game.getCountdown(player.getName(), 2)==0) {
			game.setCountdown(player.getName(), 2, plugin.getConfig().getInt("spelltime_enderportal",10));
			
			if(game.enderPortal!=null) {
				game.setCountdown(player.getName(), 2, 0);
				player.sendMessage(DvZ.getLanguage().getString("string_portal_exists","A Portal allready exists!"));
			} else {
				Location loc = player.getLocation();
				Location nloc = new Location(loc.getWorld(), loc.getBlockX()+0.5, loc.getBlockY()+10, loc.getBlockZ()+0.5);
				World w = loc.getWorld();
				
				createPortal(nloc);
				
				w.strikeLightningEffect(nloc);
				for(int i=0; i<10; i++) {
					ItemStack it = new ItemStack(121, 1);
					ItemMeta im = it.getItemMeta();
					im.setDisplayName(DvZ.getLanguage().getString("string_spell_disableportal","Disable Portal"));
					ArrayList<String> li = new ArrayList<String>();
					li.add(DvZ.getLanguage().getString("string_used_seconds","Can be used every -0- Seconds!").replace("-0-", ""+plugin.getConfig().getInt("spelltime_disableportal",3)));
					im.setLore(li);
					it.setItemMeta(im);
					
					w.dropItem(nloc, it);
				}
				
				player.teleport(nloc);
				player.getInventory().clear();
				ItemStack it = new ItemStack(369, 1);
				ItemMeta im = it.getItemMeta();
				im.setDisplayName(DvZ.getLanguage().getString("string_spell_reinforce_portal","Reinforce Portal"));
				ArrayList<String> li = new ArrayList<String>();
				li.add(DvZ.getLanguage().getString("string_used_seconds","Can be used every -0- Seconds!").replace("-0-", ""+plugin.getConfig().getInt("spelltime_reinforceenderportal",3)));
				im.setLore(li);
				it.setItemMeta(im);
				player.getInventory().addItem(it);
				player.getInventory().addItem(new ItemStack(320, 64));
				player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 95000, -20));
				player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 95000, 4));
				
				
				game.enderPortal = nloc;
				game.enderActive = true;
				game.enderMan = player.getName();
				
				game.broadcastMessage(DvZ.getLanguage().getString("string_portal_create","An Enderman has created a Portal!"));
			}
		} else {
			player.sendMessage(DvZ.getLanguage().getString("string_wait","You have to wait -0- Seconds!").replace("-0-", ""+game.getCountdown(player.getName(), 2)));
		}
	}
	
	public static void spellEnderReinforcePortal(Game game, Player player) {
		if(game.getCountdown(player.getName(), 3)==0) {
			game.setCountdown(player.getName(), 3, plugin.getConfig().getInt("spelltime_reinforceenderportal",3));
			
			if(player.getName().equals(game.enderMan)) {
				Location nloc = game.enderPortal;
				World w = nloc.getWorld();
				
				w.strikeLightningEffect(nloc);
				for(int i=0; i<10; i++) {
					ItemStack it = new ItemStack(121, 1);
					ItemMeta im = it.getItemMeta();
					im.setDisplayName(DvZ.getLanguage().getString("string_spell_disableportal","Disable Portal"));
					ArrayList<String> li = new ArrayList<String>();
					li.add(DvZ.getLanguage().getString("string_used_seconds","Can be used every -0- Seconds!").replace("-0-", ""+plugin.getConfig().getInt("spelltime_disableportal",3)));
					im.setLore(li);
					it.setItemMeta(im);
					
					w.dropItem(nloc, it);
				}
				
				player.teleport(nloc);
				player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 95000, -20));
				player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 95000, 4));
				
				game.enderActive = true;
				
				game.broadcastMessage(DvZ.getLanguage().getString("string_portal_reinforce","The Portal has been reinforced!"));
			}
		} else {
			player.sendMessage(DvZ.getLanguage().getString("string_wait","You have to wait -0- Seconds!").replace("-0-", ""+game.getCountdown(player.getName(), 3)));
		}
	}
	
	public static void spellTeleport(Game game, Player player) {
		if(game.getCountdown(player.getName(), 4)==0) {
			game.setCountdown(player.getName(), 4, plugin.getConfig().getInt("spelltime_teleport",15));
			
			if(game.enderPortal!=null) {
				if(game.enderActive) {
					player.teleport(game.enderPortal);
					player.sendMessage(DvZ.getLanguage().getString("string_teleport_success","You teleported to the Enderman Portal!"));
				} else {
					game.setCountdown(player.getName(), 4, 0);
					player.sendMessage(DvZ.getLanguage().getString("string_teleport_inactive","The Enderman Portal has been deactivated!"));
				}
			} else {
				game.setCountdown(player.getName(), 4, 0);
				player.sendMessage(DvZ.getLanguage().getString("string_teleport_noportal","The Enderman Portal does not exist yet!"));
			}
		} else {
			player.sendMessage(DvZ.getLanguage().getString("string_wait","You have to wait -0- Seconds!").replace("-0-", ""+game.getCountdown(player.getName(), 4)));
		}
	}
	
	public static void spellDrainHunger(Game game, Player player, Player target, int ammount) {
		if(game.getCountdown(player.getName(), 1)==0) {
			game.setCountdown(player.getName(), 1, plugin.getConfig().getInt("spelltime_drain_hunger",2));
			
			int newfood = target.getFoodLevel()-ammount;
			if(newfood<0) newfood = 0;
			
			target.setFoodLevel(newfood);
			
			//über 50 - alles entfernen
			if(ammount>50) {
				target.setFoodLevel(0);
				target.setSaturation(0);
			}
		} else {
			player.sendMessage(DvZ.getLanguage().getString("string_wait","You have to wait -0- Seconds!").replace("-0-", ""+game.getCountdown(player.getName(), 1)));
		}
	}
	
	public static void spellCatStealWeapon(Game game, Player player, Player target) {
		if(game.getCountdown(player.getName(), 1)==0) {
			game.setCountdown(player.getName(), 1, plugin.getConfig().getInt("spelltime_steal_weapon",2));
			
			ItemStack held = target.getItemInHand();
			target.setItemInHand(null);
			if(held.getTypeId()!=0) {
				target.getWorld().dropItemNaturally(target.getLocation(), held);
			}
		} else {
			player.sendMessage(DvZ.getLanguage().getString("string_wait","You have to wait -0- Seconds!").replace("-0-", ""+game.getCountdown(player.getName(), 1)));
		}
	}
	
	public static void spellHungryPigEat(Game game, Player player, Block target, int clicks) {
		game.setCountdown(player.getName(), 2, game.getCountdown(player.getName(), 2)-1);
		
		if(game.getCountdown(player.getName(), 2)<=-clicks) {
			game.setCountdown(player.getName(), 2, 0);
			
			int bid = target.getTypeId();
			if(bid==98 || bid==4) {
				Location loc = target.getLocation();
	
				target.setTypeId(0);
				loc.getWorld().playEffect(loc, Effect.SMOKE, 0);
				
				int ammount = 2;
				player.giveExp(ammount);
				if(game.getPlayerState(player.getName())==40) {
					if(player.getLevel()>=7 || (player.getLevel()==6 && player.getExpToLevel()<=ammount)) {
						Classswitcher.becomeHungryPig2(game, player);
					}
				}
				if(game.getPlayerState(player.getName())==41) {
					if(player.getLevel()>=10 || (player.getLevel()==9 && player.getExpToLevel()<=ammount)) {
						Classswitcher.becomeHungryPig3(game, player);
					}
				}
			}
		}
	}
	
	public static void spellSuizide(Game game, Player player) {
		player.damage(1000);
	}
	
	//create the portalblocks
	public static void createPortal(Location loc) {
		World w = loc.getWorld();
		int x = loc.getBlockX();
		int y = loc.getBlockY()-1;
		int z = loc.getBlockZ();
		
		Block block = w.getBlockAt(x, y, z);
		Block block2;
		block.setTypeId(121);
		
		for(int i=0; i<=4; i+=2) {
			block2 = block.getRelative(-1-i, 0, 0);
			block2.setTypeId(121);
			for(int j=-i/2; j<=i/2; j++) {
				block2 = block.getRelative(-1-i, 0, j);
				block2.setTypeId(121);
			}
			
			block2 = block.getRelative(1+i, 0, 0);
			block2.setTypeId(121);
			for(int j=-i/2; j<=i/2; j++) {
				block2 = block.getRelative(1+i, 0, j);
				block2.setTypeId(121);
			}
			
			block2 = block.getRelative(0, 0, -1-i);
			block2.setTypeId(121);
			for(int j=-i/2; j<=i/2; j++) {
				block2 = block.getRelative(j, 0, -1-i);
				block2.setTypeId(121);
			}
			
			block2 = block.getRelative(0, 0, 1+i);
			block2.setTypeId(121);
			for(int j=-i/2; j<=i/2; j++) {
				block2 = block.getRelative(j, 0, 1+i);
				block2.setTypeId(121);
			}
		}
		
		for(int i=-2; i<=2; i+=2) {
			for(int j=-2; j<=2; j+=2) {
				block2 = block.getRelative(i, 0, j);
				block2.setTypeId(121);
			}
		}
		
		for(int i=-4; i<=4; i+=4) {
			for(int j=-3; j<=3; j+=3) {
				block2 = block.getRelative(i, 0, j);
				block2.setTypeId(121);
			}
		}
		
		for(int i=-3; i<=3; i+=3) {
			for(int j=-4; j<=4; j+=4) {
				block2 = block.getRelative(i, 0, j);
				block2.setTypeId(121);
			}
		}
	}
	
	//###################################
	//Item Spells
	//###################################
	private static double throwvelocity = 2.75D;
	private static int throwHeight = 4;
	private static Material throwType = Material.STATIONARY_LAVA;

	public static void spellItemTrow(Player player, LivingEntity target) {
		if (target == null) {
			return;
		}
		// animate
		if (throwvelocity > 0) {
			target.setVelocity(new Vector(0, throwvelocity, 0));
		}

		if (throwHeight > 0) {
			List<Entity> allNearby = target.getNearbyEntities(50, 50, 50);
			allNearby.add(target);
			List<Player> playersNearby = new ArrayList<Player>();
			for (Entity e : allNearby) {
				if (e instanceof Player) {
					playersNearby.add((Player)e);
				}
			}
			new ThrowAnimation(target.getLocation(), playersNearby);
		}
	}

	private static class ThrowAnimation extends Animation {
		private Location start;
		private List<Player> nearby;

		public ThrowAnimation(Location start, List<Player> nearby) {
			super(0, 2, true);
			this.start = start;
			this.nearby = nearby;
		}

		@Override
		protected void onTick(int tick) {
			if (tick > throwHeight*2) {
				stop();
			} else if (tick < throwHeight) {
				Block block = start.clone().add(0,tick,0).getBlock();
				if (block.getType() == Material.AIR) {
					for (Player p : nearby) {
						p.sendBlockChange(block.getLocation(), throwType, (byte)0);
					}
				}
			} else {
				int n = throwHeight-(tick-throwHeight)-1;
				Block block = start.clone().add(0, n, 0).getBlock();
				for (Player p : nearby) {
					p.sendBlockChange(block.getLocation(), block.getType(), block.getData());
				}
			}
		}

	}
	
	//###################################
	//Inventory Helpers
	//###################################
	public static int removeItems(Player player, int type, int data, int remaining) {
		int itemsExchanged = 0;
		for (ItemStack i : player.getInventory()){
            if (i != null && i.getTypeId() == type && i.getData().getData() == data){
                if (i.getAmount() > remaining){
                    i.setAmount(i.getAmount() - remaining);
                    itemsExchanged += remaining;
                    remaining = 0;
                }else{
                    itemsExchanged += i.getAmount();
                    remaining -= i.getAmount();
                    player.getInventory().remove(i);
                }
                if(remaining==0) break;
            }
        }
		return itemsExchanged;
	}
	
	public static int countItems(Player player, int type, int data) {
		int items = 0;
		for (ItemStack i : player.getInventory()){
            if (i != null && i.getTypeId() == type && i.getData().getData() == data){
                items += i.getAmount();
            }
        }
		return items;
	}
}
