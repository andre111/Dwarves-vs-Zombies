package me.andre111.dvz.commands;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.Game;

import org.bukkit.command.CommandSender;

public class ReleaseCommand extends DvZCommand {
	//Release the monsters
	public ReleaseCommand(String name) {
		super(name);
	}

	@Override
	public boolean handle(int gameID, CommandSender sender, String[] args) {
		if(!sender.hasPermission("dvz.release")) {
			DvZ.sendPlayerMessageFormated(sender, "You don't have the Permission to do that!");
			return false;
		}

		Game game = getGameFromID(gameID, sender);
		
		if(game!=null) {
			game.release();
		}
		
		return true;
	}
}
