package me.andre111.dvz.generator;

import me.andre111.dvz.config.ConfigManager;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class DvZWorldProvider implements Listener {
	
	public DvZWorldProvider(Plugin plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	
	public static void provideWorld(int gameID) {
		
	}
	
	//TODO - disabled until I wfind a way to make it work without problems
	/*public static World generateWorld() {
		WorldCreator wc = new WorldCreator(ConfigManager.getStaticConfig().getString("world_prefix", "DvZ_")+"testing");
		World wtest = Bukkit.getServer().createWorld(wc);
		
		return wtest;
	}
	
	@EventHandler
	public void onWorldInit(WorldInitEvent event) {
		//starts with dvz
		if(!event.getWorld().getName().startsWith(ConfigManager.getStaticConfig().getString("world_prefix", "DvZ_"))) return;
		
		World world = event.getWorld();
		world.setSpawnLocation(0, world.getHighestBlockYAt(0, 0), 0);
		
        world.getPopulators().add(new AntiLeavesPopulator());
        world.getPopulators().add(new DwarfPopulator());
        world.getPopulators().add(new PathPopulator());
    }
	
	@EventHandler
	public void onChunkPopulate(ChunkLoadEvent event) {
		//starts with dvz
		if(!event.getWorld().getName().startsWith(ConfigManager.getStaticConfig().getString("world_prefix", "DvZ_"))) return;
		if(!event.isNewChunk()) return;
		
		int x1 = event.getChunk().getX() >> 4;
		int z1 = event.getChunk().getZ() >> 4;
		
		for(int x=0; x<16; x++) {
			for(int z=0; z<16; z++) {
				//if(event.getWorld().getBiome(x+x1, z+z1)==Biome.JUNGLE)
					event.getWorld().setBiome(x+x1, z+z1, Biome.FOREST);
			}
		}
	}*/
	
	public static World generateNewWorld() {
		WorldCreator wc = new WorldCreator(ConfigManager.getStaticConfig().getString("world_prefix", "DvZ_")+"testing");
		wc = wc.generator(new ChunkGeneratorDvZ());
		
		World wtest = Bukkit.getServer().createWorld(wc);
		return wtest;
	}
}
