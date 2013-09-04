package me.andre111.dvz.commands;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.Game;
import me.andre111.dvz.config.ConfigManager;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LeaveCommand extends DvZCommand {
	//Leave the game
	public LeaveCommand(String name) {
		super(name);
	}

	@Override
	public boolean handle(int gameID, CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Y U NO PLAYER??!111");
			return true;
		}
		Player player = (Player)sender;
		if(!sender.hasPermission("dvz.leave")) {
			sender.sendMessage("You don't have the Permission to do that!");
			return false;
		}

		Game game = DvZ.instance.getPlayerGame(player.getName());

		if(game!=null) {
			if(game.getState()==1 && game.getPlayerState(player.getName())==1) {
				game.removePlayer(player.getName());

				if(ConfigManager.getStaticConfig().getString("use_lobby", "true").equals("true"))
					player.teleport(Bukkit.getServer().getWorlds().get(0).getSpawnLocation());
			}
		}

		return true;
	}
}
