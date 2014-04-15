package me.andre111.dvz.dragon;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.utils.Animation;

import org.bukkit.Location;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class ControlledDragon implements Dragon {
	private int type;
	
	private Location spawn;
	private Location position;
	
	private float speed;
	
	private EnderDragon entity;
	private DragonAnimation animation;
	
	public ControlledDragon(int type) {
		this.type = type;
		this.animation = new DragonAnimation(0, 1, false);
		
		this.speed = 0.05F;
	}
	
	public void spawn(Location loc) {
		spawn = loc;
		position = spawn;
		
		entity = (EnderDragon) loc.getWorld().spawnEntity(loc, EntityType.ENDER_DRAGON);
		entity.teleport(loc);
		DvZ.dragonDeath.addDragon(entity);
		
		animation.play();
	}
	
	private class DragonAnimation extends Animation {

		public DragonAnimation(int delay, int interval, boolean autoStart) {
			super(delay, interval, autoStart);
		}

		@Override
		protected void onTick(int tick) {
			//TODO - Moving-done/Controlling(Path/Target finding) the Dragon
			Location oldpos = position.clone();
			Location target = position.clone();
			
			//target.add(1, 0, 1);
			//target = Bukkit.getServer().getPlayerExact("andre111").getLocation();
			//target.add(0, 6, 0);
			
			move(oldpos, target);
			
			//TODO - Dragon Attacking
			
			//Deathdetection
			if (entity.isDead()) {
				Player killer = entity.getKiller();
				if(killer!=null) {
					//TODO - Dragon Warrior
					DvZ.sendPlayerMessageFormated(killer, "You killed the Dragon!");
				}
				stop();
			}
		}
	}
	
	private void move(Location oldpos, Location target) {
		double xm = 0;
		double ym = 0;
		double zm = 0;
		
		if(target.getBlockX()>oldpos.getBlockX()) xm = 1; else if (target.getBlockX()<oldpos.getBlockX()) xm = -1;
		if(target.getBlockY()>oldpos.getBlockY()) ym = 1; else if (target.getBlockY()<oldpos.getBlockY()) ym = -1;
		if(target.getBlockZ()>oldpos.getBlockZ()) zm = 1; else if (target.getBlockZ()<oldpos.getBlockZ()) zm = -1;
		
		position = oldpos.add(new Vector(xm*speed, ym*speed, zm*speed));
		
		//position.setPitch(pitch);
		if(oldpos.getBlockX()<target.getBlockX())
			position.setYaw((float) Math.toDegrees(Math.atan((oldpos.getZ()-target.getZ())/(oldpos.getX()-target.getX()))) - 180 - 90);
		else if(oldpos.getBlockX()>target.getBlockX())
			position.setYaw((float) Math.toDegrees(Math.atan((oldpos.getZ()-target.getZ())/(oldpos.getX()-target.getX()))) - 180 + 90);
		
		
		entity.teleport(position);
	}
	
	public int getType() {
		return type;
	}

	//TODO - Dragon Methods
	@Override
	public Entity getEntity() {
		return null;
	}

	@Override
	public int getMana() {
		return 0;
	}

	@Override
	public void setMana(int mana) {
	}

	@Override
	public int getID() {
		return 0;
	}

	@Override
	public void setID(int id) {
	}

	@Override
	public void init() {
	}
}
