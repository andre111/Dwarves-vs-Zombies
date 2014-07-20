package me.andre111.dvz.generator;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

public class DwarfPopulator extends BlockPopulator {
	@Override
	public void populate(World world, Random random, Chunk source) {
		int cX = source.getX();
		int cZ = source.getZ();
		
		if(cX==DVZGeneratorPositions.QUARRY_CHUNK_X && cZ==DVZGeneratorPositions.QUARRY_CHUNK_Z) {
			int x = cX << 4;
			int z = cZ << 4;
			int y = world.getHighestBlockAt(x, z).getY()-1;
			QuarryGenerator.generateQuarry(world, x, y, z, DVZGeneratorPositions.QUARRY_RADIUS, 200);
		}
	}

}
