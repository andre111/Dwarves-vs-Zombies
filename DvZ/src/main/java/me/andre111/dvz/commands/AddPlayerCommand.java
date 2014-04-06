package me.andre111.dvz.commands;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.Game;
import me.andre111.dvz.config.ConfigManager;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AddPlayerCommand extends DvZCommand {
	//Add a new Player to the game
	public AddPlayerCommand(String name) {
		super(name);
	}

	@Override
	public boolean handle(int gameID, CommandSender sender, String[] args) {
		if(!sender.hasPermission("dvz.add")) {
			DvZ.sendPlayerMessageFormated(sender, "You don't have the Permission to do that!");
			return false;
		}
		
		Game game = getGameFromID(gameID, sender);
		
		if(game!=null) {
			Player player = null;
			if(args.length>1)
				player = Bukkit.getServer().getPlayerExact(args[1]);
			
			if(player!=null) {
				DvZ.instance.joinGame(player, game, true);
			} else {
				DvZ.sendPlayerMessageFormated(sender, ConfigManager.getLanguage().getString("string_noplayer","No Player found with that Name!"));
				return false;
			}
		}
		
		return true;
	}
}
