package me.andre111.dvz.commands;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.config.ConfigManager;
import me.andre111.dvz.manager.HighscoreManager;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HighscoreCommand extends DvZCommand {
	//get some information about the game
	public HighscoreCommand(String name) {
		super(name);
	}

	@Override
	public boolean handle(int gameID, CommandSender sender, String[] args) {
		if(!sender.hasPermission("dvz.highscore")) {
			DvZ.sendPlayerMessageFormated(sender, "You don't have the Permission to do that!");
			return false;
		}
		if (!(sender instanceof Player)) {
			DvZ.sendPlayerMessageFormated(sender, "Y U NO PLAYER??!111");
			return true;
		}
		Player player = (Player)sender;
		
		HashMap<UUID, Integer> pointMap = HighscoreManager.getPoints();
		int topCount = 5;
		
		//own
		int ownpoints = 0;
		int position = 0;
		if(pointMap.containsKey(player.getName())) {
			ownpoints = pointMap.get(player.getName());
		}
		
		//sort
		ValueComparator bvc = new ValueComparator(pointMap);
        TreeMap<UUID, Integer> sorted_map = new TreeMap<UUID, Integer>(bvc);
        sorted_map.putAll(pointMap);
		
        DvZ.sendPlayerMessageFormated(player, ConfigManager.getLanguage().getString("highscore_leaders", "Highscore Leaders: "));
        //print top
        for(int i=0; i<pointMap.size(); i++) {
        	UUID pname = sorted_map.lastKey();
        	int ppoints = pointMap.get(pname);
        	sorted_map.remove(pname);
        	
        	if(i<topCount) {
        		DvZ.sendPlayerMessageFormated(player, (i+1)+". "+pname+" - "+ppoints);
        	}
        	
        	if(pname.equals(player.getName())) {
        		position = i+1;
        	}
        }
        
        //print own
        if(position>topCount) {
        	DvZ.sendPlayerMessageFormated(player, "-> "+position+". "+player.getName()+" - "+ownpoints);
        }
        
		return true;
	}
}

class ValueComparator implements Comparator<UUID> {

    Map<UUID, Integer> base;
    public ValueComparator(Map<UUID, Integer> base) {
        this.base = base;
    }

    // Note: this comparator imposes orderings that are inconsistent with equals.    
    public int compare(UUID a, UUID b) {
        if (base.get(a) >= base.get(b)) {
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys
    }
}