package me.andre111.dvz.commands;

import java.io.File;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.config.ConfigManager;
import me.andre111.dvz.manager.BlockManager;
import me.andre111.dvz.manager.WorldManager;
import me.andre111.items.SpellItems;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class ReloadCommand extends DvZCommand {
	//Reloads a specific config or the whole plugins
	public ReloadCommand(String name) {
		super(name);
	}

	@Override
	public boolean handle(int gameID, CommandSender sender, String[] args) {
		if(!sender.hasPermission("dvz.reload")) {
			DvZ.sendPlayerMessageFormated(sender, "You don't have the Permission to do that!");
			return false;
		}
		
		
		if(args.length>0) {
			String rs = args[0];
			boolean reload = false;
			
			if(rs.equalsIgnoreCase("plugin")) {
				Bukkit.getServer().getPluginManager().disablePlugin(DvZ.instance);
				Bukkit.getServer().getPluginManager().enablePlugin(DvZ.instance);
				
				reload = true;
			}
			if(rs.equalsIgnoreCase("classes") || rs.equalsIgnoreCase("all")) {
				ConfigManager.reloadConfig("classes");
				DvZ.classManager.reload();
				reload = true;
			}
			//if(rs.equalsIgnoreCase("monsters") || rs.equalsIgnoreCase("all")) {
			//	ConfigManager.reloadConfig("monsters");
			//	DvZ.monsterManager.reload();
			//	reload = true;
			//}
			if(rs.equalsIgnoreCase("items") || rs.equalsIgnoreCase("all")) {
				ConfigManager.reloadConfig("items");
				DvZ.reloadItems();
				reload = true;
			}
			if(rs.equalsIgnoreCase("spells") || rs.equalsIgnoreCase("all")) {
				SpellItems.luacontroller.loadScript(new File(DvZ.instance.getDataFolder(), "spells.lua").getAbsolutePath());
				reload = true;
			}
			if(rs.equalsIgnoreCase("enchantments") || rs.equalsIgnoreCase("all")) {
				ConfigManager.reloadConfig("items");
				DvZ.reloadItems();
				reload = true;
			}
			if(rs.equalsIgnoreCase("rewards") || rs.equalsIgnoreCase("all")) {
				ConfigManager.reloadConfig("rewards");
				DvZ.reloadRewards();
				reload = true;
			}
			if(rs.equalsIgnoreCase("blocks") || rs.equalsIgnoreCase("all")) {
				ConfigManager.reloadConfig("blocks");
				BlockManager.reload();
				reload = true;
			}
			if(rs.equalsIgnoreCase("worlds") || rs.equalsIgnoreCase("all")) {
				WorldManager.reload();
				reload = true;
			}
			if(rs.equalsIgnoreCase("players") || rs.equalsIgnoreCase("all")) {
				ConfigManager.reloadConfig("players");
				DvZ.playerManager.reload();
				reload = true;
			}
			if(rs.equalsIgnoreCase("language") || rs.equalsIgnoreCase("all")) {
				ConfigManager.reloadConfig("language");
				reload = true;
			}
			
			if(reload) {
				DvZ.sendPlayerMessageFormated(sender, "Successfully reloaded!");
				return true;
			} else {
				DvZ.sendPlayerMessageFormated(sender, "Unknown argument: "+rs);
				return false;
			}
		} else {
			DvZ.sendPlayerMessageFormated(sender, "Please specify what you want to reload!");
			return false;
		}
	}
}
