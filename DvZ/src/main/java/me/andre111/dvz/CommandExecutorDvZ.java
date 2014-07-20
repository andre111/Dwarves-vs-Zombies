package me.andre111.dvz;

import me.andre111.dvz.commands.DvZCommand;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandExecutorDvZ implements CommandExecutor {
	public CommandExecutorDvZ(){
		DvZCommand.initCommands();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		//trick to make /dvz start work like /dvz_start
		if(command.getName().equalsIgnoreCase("dvz")) {
			if(args.length>0) {
				if(sender instanceof Player) {
					StringBuilder argstring = new StringBuilder();
					for(int i=1; i<args.length; i++) {
						argstring.append(" " + args[i]);
					}
					
					((Player) sender).performCommand("dvz_"+args[0]+argstring.toString());
					return true;
				}
			} 
			return false;
		//normal command
		} else {
			return onCommandIntern(sender, command, label, args);
		}
	}
	
	private boolean onCommandIntern(CommandSender sender, Command command, String label, String[] args) {
		int gameID = -1;
		try {
			if(args.length>0) gameID = Integer.parseInt(args[0].replace("+", ""));
		} catch (Exception e) {}
		
		//new commandsystem
		DvZCommand dvzCommand = DvZCommand.getCommand(command.getName().toLowerCase());
		if(dvzCommand!=null) return dvzCommand.handle(gameID, sender, args);
		
		return false;
	}
}