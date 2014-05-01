package me.andre111.dvz.commands;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.Game;
import me.andre111.dvz.teams.Team;

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
			if(args.length>1) {
				Team team = game.teamSetup.getTeam(args[1]);
				if(team!=null) {
					int percent = 1;
					if(args.length>2)
						percent = Integer.parseInt(args[2].replace("+", ""));
					
					game.addAssasins(team, percent);
					return true;
				}
			}
		}
		
		return false;
	}
}
