package me.andre111.dvz.generator;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.generator.BlockPopulator;

public class AntiLeavesPopulator extends BlockPopulator {

	@Override
	public void populate(World world, Random random, Chunk source) {
		int xP = source.getX() << 4;
		int zP = source.getZ() << 4;
		
		for(int x=0; x<32; x++) {
			for(int z=0; z<32; z++) {
				for(int y=0; y<world.getMaxHeight(); y++) {
					Block block = world.getBlockAt(x+xP, y, z+zP);
					if(block.getType()==Material.LEAVES) {
						block.setType(Material.AIR);
					}
				}
			}
		}
	}

}
