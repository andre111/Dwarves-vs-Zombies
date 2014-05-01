package me.andre111.dvz.commands;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.Game;
import me.andre111.dvz.config.ConfigManager;
import me.andre111.dvz.utils.Slapi;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MonumentCommand extends DvZCommand {
	//create the dwarf monument
	public MonumentCommand(String name) {
		super(name);
	}

	@Override
	public boolean handle(int gameID, CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			DvZ.sendPlayerMessageFormated(sender, "Y U NO PLAYER??!111");
			return true;
		}
		Player player = (Player)sender;
		if(!sender.hasPermission("dvz.monument")) {
			DvZ.sendPlayerMessageFormated(sender, "You don't have the Permission to do that!");
			return false;
		}
		
		//TODO - remove and replace command for teams
		/*if(gameID==-1) {
			DvZ.sendPlayerMessageFormated(sender, ConfigManager.getLanguage().getString("string_using_dummy","Using dummy Game"));
			
			DvZ.instance.getDummy().monument = player.getLocation();
			DvZ.instance.getDummy().monumentexists = true;
			player.teleport(new Location(DvZ.instance.getDummy().monument.getWorld(), DvZ.instance.getDummy().monument.getBlockX(), DvZ.instance.getDummy().monument.getBlockY()+4, DvZ.instance.getDummy().monument.getBlockZ()));
			DvZ.instance.getDummy().createMonument(true);
		} else {
			Game game = DvZ.instance.getGame(gameID);
			if(game!=null) DvZ.sendPlayerMessageFormated(sender, ConfigManager.getLanguage().getString("string_using_game","Using Game ID -0-").replace("-0-", ""+gameID));
			else DvZ.sendPlayerMessageFormated(sender, ConfigManager.getLanguage().getString("string_not_game","Game ID -0- does not exist/is not activated!").replace("-0-", ""+0));
			
			if(game!=null) {
				game.monument = player.getLocation();
				game.monumentexists = true;
				player.teleport(new Location(game.monument.getWorld(), game.monument.getBlockX(), game.monument.getBlockY()+4, game.monument.getBlockZ()));
				game.createMonument(true);
			}
		}*/
		
		String path = Bukkit.getServer().getWorldContainer().getPath()+"/"+player.getWorld().getName()+"/dvz/dvz_mon.dat";
		try {
			Slapi.save(player.getLocation().getBlockX()+":"+player.getLocation().getBlockY()+":"+player.getLocation().getBlockZ(), path);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return true;
	}
}
