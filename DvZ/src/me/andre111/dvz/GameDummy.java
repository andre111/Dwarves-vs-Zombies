package me.andre111.dvz;

import org.bukkit.Location;
import org.bukkit.block.Block;

public class GameDummy {
	public Location spawnDwarves;
	public Location spawnMonsters;
	
	public Location monument;
	public boolean monumentexists;
	
	//#######################################
	//Creating the Monument
	//#######################################
	public void createMonument(boolean obsi) {
		Block block = monument.getWorld().getBlockAt(monument);
		Block block2;

		for(int i=-1; i<=2; i++) {
			for(int j=-1; j<=2; j++) {
				if(obsi) {
					block2 = block.getRelative(i, 0, j);
					block2.setTypeId(49);
				}
			}
		}

		for(int i=0; i<=1; i++) {
			for(int j=0; j<=1; j++) {
				if(obsi) {
					block2 = block.getRelative(i, 1, j);
					block2.setTypeId(49);
					block2 = block.getRelative(i, 2, j);
					block2.setTypeId(49);
				}
				block2 = block.getRelative(i, 3, j);
				block2.setTypeId(116);
			}
		}
	}
}
