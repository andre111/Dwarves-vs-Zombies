package me.andre111.dvz.generator;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

public class SpawnPopulator extends BlockPopulator {

	@Override
	public void populate(World world, Random random, Chunk source) {
		if(source.getX()==0 && source.getZ()==0) {
			int centerX = (source.getX() << 4);// + random.nextInt(16);
            int centerZ = (source.getZ() << 4);// + random.nextInt(16);
            int centerY = world.getHighestBlockYAt(centerX, centerZ);
            Vector center = new BlockVector(centerX, centerY, centerZ);
            
            int size = 6;
            
            //platform
            for (int x = -size; x <= size; x++) {
            	for (int z = -size; z <= size; z++) {
            		Vector position = center.clone().add(new Vector(x, 0, z));
            		
            		world.getBlockAt(position.toLocation(world)).setType(Material.WOOD);
            		
            		//pillars
            		if((x==-size && (z==-size || z==size))
            		   || (x==size && (z==-size || z==size))) {
	            		int y = -1;
	            		Vector yposition = center.clone().add(new Vector(x, y, z));
	            		int blockType = world.getBlockAt(yposition.toLocation(world)).getTypeId();
	            		while(blockType==Material.AIR.getId() || blockType==Material.STATIONARY_WATER.getId() || blockType==Material.WATER.getId()) {
	            			
	            			world.getBlockAt(yposition.toLocation(world)).setType(Material.WOOD);
	            			
	            			y -= 1;
	            			yposition = center.clone().add(new Vector(x, y, z));
	                		blockType = world.getBlockAt(yposition.toLocation(world)).getTypeId();
	            		}
            		}
            		
            		//top
            		for (int y = 1; y <= 5; y++) {
            			Vector yposition = center.clone().add(new Vector(x, y, z));
	            		
            			world.getBlockAt(yposition.toLocation(world)).setType(Material.AIR);
            			
            			//sides
            			if((x==-size && (z==-size || z==size))
                     		   || (x==size && (z==-size || z==size))) {
            				
            				world.getBlockAt(yposition.toLocation(world)).setType(Material.WOOD);
            			}
            			
            			//fence
            			if(y==1 && (x==-size || x==size || z==-size || z==size)) {
            				
            				if(!(x==0 || z==0) && world.getBlockAt(yposition.toLocation(world)).getType()==Material.AIR)
            					world.getBlockAt(yposition.toLocation(world)).setType(Material.FENCE);
            			}
            			
            			//top
            			if(y==5) {
            				world.getBlockAt(yposition.toLocation(world)).setType(Material.WOOD);
            			}
            		}
            	}
            }
		}
	}

}
