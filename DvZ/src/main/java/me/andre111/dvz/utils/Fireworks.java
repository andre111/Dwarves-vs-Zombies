package me.andre111.dvz.utils;

import java.lang.reflect.Field;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;

public abstract class Fireworks {
	
	public static void spawnTest(Location loc) {
		Firework fw = spawnFirework(loc);
		FireworkMeta fm = fw.getFireworkMeta();
		fm.clearEffects();
		
		Builder build = FireworkEffect.builder();
		build.flicker(false);
		build.trail(false);
		build.with(Type.BURST);
		build.withColor(Color.WHITE);
		FireworkEffect fe = build.build();
		
		fm.addEffect(fe);
		
		instantExplode(fm);
		
		fw.setFireworkMeta(fm);
		fw.setVelocity(new Vector(0, 0, 0));
	}
	
	public static void spawnEffect(Location loc, Color color, Type type) {
		Firework fw = spawnFirework(loc);
		FireworkMeta fm = fw.getFireworkMeta();
		fm.clearEffects();
		
		Builder build = FireworkEffect.builder();
		build.flicker(false);
		build.trail(false);
		build.with(type);
		build.withColor(color);
		FireworkEffect fe = build.build();
		
		fm.addEffect(fe);
		
		instantExplode(fm);
		
		fw.setFireworkMeta(fm);
		fw.setVelocity(new Vector(0, 0, 0));
	}
	
	private static Firework spawnFirework(Location loc) {
		return (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
	}
	
	private static int identifier = 0;
	private static void instantExplode(FireworkMeta fwm) {
		fwm.setPower(identifier);
		
		try {
			//Finding the right field
			Field[] fields = fwm.getClass().getDeclaredFields();
			for(int i=0; i<fields.length; i++) {
				if (fields[i].getClass().equals(int.class) || fields[i].getType().equals(Integer.TYPE) ) {
					fields[i].setAccessible(true);
					if (fields[i].getInt(fwm)==identifier) {
						fields[i].set(fwm, -2);
					}
				}
			}
			
			//############ -> right, but not used for non direct mode
            //Field f = fm.getClass().getDeclaredField("power");
            //f.setAccessible(true);
            //f.set(fm, -2);
        } catch (SecurityException e1) {
            e1.printStackTrace();
        } catch (IllegalArgumentException e1) {
            e1.printStackTrace();
        } catch (IllegalAccessException e1) {
            e1.printStackTrace();
        //} catch (NoSuchFieldException e) {
		//	e.printStackTrace();
		}
	}
}
