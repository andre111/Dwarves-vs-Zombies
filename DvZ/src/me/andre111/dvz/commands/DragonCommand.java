package me.andre111.dvz.commands;

import java.util.Random;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.Game;
import me.andre111.dvz.config.ConfigManager;
import me.andre111.dvz.dragon.PlayerDragon;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DragonCommand extends DvZCommand {
	//become the dragen
	public DragonCommand(String name) {
		super(name);
	}

	@Override
	public boolean handle(int gameID, CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			DvZ.sendPlayerMessageFormated(sender, "Y U NO PLAYER??!111");
			return true;
		}
		Player player = (Player)sender;
		if(!sender.hasPermission("dvz.dragon")) {
			DvZ.sendPlayerMessageFormated(sender, "You don't have the Permission to do that!");
			return false;
		}
		
		//int type = 1;
		//if(args.length>0)
		//	type = Integer.parseInt(args[0].replace("+", ""));
		Game game = DvZ.instance.getPlayerGame(player.getUniqueId());

		if(game != null) {
			int dID = -1;
			if(args.length>0) dID = Integer.parseInt(args[0].replace("+", ""));
			if(dID<0 || dID>DvZ.dragonAtManager.getMaxDragonCount()) {
				dID = (new Random()).nextInt(DvZ.dragonAtManager.getMaxDragonCount()+1);
				DvZ.sendPlayerMessageFormated(player, ConfigManager.getLanguage().getString("string_random_dragon", "Choosing a random Dragon!"));
			}
			DvZ.sendPlayerMessageFormated(player, ConfigManager.getLanguage().getString("string_become_dragon", "You became the -0-!").replace("-0-", DvZ.dragonAtManager.getDragon(dID).getName()));
			
			PlayerDragon dragon = new PlayerDragon(player);
			
			dragon.setID(dID);
			dragon.init();
			
			game.setDragon(dragon);
		} else {
			DvZ.sendPlayerMessageFormated(player, ConfigManager.getLanguage().getString("string_need_game", "You have to be a player in a game to become the Dragon!"));
		}
		/*DvZ.disguiseP(player, new Disguise(DvZ.api.newEntityID(), "", DisguiseType.EnderDragon));
		player.setAllowFlight(true);
		player.setFlying(true);
		player.setMaxHealth(200);
		player.setHealth(200);
		plugin.getGame(0).setPlayerState(player.getName(), 100);*/
		
		return true;
	}
}
