package me.andre111.dvz.commands;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.Game;
import me.andre111.dvz.config.ConfigManager;

import org.bukkit.command.CommandSender;

public class StartCommand extends DvZCommand {
	//Start the Game
	public StartCommand(String name) {
		super(name);
	}

	@Override
	public boolean handle(int gameID, CommandSender sender, String[] args) {
		if(!sender.hasPermission("dvz.start")) {
			DvZ.sendPlayerMessageFormated(sender, "You don't have the Permission to do that!");
			return false;
		}
		
		Game game = getGameFromID(gameID, sender);
		
		if(game!=null) {
			int time = 1;
			if(args.length>1)
				time = Integer.parseInt(args[1].replace("+", ""));
			else
				game.broadcastMessage(ConfigManager.getLanguage().getString("string_starting_instant","Starting instantly!"));
			
			game.start(time);
		}
		return true;
	}
}
