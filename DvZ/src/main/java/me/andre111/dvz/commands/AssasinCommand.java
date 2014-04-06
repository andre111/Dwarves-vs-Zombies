package me.andre111.dvz.commands;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.Game;

import org.bukkit.command.CommandSender;

public class AssasinCommand extends DvZCommand {
	//Chose Assasins
	public AssasinCommand(String name) {
		super(name);
	}

	@Override
	public boolean handle(int gameID, CommandSender sender, String[] args) {
		if(!sender.hasPermission("dvz.assasin")) {
			DvZ.sendPlayerMessageFormated(sender, "You don't have the Permission to do that!");
			return false;
		}
		
		Game game = getGameFromID(gameID, sender);
		
		if(game!=null) {
			int count = 1;
			if(args.length>1)
				count = Integer.parseInt(args[1].replace("+", ""));
			
			game.addAssasins(count);
		}
		
		return true;
	}
}
