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
		BasicGenerator.generateCylinder(world, x, y, z, radius+4, depth, Material.STONE, (byte) 0);
		
		//basic gravel stuff
		boolean gravel = true; //true; //disbaled for testing
		BasicGenerator.generateCylinder(world, x, y, z, radius, depth, gravel ? Material.GRAVEL : Material.AIR, (byte) 0);
	
		//ores
		generateOreRing(world, x, y, z, radius+1, depth);
		
		//top
		if(gravel) {
			generateTop(world, x, y, z, radius-2, depth, 2, 8);
			generateTop(world, x, y, z, radius, depth, 2, 6);
			generateTop(world, x, y, z, radius+2, depth, 2, 4);
			generateTop(world, x, y-2, z, radius+4, depth, 3, 5);
			generateTop(world, x, y-2, z, radius+5, depth, 2, 4);
			generateTop(world, x, y-3, z, radius+6, depth, 2, 4);
		}
		
		//Obsidian ladder pillar
		if(radius>9) {
			int offset = 5;
			BasicGenerator.generateCylinder(world, x, y-offset, z, 2, depth-offset, Material.OBSIDIAN, (byte) 0);
			for(int yy=offset; yy<depth-offset; yy++) {
				int y2 = y - yy;
				int x2 = x + 2;
				
				for(int zz=-1; zz<=1; zz++) {
					int z2 = z + zz;
					
					Block block = world.getBlockAt(x2, y2, z2);
					if(block.getType()!=Material.BEDROCK && rand.nextInt(100)<40 || yy==offset/*first row*/) {
						if(y2>0) {
							block.setType(Material.LADDER);
							block.setData((byte) 5);
						}
					} else {
						if(y2>0) {
							block.setType(Material.AIR);
						}
					}
				}
			}
		}
	}
	
	private static void generateOreRing(World world, int x, int y, int z, int radius, int depth) {
		int offset = 2;
		
		int chance = 90;
		int ironChance = 50;
		int goldChance = 30;
		int redstoneChance = 20;
		int diamondChance = 5;
		int emeraldChance = 5;
		
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
						if(-yy+yOffset<=0 && rand.nextInt(100)<chance && y2+yOffset>=0) {
							Block block = world.getBlockAt(x2+xOffset, y2+yOffset, z2+zOffset);
							if(block.getType()!=Material.BEDROCK) {
								//random ores
								Material mat = Material.COAL_ORE;
								
								if(rand.nextInt(100)<ironChance) mat = Material.IRON_ORE;
								if(rand.nextInt(100)<goldChance) mat = Material.GOLD_ORE;
								if(rand.nextInt(100)<redstoneChance) mat = Material.REDSTONE_ORE;
								if(rand.nextInt(100)<emeraldChance) mat = Material.EMERALD_ORE;
								if(rand.nextInt(100)<diamondChance) mat = Material.DIAMOND_ORE;
								
								block.setType(mat);
							}
						}
					}
				}
			}
		}
	}
	
	private static void generateTop(World world, int x, int y, int z, int radius, int depth, int minHeight, int maxHeight) {
		for(int xx=-radius; xx<=radius; xx++) {
			for(int zz=-radius; zz<=radius; zz++) {
				//Check für radius
				int size = (xx*xx) + (zz*zz);
				if (size </*<=*/ (radius*radius)) {
					int x2 = x + xx;
					int z2 = z + zz;
					
					for(int yy=0; yy<=minHeight+rand.nextInt(maxHeight-minHeight); yy++) {
						int y2 = y + yy;
						
						Block block = world.getBlockAt(x2, y2, z2);
						if(block.getType()!=Material.BEDROCK) {
							block.setType(Material.GRAVEL);
						}
					}
				}
			}
		}
	}
}
