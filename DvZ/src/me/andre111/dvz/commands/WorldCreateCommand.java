package me.andre111.dvz.commands;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.manager.WorldManager;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WorldCreateCommand extends DvZCommand {
	//Save a Automap of the world
	public WorldCreateCommand(String name) {
		super(name);
	}

	@Override
	public boolean handle(int gameID, CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			DvZ.sendPlayerMessageFormated(sender, "Y U NO PLAYER??!111");
			return true;
		}
		Player player = (Player)sender;
		if(!sender.hasPermission("dvz.save")) {
			DvZ.sendPlayerMessageFormated(sender, "You don't have the Permission to do that!");
			return false;
		}
		if(args.length>0) {
			WorldManager.createWorld(sender, player.getWorld().getName(), args[0]);
			return true;
		}
		
		return false;
	}
}
