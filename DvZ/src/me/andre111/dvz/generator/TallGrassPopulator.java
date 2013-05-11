package me.andre111.dvz.generator;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.generator.BlockPopulator;

public class TallGrassPopulator extends BlockPopulator {
	private static int GRASS_CHANCE = 40;
	private static int YELLOW_CHANCE = 1;
	private static int RED_CHANCE = 1;

	@Override
	public void populate(World world, Random random, Chunk source) {
		int centerX = (source.getX() << 4);
		int centerZ = (source.getZ() << 4);

		for (int x = 0; x <= 15; x++) {
			for (int z = 0; z <= 15; z++) {
				//grass
				Block highest = world.getHighestBlockAt(centerX+x, centerZ+z);
				highest = highest.getRelative(0, -1, 0);
				if(highest.getType()==Material.GRASS) {
					if(random.nextInt(100)<GRASS_CHANCE) {
						world.getBlockAt(centerX+x, highest.getY()+1, centerZ+z).setType(Material.LONG_GRASS);
						world.getBlockAt(centerX+x, highest.getY()+1, centerZ+z).setData((byte) 1);
					}
					
					if(random.nextInt(100)<YELLOW_CHANCE) {
						world.getBlockAt(centerX+x, highest.getY()+1, centerZ+z).setType(Material.YELLOW_FLOWER);
					}
					
					if(random.nextInt(100)<RED_CHANCE) {
						world.getBlockAt(centerX+x, highest.getY()+1, centerZ+z).setType(Material.RED_ROSE);
					}
				}
			}
		}
	}

}
