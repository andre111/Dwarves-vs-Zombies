package me.andre111.dvz.manager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.config.DVZFileConfiguration;
import me.andre111.dvz.utils.Item3DHandler.Item3DRunnable;
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
		/*File f = new File(directory, "0.dat");
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
		}*/

		//NEW
		File file = new File(directory, "itemstands.yml");
		if(file.exists()) {
			DVZFileConfiguration saveF = DVZFileConfiguration.loadConfiguration(file);
			for(String key : saveF.getConfigurationSection("itemstands").getKeys(false)) {
				
				int x = saveF.getInt("itemstands."+key+".x");
				int y = saveF.getInt("itemstands."+key+".y");
				int z = saveF.getInt("itemstands."+key+".z");
				int itemID = saveF.getInt("itemstands."+key+".itemID");
				boolean once = saveF.getBoolean("itemstands."+key+".once");
				String formatedItem = saveF.getString("itemstands."+key+".formatedItem");
				boolean onlyClicking = saveF.getBoolean("itemstands."+key+".onlyClicking");

				Location loc = new Location(w, x, y, z);

				createStand(loc, itemID, once, formatedItem, onlyClicking);
			}
		}
	}

	public void createAndSaveStand(File directory, Location loc, int itemID, boolean once, String formatedItem, boolean onlyClicking) {
		createStand(loc, itemID, once, formatedItem, onlyClicking);

		int x = loc.getBlockX();
		int y = loc.getBlockY();
		int z = loc.getBlockZ();

		//NEW
		File file = new File(directory, "itemstands.yml");
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
			}
		}

		DVZFileConfiguration saveF = DVZFileConfiguration.loadConfiguration(file);
		int pos = 0;
		while(saveF.isSet("itemstands."+pos)) {
			pos++;
		}
		saveF.set("itemstands."+pos+".x", x);
		saveF.set("itemstands."+pos+".y", y);
		saveF.set("itemstands."+pos+".z", z);
		saveF.set("itemstands."+pos+".itemID", itemID);
		saveF.set("itemstands."+pos+".once", once);
		saveF.set("itemstands."+pos+".formatedItem", formatedItem);
		saveF.set("itemstands."+pos+".onlyClicking", onlyClicking);

		try {
			saveF.save(file);
		} catch (IOException e) {
		}
	}

	private void createStand(Location loc, final int itemID, final boolean once, final String formatedItem, final boolean onlyClicking) {
		DvZ.item3DHandler.spawnAroundBlock(null, loc, itemID, new Item3DRunnable() {
			private ArrayList<String> players = new ArrayList<String>();

			@Override
			public void run(Player player) {
				if(once && players.contains(player.getUniqueId().toString())) return;

				ItemStack it = ItemHandler.decodeItem(formatedItem, null);

				if(it!=null) {
					Item ie = player.getWorld().dropItemNaturally(player.getLocation().clone().add(0, 1, 0), it);
					if (onlyClicking) {
						ie.setMetadata("dvz_onlyPickup", new FixedMetadataValue(DvZ.instance, player.getUniqueId().toString()));
					}
				}

				players.add(player.getUniqueId().toString());
			}
		});
	}
}
