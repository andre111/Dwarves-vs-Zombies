package me.andre111.dvz.generator;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public abstract class QuarryGenerator {
	private static Random rand = new Random();

	public static void generateQuarry(Location loc, int radius, int depth) {
		generateQuarry(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), radius, depth);
	}
	
	public static void generateQuarry(World world, int x, int y, int z, int radius, int depth) {
		//stone border
		BasicGenerator.generateCylinder(world, x, y, z, radius+4, depth, 1, (byte) 0);
		
		//basic gravel stuff
		boolean gravel = false; //true; //disbaled for testing
		BasicGenerator.generateCylinder(world, x, y, z, radius, depth, gravel ? 13 : 0, (byte) 0);
	
		generateOreRing(world, x, y, z, radius+1, depth);
		
		//Obsidian ladder pillar
		if(radius>9) {
			int offset = 5;
			BasicGenerator.generateCylinder(world, x, y-offset, z, 2, depth-offset, 49, (byte) 0);
			for(int yy=offset; yy<depth-offset; yy++) {
				int y2 = y - yy;
				int x2 = x + 2;
				
				for(int zz=-1; zz<=1; zz++) {
					int z2 = z + zz;
					
					Block block = world.getBlockAt(x2, y2, z2);
					if(block.getType()!=Material.BEDROCK && rand.nextInt(100)<40 || yy==offset/*first row*/) {
						block.setTypeId(65);
						block.setData((byte) 5);
					} else {
						block.setTypeId(0);
					}
				}
			}
		}
	}
	
	private static void generateOreRing(World world, int x, int y, int z, int radius, int depth) {
		int offset = 2;
		
		int chance = 50;
		int ironChance = 30;
		int goldChance = 15;
		int redstoneChance = 15;
		int diamondChance = 5;
		
		for(int xx=-radius; xx<=radius; xx++) {
			for(int zz=-radius; zz<=radius; zz++) {
				//Check für radius
				int size = (xx*xx) + (zz*zz);
				if ((size </*<=*/ (radius*radius))
				   && (size >= (radius-1)*(radius-1))) {
					int x2 = x + xx;
					int z2 = z + zz;
					
					for(int yy=0; yy<=depth; yy++) {
						int y2 = y - yy;
						
						//random offset
						int xOffset = -offset + rand.nextInt(offset*2+1);
						int yOffset = -offset + rand.nextInt(offset*2+1);
						int zOffset = -offset + rand.nextInt(offset*2+1);
						
						//chance
						if(-yy+yOffset<=0 && rand.nextInt(100)<chance) {
							Block block = world.getBlockAt(x2+xOffset, y2+yOffset, z2+zOffset);
							if(block.getType()!=Material.BEDROCK) {
								//random ores
								int id = 16;
								
								if(rand.nextInt(100)<ironChance) id = 15;
								if(rand.nextInt(100)<goldChance) id = 14;
								if(rand.nextInt(100)<redstoneChance) id = 73;
								if(rand.nextInt(100)<diamondChance) id = 56;
								
								block.setTypeId(id);
							}
						}
					}
				}
			}
		}
	}
}
