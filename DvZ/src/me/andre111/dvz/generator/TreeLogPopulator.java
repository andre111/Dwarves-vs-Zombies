package me.andre111.dvz.generator;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

public class TreeLogPopulator extends BlockPopulator {
	private int treeHeight = 3;
	private int treeCount = 10;
	private int chunckChance = 25;

	@Override
	public void populate(World world, Random random, Chunk source) {
		if(random.nextInt(100)<chunckChance) {
			int xB = (source.getX() << 4);
			int zB = (source.getZ() << 4);
			
			for(int i=0; i<random.nextInt(treeCount); i++) {
				populateIntern(world, random, xB+random.nextInt(16), zB+random.nextInt(16));
			}
		}
	}
	
	public void populateIntern(World world, Random random, int xB, int zB) {
		int yB = world.getHighestBlockYAt(xB, zB);
		
        int height = random.nextInt(3) + this.treeHeight;

        if (yB >= 1 && yB + height + 1 <= 256) {
            int i1;

            Material mat = world.getBlockAt(xB, yB-1, zB).getType();
            if(mat==Material.GRASS ||
               mat==Material.DIRT ||
               mat==Material.LONG_GRASS) {
	            for (i1 = yB; i1 <= yB + 1 + height; ++i1) {
	                world.getBlockAt(xB, i1, zB).setType(Material.LOG);
	            }
            }
        } else {
            return;
        }
	}

}
