package me.andre111.dvz.dragon;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.config.ConfigManager;
import me.andre111.dvz.utils.Animation;
import me.andre111.dvz.utils.Fireworks;
import me.andre111.dvz.utils.PlayerHandler;

public class DragonAttackManager {
	public DragonCustom[] dragons;
	private int dragonCounter;
	public DragonCustomAttack[] attacks;
	private int attackCounter;
	
	public void loadAttacks() {
		FileConfiguration df = ConfigManager.getDragonsFile();
		//dragons
		ConfigurationSection ds = df.getConfigurationSection("dragons");
		Set<String> strings = ds.getKeys(false);
		String[] stK = strings.toArray(new String[strings.size()]);
		dragons = new DragonCustom[stK.length];
		for(int i=0; i<stK.length; i++) {
			loadDragon(stK[i]);
		}
		//attacks
		attackCounter = 0;
		ConfigurationSection as = df.getConfigurationSection("attacks");
		Set<String> strings2 = as.getKeys(false);
		String[] stK2 = strings2.toArray(new String[strings2.size()]);
		attacks = new DragonCustomAttack[stK2.length];
		for(int i=0; i<stK2.length; i++) {
			loadAttack(stK2[i]);
		}
	}
	
	private void loadDragon(String dp) {
		DragonCustom dcTemp = new DragonCustom();
		dcTemp.setName(ConfigManager.getDragonsFile().getString("dragons."+dp+".name", ""));
		dcTemp.setMana(ConfigManager.getDragonsFile().getInt("dragons."+dp+".mana", 0));
		dcTemp.setHealth(ConfigManager.getDragonsFile().getInt("dragons."+dp+".health", 200));
		dcTemp.setFlyingSpeed((float) ConfigManager.getDragonsFile().getDouble("dragons."+dp+".speed", 0.2));
		dragons[dragonCounter] = dcTemp;
		dragonCounter++;
	}
	
	private void loadAttack(String ap) {
		DragonCustomAttack dcaTemp = new DragonCustomAttack();
		dcaTemp.setType(ConfigManager.getDragonsFile().getInt("attacks."+ap+".type", 0));
		dcaTemp.setTimes(ConfigManager.getDragonsFile().getInt("attacks."+ap+".times", 1));
		dcaTemp.setDelay(ConfigManager.getDragonsFile().getInt("attacks."+ap+".delay", 0));
		dcaTemp.setMaxDistance(ConfigManager.getDragonsFile().getInt("attacks."+ap+".maxDistance", 100));
		dcaTemp.setName(ConfigManager.getDragonsFile().getString("attacks."+ap+".name", ""));
		dcaTemp.setStopAtCol(ConfigManager.getDragonsFile().getBoolean("attacks."+ap+".stopAtCol", true));
		dcaTemp.setSpeed(ConfigManager.getDragonsFile().getInt("attacks."+ap+".speed", 1));
		//Cast
		//#########
		String attack = ConfigManager.getDragonsFile().getString("attacks."+ap+".cast", "");
		try {
			if(!attack.contains("me.andre111.dvz.dragon.attack.")) {
				attack = "me.andre111.dvz.dragon.attack." + attack;
			}
			Class<?> c = Class.forName(attack);
			if(c.getSuperclass().equals(DragonAttack.class)) {
				dcaTemp.setOnHit((DragonAttack) c.newInstance());
				//double
				dcaTemp.getOnHit().setCastVar(0, ConfigManager.getDragonsFile().getDouble("attacks."+ap+".castVar0", 0));
				dcaTemp.getOnHit().setCastVar(1, ConfigManager.getDragonsFile().getDouble("attacks."+ap+".castVar1", 0));
				//new method, for loading more than 2 cast vars
				List<String> stList = ConfigManager.getDragonsFile().getStringList("attacks."+ap+".castVars");
				for(int i=0; i<stList.size(); i++) {
					dcaTemp.getOnHit().setCastVar(i, stList.get(i));
					try {
						double d = Double.parseDouble(stList.get(i));
						dcaTemp.getOnHit().setCastVar(i, d);
					} catch (NumberFormatException  e) {
					}
				}
				//changed to string reader, because doublelist skips string
				//-> numbers get messed up
			}
		} catch (ClassNotFoundException e) {
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		}
		//#########
		//FX
		//#########
		dcaTemp.setEffect(ConfigManager.getDragonsFile().getInt("attacks."+ap+".fx", 0));
		dcaTemp.setEffectSpeed(ConfigManager.getDragonsFile().getInt("attacks."+ap+".fxSpeed", 1));
		dcaTemp.setFwColor(Color.fromRGB(ConfigManager.getDragonsFile().getInt("attacks."+ap+".fxColorR", 0), ConfigManager.getDragonsFile().getInt("attacks."+ap+".fxColorG", 0), ConfigManager.getDragonsFile().getInt("attacks."+ap+".fxColorB", 0)));
		//fxType
		dcaTemp.setFwType(ConfigManager.getDragonsFile().getInt("attacks."+ap+".fxType", 0));
		//#########
		dcaTemp.setManaCost(ConfigManager.getDragonsFile().getInt("attacks."+ap+".mana", 100));
		dcaTemp.setItemID(ConfigManager.getDragonsFile().getInt("attacks."+ap+".item", 256));
		dcaTemp.setCanMove(ConfigManager.getDragonsFile().getBoolean("attacks."+ap+".move", false));
		dcaTemp.setInvulnarable(ConfigManager.getDragonsFile().getBoolean("attacks."+ap+".invul", false));
		//dragon types
		dcaTemp.setDragons(ConfigManager.getDragonsFile().getIntegerList("attacks."+ap+".dragonTyps"));
		attacks[attackCounter] = dcaTemp;
		attackCounter++;
	}
	
	public void addCastItems(Player player, int dID) {
		PlayerInventory pI = player.getInventory();
		pI.clear();
		
		for(int i=0; i<attacks.length; i++) {
			if(attacks[i].getDragons().contains(dID)) {
				ItemStack it = new ItemStack(attacks[i].getItemID());
				ItemMeta im = it.getItemMeta();
				im.setDisplayName(attacks[i].getName());
				it.setItemMeta(im);
				
				pI.addItem(it);
			}
		}
		
		DvZ.updateInventory(player);
	}
	
	public DragonCustom getDragon(int id) {
		if(id>=0 && id<dragonCounter) {
			return dragons[id];
		}
		
		return null;
	}
	
	public int getMaxDragonCount() {
		return dragonCounter-1;
	}
	
	public int getAttack(int itemID) {
		for(int i=0; i<attacks.length; i++) {
			if(attacks[i].getItemID() == itemID) {
				return i;
			}
		}
		
		return -1;
	}
	
	public boolean castFromPlayerDragon(PlayerDragon player, int id) {
		if(player.getMana()<attacks[id].getManaCost()) {
			return false;
		}
		
		player.setMana(player.getMana()-attacks[id].getManaCost());
		
		return castFromPlayer((Player) player.getEntity(), id);
	}
	
	public boolean castFromPlayer(Player player, int id) {
		//DvZ.sendPlayerMessageFormated(player, "Trying to cast: "+attacks[id].getName());
		//looking towards block
		if(attacks[id].getType()==1) {
			int distance = attacks[id].getMaxDistance();
			List<Block> blocks = player.getLineOfSight(DvZ.transparent, distance);
			Block furthest = blocks.get(0);
			Location ploc = player.getLocation();
			double maxDistance = 0;
			for(int i=0; i<blocks.size(); i++) {
				double dist = blocks.get(i).getLocation().distanceSquared(ploc);
				if (dist>maxDistance) {
					maxDistance = dist;
					furthest = blocks.get(i);
				}
			}
			//nothing reached
			if (DvZ.transparent.contains((byte)furthest.getTypeId()))
			{
				return false;
			}
			return castAnimation(id, player, furthest.getLocation());
		//looking towards player
		} else if(attacks[id].getType()==3) {
			Player nearest = PlayerHandler.getTarget(player, attacks[id].getMaxDistance());
			if (nearest==null) return false;
			return castAnimation(id, player, nearest.getLocation());
		}
		//ervything else
		return castAnimation(id, player, player.getLocation());
	}
	
	public boolean castAnimation(int id, Entity dragon, Location target) {
		if(attacks.length-1<id) return false;
		
		//if(attacks[id].getType()>0) {
			AttackAnimationManager atam = new AttackAnimationManager(this, id, 1, 1, false);
			atam.setCaster(dragon);
			atam.setTarget(target);
			atam.play();
		//} else {
		//	attacks[id].getOnHit().cast(dragon);
		//}
		
		return true;
	}
	
	//AttackManager
	//(How often the attack gets called)
	private class AttackAnimationManager extends Animation {
		private int id;
		private DragonAttackManager manager;
		private Entity dragon;
		private Location target;
		
		private int count;
		private int ticker;
		
		private boolean init;
		
		private AttackAnimation lastAttack;
		
		public AttackAnimationManager(DragonAttackManager manager, int attack, int delay, int interval, boolean autoStart) {
			super(delay, interval, autoStart);
			this.manager = manager;
			this.id = attack;
			
			init = false;
			
			count = 0;
			ticker = 0;
		}
		
		public void setCaster(Entity caster) {
			dragon = caster;
		}
		
		public void setTarget(Location loc) {
			target = loc;
		}

		@Override
		protected void onTick(int tick) {
			if(!init) {
				init = true;
				//movement
				if(!manager.attacks[id].isCanMove()) {
					if(dragon instanceof Player) {
						Player entity = (Player) dragon;
						DvZ.moveStop.addEntity(entity);
					}
				}
				//
				//invulnarable
				if(manager.attacks[id].isInvulnarable()) {
					if(dragon instanceof Player) {
						Player entity = (Player) dragon;
						DvZ.inVul.addEntity(entity);
					}
				}
				//
			}
			
			ticker++;
			//delay
			if(ticker>=manager.attacks[id].getDelay()) {
				ticker = 0;
				//times
				if(count<manager.attacks[id].getTimes()) {
					count++;
					cast();
				} else {
					//movement
					if(!manager.attacks[id].isCanMove()) {
						if(dragon instanceof Player) {
							Player entity = (Player) dragon;
							DvZ.moveStop.removeEntity(entity);
						}
					}
					//
					if(lastAttack == null) {
						stop();
					} else if(lastAttack.isStopped()) {
						stop();
					}
				}
			}
		}
		
		@Override
		public void stop() {
			super.stop();
			lastAttack = null;
			//invulnarable
			if(manager.attacks[id].isInvulnarable()) {
				if(dragon instanceof Player) {
					Player entity = (Player) dragon;
					DvZ.inVul.removeEntity(entity);
				}
			}
			//
		}
		
		private void cast() {
			if(manager.attacks[id].getType()==0) {
				attacks[id].getOnHit().cast(dragon);
			}else if(manager.attacks[id].getType()==1 || manager.attacks[id].getType()==3) {
				AttackAnimation ata = new AttackAnimation(manager, id, 1, 1, false);
				ata.setStart(dragon.getLocation());
				ata.setTarget(target);
				ata.setCaster(dragon);
				ata.play();
				
				lastAttack = ata;
			} else if(manager.attacks[id].getType()==2) {
				AttackAnimation ata = new AttackAnimation(manager, id, 1, 1, false);
				ata.setStart(dragon.getLocation());
				Location t = dragon.getLocation();
				t.setY(5);
				ata.setTarget(t);
				ata.setCaster(dragon);
				ata.play();
				
				lastAttack = ata;
			}
		}
	}
	
	//AttackController
	//(Move the "bullet" and call Attack on impact)
	private class AttackAnimation extends Animation {
		private DragonAttackManager manager;
		
		private Location loc;
		private Location target;
		private int attack;
		
		private Entity caster;
		
		private int ticker;
		private boolean init;
		
		private double xStart;
		private double yStart;
		private double zStart;
		
		private double xPlus;
		private double yPlus;
		private double zPlus;
		
		private double xCurrent;
		private double yCurrent;
		private double zCurrent;
		
		private Fireball fBall;
		
		private Location blockprev;
		
		public AttackAnimation(DragonAttackManager manager, int attack, int delay, int interval, boolean autoStart) {
			super(delay, interval, autoStart);
			this.manager = manager;
			this.attack = attack;
			ticker = 0;
			init = false;
		}
		
		public void setStart(Location loc) {
			this.loc = loc;
		}
		
		public void setTarget(Location loc) {
			this.target = loc;
		}
		
		public void setCaster(Entity entity) {
			caster = entity;
		}
		
		//init the steps and get the distance per step
		private void initAnimation() {
			xStart = loc.getBlockX();
			yStart = loc.getBlockY();
			zStart = loc.getBlockZ();
			
			double xt = target.getBlockX();
			double yt = target.getBlockY();
			double zt = target.getBlockZ();
			
			//individual distance
			double xDiff = xt-xStart;
			double yDiff = yt-yStart;
			double zDiff = zt-zStart;
			
			//maxdistance
			double maxDiff = 0;
			if(Math.abs(xDiff)>maxDiff) maxDiff=Math.abs(xDiff);
			if(Math.abs(yDiff)>maxDiff) maxDiff=Math.abs(yDiff);
			if(Math.abs(zDiff)>maxDiff) maxDiff=Math.abs(zDiff);
			
			xCurrent = 0;
			yCurrent = 0;
			yCurrent = 0;
			
			//distance per step
			xPlus = xDiff/maxDiff;
			yPlus = yDiff/maxDiff;
			zPlus = zDiff/maxDiff;
			
			//Fireball
			if(manager.attacks[attack].isEffect()==2) {
				fBall = (Fireball) loc.getWorld().spawnEntity(loc, EntityType.FIREBALL);
				fBall.setBounce(true);
				fBall.setIsIncendiary(false);
				fBall.setYield(0F);
				DvZ.attackListener.addFireball(fBall);
			}
			if(manager.attacks[attack].isEffect()==4) {
				blockprev = loc;
			}
			//
		}
		//

		@Override
		protected void onTick(int tick) {
			if (!init) {
				init = true;
				initAnimation();
			}
			
			int speed = manager.attacks[attack].getSpeed();
			ticker++;
			
			if(ticker>=speed) {
				ticker = 0;
				//Move
				int xt = target.getBlockX();
				int yt = target.getBlockY();
				int zt = target.getBlockZ();
				/*int xp = loc.getBlockX();
				int yp = loc.getBlockY();
				int zp = loc.getBlockZ();
				
				if(xp<xt) xp+=1;
				if(xp>xt) xp-=1;
				if(yp<yt) yp+=1;
				if(yp>yt) yp-=1;
				if(zp<zt) zp+=1;
				if(zp>zt) zp-=1;*/
				xCurrent+=xPlus;
				yCurrent+=yPlus;
				zCurrent+=zPlus;
				
				double xp = xStart+xCurrent;
				double yp = yStart+yCurrent;
				double zp = zStart+zCurrent;
				
				loc.setX(xp);
				loc.setY(yp);
				loc.setZ(zp);
				//
				
				//effect
				if(manager.attacks[attack].isEffect()==1) {
					Fireworks.spawnEffect(loc, manager.attacks[attack].getFwColor(), manager.attacks[attack].getFwType());
				} else if(manager.attacks[attack].isEffect()==2) {
					fBall.teleport(loc);
					if(fBall.isOnGround()) {
						fBall.remove();
					}
				} else if(manager.attacks[attack].isEffect()==3) {
					caster.teleport(loc);
				} else if(manager.attacks[attack].isEffect()==4) {
					//animate fake block
					Arrow a = (Arrow) loc.getWorld().spawnEntity(loc, EntityType.ARROW);
					List<Entity> allNearby = a.getNearbyEntities(50, 50, 50);
					a.remove();
					List<Player> playersNearby = new ArrayList<Player>();
					for (Entity e : allNearby) {
						if (e instanceof Player) {
							playersNearby.add((Player)e);
						}
					}
					
					for(Player p : playersNearby) {
						Block prev = blockprev.getWorld().getBlockAt(blockprev);
						p.sendBlockChange(prev.getLocation(), prev.getType(), prev.getData());
						
						p.sendBlockChange(loc, manager.attacks[attack].getFxType(), (byte) 0);
					}
					
					blockprev = loc.clone();
					//
				}
				//
				
				//hit
				double prec = .01;
				if(((Math.abs(xp - xt) < prec) && (Math.abs(yp - yt) < prec) && (Math.abs(zp - zt) < prec))
				   || !DvZ.isPathable(target.getWorld().getBlockAt((int)Math.round(xp), (int)Math.round(yp), (int)Math.round(zp)))
				   || isPlayerNearExcluding(loc, caster, 3)) {
					DragonAttack onHit = manager.attacks[attack].getOnHit();
					if(onHit!=null) {
						//cast on PLayer
						if(attacks[attack].getType()==3) {
							onHit.castOnNearPlayer(loc);
						//normal attack
						} else {
							onHit.cast(loc);
						}
					}
					
					if(manager.attacks[attack].isEffect()==2) {
						fBall.remove();
					}
					
					if(!((Math.abs(xp - xt) < prec) && (Math.abs(yp - yt) < prec) && (Math.abs(zp - zt) < prec))) {
						if(manager.attacks[attack].isStopAtCol()) 
							stop();
					} else {
						stop();
					}
				}
				//
			}
		}
		
		@Override
		public void stop() {
			super.stop();
			
			if(manager.attacks[attack].isEffect()==4) {
				//remove last fake block
				Arrow a = (Arrow) loc.getWorld().spawnEntity(loc, EntityType.ARROW);
				List<Entity> allNearby = a.getNearbyEntities(50, 50, 50);
				List<Player> playersNearby = new ArrayList<Player>();
				for (Entity e : allNearby) {
					if (e instanceof Player) {
						playersNearby.add((Player)e);
					}
				}
				
				for(Player p : playersNearby) {
					Block prev = blockprev.getWorld().getBlockAt(blockprev);
					p.sendBlockChange(prev.getLocation(), prev.getType(), prev.getData());
				}
				a.remove();
				//
			}
		}
		
		private boolean isPlayerNearExcluding(Location loc, Entity exclude, int ent) {
			List<Player> players = loc.getWorld().getPlayers();
			for(int i=0; i<players.size(); i++) {
				if(!players.get(i).equals(exclude)) {
					if(players.get(i).getLocation().distanceSquared(loc)<=ent) {
						return true;
					}
				}
			}
			
			return false;
		}
	}
}
