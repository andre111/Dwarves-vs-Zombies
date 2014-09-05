package me.andre111.dvz.commands;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.Game;
import me.andre111.dvz.config.ConfigManager;
import me.andre111.dvz.manager.WorldManager;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VoteCommand extends DvZCommand {
	
	public VoteCommand(String name) {
		super(name);
	}

	@Override
	public boolean handle(int gameID, CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			DvZ.sendPlayerMessageFormated(sender, "Y U NO PLAYER??!111");
			return true;
		}
		Player player = (Player)sender;
		if(!sender.hasPermission("dvz.vote")) {
			DvZ.sendPlayerMessageFormated(sender, "You don't have the Permission to do that!");
			return false;
		}

		if(args.length>0) {
			Game game = DvZ.instance.getPlayerGame(player.getUniqueId());
			
			if(game!=null) {
				if(game.acceptsVotes()) {
					try {
						int id = Integer.parseInt(args[0]);
						if(game.vote(player, id)) {
							DvZ.sendPlayerMessageFormated(player, ConfigManager.getLanguage().getString("string_vote_success", "You voted for -0-!").replace("-0-", WorldManager.getWorldName(DvZ.instance.getGameID(game), id-1)));
						}
						return true;
					} catch(NumberFormatException e) {
					}
				}
			}
		}

		return false;
	}
}
