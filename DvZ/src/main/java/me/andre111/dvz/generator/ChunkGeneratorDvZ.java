package me.andre111.dvz.generator;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import me.andre111.dvz.volatileCode.DeprecatedMethods;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

public class ChunkGeneratorDvZ extends ChunkGenerator {
	byte[] result;
	
	private int waterlevel = 56;

	//This needs to be set to return true to override minecraft's default behaviour
	@Override
	public boolean canSpawn(World world, int x, int z) {
		return true;
	}
	//This converts relative chunk locations to bytes that can be written to the chunk
	public int xyzToByte(int x, int y, int z) {
		return (x * 16 + z) * 128 + y;
	}

	@Override
	public byte[] generate(World world, Random rand, int chunkx, int chunkz) {
		result = new byte[32768];
		for(int x=0; x<16; x++)
			for(int z=0; z<16; z++) {
				int trueX = chunkx*16+x;
				int trueZ = chunkz*16+z;
				
				double h = DvZGeneratorFunction.get(trueX, trueZ, world.getSeed())*127;
				int y;
				int yt;
				//stone and grass
				for(y=0; y<h; y++) {
					result[xyzToByte(x,y,z)] = (byte) DeprecatedMethods.getMaterialID(Material.STONE);
					
					//grass
					if(y>=h-1) {
						result[xyzToByte(x,y,z)] = (byte) DeprecatedMethods.getMaterialID(Material.GRASS);
					//dirt
					} else if(y>=h-5) {
						result[xyzToByte(x,y,z)] = (byte) DeprecatedMethods.getMaterialID(Material.DIRT);
					}
				}
				//water and sand
				yt = y;
				for(y-=3; y<waterlevel; y++) {
					if(y<yt)
						result[xyzToByte(x,y,z)] = (byte) DeprecatedMethods.getMaterialID(Material.SAND);
					else
						result[xyzToByte(x,y,z)] = (byte) DeprecatedMethods.getMaterialID(Material.STATIONARY_WATER);
				}
				//This will set the floor of each chunk at bedrock level to bedrock
				result[xyzToByte(x,0,z)] = (byte) DeprecatedMethods.getMaterialID(Material.BEDROCK);
			}
		return result;
	}
	
	@Override
    public List<BlockPopulator> getDefaultPopulators(World world) {
        return Arrays.asList(
        		(BlockPopulator)new SpawnPopulator(), 
        		new BiomePopulator(),
        		new TreeLogPopulator(),
        		new DwarfPopulator(),
        		new PathPopulator());
    }
	
	@Override
    public Location getFixedSpawnLocation(World world, Random random) {
        int x = 0;//random.nextInt(200) - 100;
        int z = 0;//random.nextInt(200) - 100;
        int y = world.getHighestBlockYAt(x, z);
        return new Location(world, x, y, z);
    }
}
