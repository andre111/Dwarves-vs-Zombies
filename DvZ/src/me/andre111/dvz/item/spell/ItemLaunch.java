package me.andre111.dvz.item.spell;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.Game;
import me.andre111.dvz.item.ItemSpell;

public class ItemLaunch extends ItemSpell {
	private int blockId = 1;
	private byte blockData = 0;

	private double power = 1;
	
	private boolean drop = false;
	private boolean block = false;
	
	private ItemSpell onHit;
	
	@Override
	public void setCastVar(int id, String var) {
		//onHit
		if(id==5) {
			try {
				if(!var.contains("me.andre111.dvz.item.spell.")) {
					var = "me.andre111.dvz.item.spell." + var;
				}
				Class<?> c = Class.forName(var);
				if(c.getSuperclass().equals(ItemSpell.class)) {
					onHit = (ItemSpell) c.newInstance();
					onHit.setItemName(getItemName());
					onHit.setLeft(isLeft());
				}
			} catch (ClassNotFoundException e) {
			} catch (InstantiationException e) {
			} catch (IllegalAccessException e) {
			}
		} 
		//castVars für onHit
		else if(id>5) {
			if(onHit!=null) onHit.setCastVar(id-6, var);
		}
	}
	
	@Override
	public void setCastVar(int id, double var) {
		if(id==0) blockId = (int) Math.round(var);
		else if(id==1) blockData = (byte) Math.round(var);
		else if(id==2) power = var;
		else if(id==3) drop = var==1;
		else if(id==4) block = var==1;
		//castVars für onHit
		else if(id>5) {
			if(onHit!=null) onHit.setCastVar(id-6, var);
		}
	}
	
	@Override
	public boolean cast(Game game, Player player) {
		Location loc = player.getEyeLocation();
		FallingBlock fs = loc.getWorld().spawnFallingBlock(loc, blockId, blockData);
		
		Vector velocity = loc.getDirection().normalize().multiply(power);
		fs.setVelocity(velocity);
		
		fs.setDropItem(drop);
		if(!block) fs.setMetadata("dvz_falling_noblock", new FixedMetadataValue(DvZ.instance, 0));
		
		fs.setMetadata("dvz_falling_casting", new FixedMetadataValue(DvZ.instance, this));
	
		return true;
	}
	
	@Override
	public boolean cast(Game game, Player player, Block target) {
		return cast(game, player);
	}
	@Override
	public boolean cast(Game game, Player player, Player target) {
		return cast(game, player);
	}
	
	public void onHit(Block block) {
		//effects
		getItem().createEffects(block.getLocation(), isLeft(), "onHit");
		
		onHit.cast(block.getLocation());
	}
}
