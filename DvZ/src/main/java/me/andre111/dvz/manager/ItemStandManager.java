package me.andre111.dvz.manager;

import java.io.File;
import java.util.ArrayList;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.utils.Item3DHandler.Item3DRunnable;
import me.andre111.dvz.utils.Slapi;
import me.andre111.items.ItemHandler;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class ItemStandManager {
	//private Game game;
	
	public ItemStandManager(/*Game g*/) {
		//game = g;
	}
	

	public void loadStands(World w, File directory) {
		File f = new File(directory, "0.dat");
		int count = 0;
		
		while(f.exists()) {
			try {
				String st = (String) Slapi.load(f.getAbsolutePath());
				String[] split = st.split("//");
				
				int x = Integer.parseInt(split[0]);
				int y = Integer.parseInt(split[1]);
				int z = Integer.parseInt(split[2]);
				int itemID = Integer.parseInt(split[3]);
				boolean once = Boolean.parseBoolean(split[4]);
				String item = split[5];
				boolean onlyClicking = Boolean.parseBoolean(split[5]);
				
				Location loc = new Location(w, x, y, z);
				
				createStand(loc, itemID, once, item, onlyClicking);
			} catch (Exception e) {
				DvZ.log("Could not read Itemstandinfo!");
				e.printStackTrace();
			}
			
			count++;
			f = new File(directory, count+".dat");
		}
	}
	
	public void createAndSaveStand(File directory, Location loc, int itemID, boolean once, String formatedItem, boolean onlyClicking) {
		createStand(loc, itemID, once, formatedItem, onlyClicking);
		
		int x = loc.getBlockX();
		int y = loc.getBlockY();
		int z = loc.getBlockZ();
		
		String save = x+"//"+y+"//"+z+"//"+itemID+"//"+once+"//"+formatedItem+"//"+onlyClicking;
		
		File f = new File(directory, "0.dat");
		int count = 0;
		while(f.exists()) {
			count++;
			f = new File(directory, count+".dat");
		}
		
		try {
			Slapi.save(save, f.getPath());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*public void saveStands(File directory) {
		
	}*/
	
	private void createStand(Location loc, final int itemID, final boolean once, final String formatedItem, final boolean onlyClicking) {
		DvZ.item3DHandler.spawnAroundBlock(null, loc, itemID, new Item3DRunnable() {
			private ArrayList<String> players = new ArrayList<String>();
			
			@Override
			public void run(Player player) {
				if(once && players.contains(player.getName())) return;
				
				ItemStack it = ItemHandler.decodeItem(formatedItem, null);
					
				if(it!=null) {
					Item ie = player.getWorld().dropItemNaturally(player.getLocation().clone().add(0, 1, 0), it);
					if (onlyClicking) {
						ie.setMetadata("dvz_onlyPickup", new FixedMetadataValue(DvZ.instance, player.getName()));
					}
				}
				
				players.add(player.getName());
			}
		});
	}
}
