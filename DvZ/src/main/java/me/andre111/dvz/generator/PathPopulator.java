package me.andre111.dvz.generator;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.generator.BlockPopulator;

public class PathPopulator extends BlockPopulator {
	private int halfsize = 2;

	@Override
	public void populate(World world, Random random, Chunk source) {
		int cX = source.getX();
		int cZ = source.getZ();
		
		if(cX==DVZGeneratorPositions.DWARF_CHUNK_X && cZ==DVZGeneratorPositions.DWARF_CHUNK_Y) {
			int startX = cX << 4;
			int startZ = cZ << 4;
			
			int targetX = DVZGeneratorPositions.MONSTER_CHUNK_X << 4;
			int targetZ = DVZGeneratorPositions.MONSTER_CHUNK_Z << 4;
			
			int xP = startX;
			int zP = startZ;
			
			while(xP!=targetX || zP!=targetZ) {
				if(xP<targetX) xP+=1; 
				else if(xP>targetX) xP-=1;
				
				if(zP<targetZ) zP+=1; 
				else if(zP>targetZ) zP-=1;
				
				//System.out.println(xP+" "+targetX+" "+zP+" "+targetZ);
				
				for(int x2=-halfsize; x2<=halfsize; x2++) {
					for(int z2=-halfsize; z2<=halfsize; z2++) {
						Block highest = world.getHighestBlockAt(xP+x2, zP+z2);
						//move over water
						//while(highest.getType()==Material.STATIONARY_WATER || highest.getType()==Material.SAND || highest.getType()==Material.WATER) {
						//	highest = highest.getRelative(0, 1, 0);
						//}
						
						//set gravel
						if(highest.getRelative(0, -1, 0).getType()!=DVZGeneratorPositions.PATH_MATERIAL) {
							highest.setType(DVZGeneratorPositions.PATH_MATERIAL);
							
							//pilars
							if(shouldFence(highest.getRelative(0, -1, 0).getType())) {
								highest = highest.getRelative(0, -1, 0);
								highest.setType(Material.FENCE);
							}
							
							if(xP%10==0 || zP%10==0) {
								while(shouldFence(highest.getRelative(0, -1, 0).getType())) {
									highest = highest.getRelative(0, -1, 0);
									highest.setType(Material.FENCE);
								}
							}
						}
					}
				}
			}
		}
	}

	public boolean shouldFence(Material mat) {
		if(mat!=Material.STONE
		 && mat!=Material.SAND
		 && mat!=Material.GRASS
		 && mat!=Material.DIRT) {
			return true;
		}
		
		return false;
	}
}
