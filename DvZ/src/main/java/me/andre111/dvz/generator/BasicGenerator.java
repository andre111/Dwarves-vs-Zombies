package me.andre111.dvz.generator;

import me.andre111.dvz.volatileCode.DeprecatedMethods;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public abstract class BasicGenerator {

	public static void generateCylinder(Location loc, int radius, int depth, Material mat, byte data) {
		generateCylinder(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), radius, depth, mat, data);
	}
	
	public static void generateCylinder(World world, int x, int y, int z, int radius, int depth, Material mat, byte data) {
		for(int xx=-radius; xx<=radius; xx++) {
			for(int zz=-radius; zz<=radius; zz++) {
				//Check für radius
				if ((xx*xx) + (zz*zz) </*<=*/ (radius*radius)){
					int x2 = x + xx;
					int z2 = z + zz;
					
					for(int yy=0; yy<=depth; yy++) {
						int y2 = y - yy;
						
						if(y2>=0) {
							Block block = world.getBlockAt(x2, y2, z2);
							if(block.getType()!=Material.BEDROCK) {
								block.setType(mat);
								DeprecatedMethods.setBlockData(block, data);
							}
						}
					}
				}
			}
		}
	}
}
