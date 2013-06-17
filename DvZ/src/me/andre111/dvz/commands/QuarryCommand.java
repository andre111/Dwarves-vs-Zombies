package me.andre111.dvz.commands;

import me.andre111.dvz.generator.QuarryGenerator;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class QuarryCommand extends DvZCommand {
	//Create a quarry
	public QuarryCommand(String name) {
		super(name);
	}

	@Override
	public boolean handle(int gameID, CommandSender sender, String[] args) {
		if(!sender.hasPermission("dvz.quarry")) {
			sender.sendMessage("You don't have the Permission to do that!");
			return false;
		}
		if (!(sender instanceof Player)) {
			sender.sendMessage("Y U NO PLAYER??!111");
			return true;
		}
		Player player = (Player)sender;
		if(args.length<1) {
			player.sendMessage("Please specify a radius!");
			return false;
		}
		int radius = Integer.parseInt(args[0]);
		
		Location loc = player.getLocation();
		QuarryGenerator.generateQuarry(player.getLocation().clone().subtract(0, 1, 0), radius, 200);
		
		player.teleport(loc.clone().add(0, 10, 0));
		
		return true;
	}
}
