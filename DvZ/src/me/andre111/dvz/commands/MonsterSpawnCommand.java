package me.andre111.dvz.commands;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.Game;
import me.andre111.dvz.config.ConfigManager;
import me.andre111.dvz.utils.Slapi;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MonsterSpawnCommand extends DvZCommand {
	//Set the Monster Spawnpoint
	public MonsterSpawnCommand(String name) {
		super(name);
	}

	@Override
	public boolean handle(int gameID, CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Y U NO PLAYER??!111");
			return true;
		}
		Player player = (Player)sender;
		if(!sender.hasPermission("dvz.setspawn")) {
			sender.sendMessage("You don't have the Permission to do that!");
			return false;
		}
		
		if(gameID==-1) {
			DvZ.instance.getDummy().spawnMonsters = player.getLocation();
			sender.sendMessage(ConfigManager.getLanguage().getString("string_using_dummy","Using dummy Game"));
		} else {
			Game game = DvZ.instance.getGame(gameID);
			if(game!=null) sender.sendMessage(ConfigManager.getLanguage().getString("string_using_game","Using Game ID -0-").replace("-0-", ""+gameID));
			else sender.sendMessage(ConfigManager.getLanguage().getString("string_not_game","Game ID -0- does not exist/is not activated!").replace("-0-", ""+0));
			
			if(game!=null) game.spawnMonsters = player.getLocation();
		}
		
		player.sendMessage(ConfigManager.getLanguage().getString("string_setspawn_monster","Set Monster Spawn to your current Location!"));
		
		String path = Bukkit.getServer().getWorldContainer().getPath()+"/"+player.getWorld().getName()+"/dvz/dvz_spawn_m.dat";
		Location loc = player.getLocation();
		try {
			Slapi.save(loc.getX()+":"+loc.getY()+":"+loc.getZ()+":"+loc.getPitch()+":"+loc.getYaw(), path);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return true;
	}
}
