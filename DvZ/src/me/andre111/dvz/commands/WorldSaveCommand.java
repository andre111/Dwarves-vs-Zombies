package me.andre111.dvz.commands;

import me.andre111.dvz.manager.WorldManager;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WorldSaveCommand extends DvZCommand {
	//Save a Backup of the DvZ_Main world
	public WorldSaveCommand(String name) {
		super(name);
	}

	@Override
	public boolean handle(int gameID, CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Y U NO PLAYER??!111");
			return true;
		}
		Player player = (Player)sender;
		if(!sender.hasPermission("dvz.save")) {
			sender.sendMessage("You don't have the Permission to do that!");
			return false;
		}
		
		WorldManager.saveWorld(sender, player.getWorld().getName());
		
		return true;
	}
}
