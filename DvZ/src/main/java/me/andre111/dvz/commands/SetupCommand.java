package me.andre111.dvz.commands;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.Game;
import me.andre111.dvz.teams.GameTimer;

import org.bukkit.command.CommandSender;

public class SetupCommand extends DvZCommand {
	//Release the monsters
	public SetupCommand(String name) {
		super(name);
	}

	@Override
	public boolean handle(int gameID, CommandSender sender, String[] args) {
		if(!sender.hasPermission("dvz.setup")) {
			DvZ.sendPlayerMessageFormated(sender, "You don't have the Permission to do that!");
			return false;
		}

		Game game = getGameFromID(gameID, sender);
		
		if(game!=null) {
			if(args.length>0) {
				if(args[0].equalsIgnoreCase("finishtimer")) {
					String timername = game.teamSetup.getDisplayedTimer();
					if(args.length>1) {
						timername = args[1];
					}
					
					GameTimer timer = game.teamSetup.getTimer(timername);
					if(timer!=null) {
						timer.finish();
						
						return true;
					}
				}
			}
		}
		
		return false;
	}
}
