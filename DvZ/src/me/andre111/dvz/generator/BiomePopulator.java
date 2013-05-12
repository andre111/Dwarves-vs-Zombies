package me.andre111.dvz.generator;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.generator.BlockPopulator;

public class BiomePopulator extends BlockPopulator {
	private static int GRASS_CHANCE = 40;
	private static int YELLOW_CHANCE = 1;
	private static int RED_CHANCE = 1;
	private static int DEAD_CHANCE = 1;
	private static int DEAD_CHANCE2 = 25;
	private static int LILLY_CHANCE = 1;
	private static int LILLY_CHANCE2 = 25;

	@Override
	public void populate(World world, Random random, Chunk source) {
		int centerX = (source.getX() << 4);
		int centerZ = (source.getZ() << 4);

		for (int x = 0; x <= 15; x++) {
			for (int z = 0; z <= 15; z++) {
				//grass
				Block highest = world.getHighestBlockAt(centerX+x, centerZ+z);
				highest = highest.getRelative(0, 1, 0);
				for(int j=0; j<2; j++) {
					highest = highest.getRelative(0, -1, 0);
					if(highest.getType()==Material.GRASS) {
						//grass+flowers
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
						//snow
						if(highest.getTemperature()<0.4) {
							world.getBlockAt(centerX+x, highest.getY()+1, centerZ+z).setType(Material.SNOW);
						}
						//sand-desert
						if(highest.getTemperature()>1.3) {
							world.getBlockAt(centerX+x, highest.getY()+1, centerZ+z).setType(Material.AIR);
							if(random.nextInt(100)<DEAD_CHANCE) 
							if(random.nextInt(100)<DEAD_CHANCE2) {
								world.getBlockAt(centerX+x, highest.getY()+1, centerZ+z).setType(Material.DEAD_BUSH);
								world.getBlockAt(centerX+x, highest.getY()+1, centerZ+z).setData((byte) 0);
							}
							
							world.getBlockAt(centerX+x, highest.getY(), centerZ+z).setType(Material.SAND);
							world.getBlockAt(centerX+x, highest.getY()-1, centerZ+z).setType(Material.SAND);
							world.getBlockAt(centerX+x, highest.getY()-2, centerZ+z).setType(Material.SAND);
							world.getBlockAt(centerX+x, highest.getY()-3, centerZ+z).setType(Material.SANDSTONE);
							world.getBlockAt(centerX+x, highest.getY()-4, centerZ+z).setType(Material.SANDSTONE);
						}
					}
					//waterlily
					if(highest.getType()==Material.STATIONARY_WATER) {
						if(highest.getHumidity()>0.8) {
							if(random.nextInt(100)<LILLY_CHANCE) 
							if(random.nextInt(100)<LILLY_CHANCE2) {
								world.getBlockAt(centerX+x, highest.getY()+1, centerZ+z).setType(Material.WATER_LILY);
							}
						}
					}
				}
			}
		}
	}

}
