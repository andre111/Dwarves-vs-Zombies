package me.andre111.dvz.item.spell;

//TODO - port to the ItemSpell system
/*import java.util.List;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.Game;
import me.andre111.dvz.monster.MonsterAttack;
import me.andre111.dvz.utils.Animation;
import me.andre111.dvz.utils.Fireworks;*/

public class MonsterTargeted {/*extends MonsterAttack {
	protected MonsterAttack onHit = null;
	private double range = 10;
	protected boolean fx = true;
	protected int fx_Type = 0;
	protected int fx_R = 255;
	protected int fx_G = 255;
	protected int fx_B = 255;
	
	protected Player caster;
	protected Game game;
	
	@Override
	public void setCastVar(int id, String var) {
		//onHit
		if(id==0) {
			try {
				if(!var.contains("me.andre111.dvz.monster.attack.")) {
					var = "me.andre111.dvz.monster.attack." + var;
				}
				Class<?> c = Class.forName(var);
				if(c.getSuperclass().equals(MonsterAttack.class)) {
					onHit = (MonsterAttack) c.newInstance();
				}
			} catch (ClassNotFoundException e) {
			} catch (InstantiationException e) {
			} catch (IllegalAccessException e) {
			}
		} 
		//castVars für onHit
		else if(id>6) {
			if(onHit!=null) onHit.setCastVar(id-7, var);
		}
	}
	
	@Override
	public void setCastVar(int id, double var) {
		//castVars für onHit
		if(id==1) range = var;
		else if(id==2) fx = var==1;
		else if(id==3) fx_Type = (int) Math.round(var);
		else if(id==4) fx_R = (int) Math.round(var);
		else if(id==5) fx_G = (int) Math.round(var);
		else if(id==6) fx_B = (int) Math.round(var);
		//castVars für onHit
		else {
			if(onHit!=null) onHit.setCastVar(id-7, var);
		}
	}
	
	@Override
	public void spellCastFarTargeted(Game g, Player player, Block target) {	
		this.game = g;
		caster = player;
		
		CastManager cast = new CastManager(this, 1, 1, false);
		cast.setPosition(player.getLocation());
		cast.setTarget(target.getLocation());
		cast.play();
	}
	
	private class CastManager extends Animation {
		private MonsterTargeted attack;
		private Location pos;
		private Location target;
		
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
		
		public CastManager(MonsterTargeted att, int delay, int interval, boolean autoStart) {
			super(delay, interval, autoStart);
			this.attack = att;
			ticker = 0;
			init = false;
		}
		
		public void setPosition(Location loc) {
			pos = loc;
		}
		public void setTarget(Location loc) {
			target = loc;
		}
		
		//init the steps and get the distance per step
		private void initAnimation() {
			xStart = pos.getBlockX();
			yStart = pos.getBlockY();
			zStart = pos.getBlockZ();

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
		}
		//

		@Override
		protected void onTick(int tick) {
			if (!init) {
				init = true;
				initAnimation();
			}
			
			int speed = 1;
			ticker++;
			
			if(ticker>=speed) {
				ticker = 0;
				//Move
				int xt = target.getBlockX();
				int yt = target.getBlockY();
				int zt = target.getBlockZ();
				
				xCurrent+=xPlus;
				yCurrent+=yPlus;
				zCurrent+=zPlus;
				
				double xp = xStart+xCurrent;
				double yp = yStart+yCurrent;
				double zp = zStart+zCurrent;
				
				pos.setX(xp);
				pos.setY(yp);
				pos.setZ(zp);
				//
				
				//effect
				if(attack.fx) {
					Fireworks.spawnEffect(pos, Color.fromRGB(attack.fx_R, attack.fx_G, attack.fx_B), attack.getFwType());
				}
				
				double prec = .01;
				if(((Math.abs(xp - xt) < prec) && (Math.abs(yp - yt) < prec) && (Math.abs(zp - zt) < prec))
				   || !DvZ.isPathable(target.getWorld().getBlockAt((int)Math.round(xp), (int)Math.round(yp), (int)Math.round(zp)))
				   || isPlayerNearExcluding(pos, caster, 3)) {
					if(onHit!=null) {
						onHit.spellCastOnLocation(game, caster, pos);
					}
					
					stop();
				}
			}
		}
	}
	
	protected Type getFwType() {
		switch(fx_Type) {
		case 0:
		default:
			return Type.BURST;
		case 1:
			return Type.BALL;
		case 2:
			return Type.BALL_LARGE;
		case 3:
			return Type.STAR;
		case 4:
			return Type.CREEPER;
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
	
	@Override
	public int getType() {
		return 3;
	}
	
	@Override
	public int getRange() {
		return (int) Math.round(range);
	}*/
}
