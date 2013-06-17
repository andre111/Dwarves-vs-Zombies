package me.andre111.dvz.commands;

import java.util.HashMap;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.Game;
import me.andre111.dvz.config.ConfigManager;

import org.bukkit.command.CommandSender;

public class DvZCommand {
	
	public static void initCommands() {
		new TestCommand("dvztest");
		new StartCommand("dvz_start");
		new DwarfSpawnCommand("dvz_dwarf");
		new MonsterSpawnCommand("dvz_monster");
		new InfoCommand("dvz_info");
		new ResetCommand("dvz_reset");
		new MonumentCommand("dvz_monument");
		new DragonCommand("dvz_dragon");
		new AddPlayerCommand("dvz_add");
		new AssasinCommand("dvz_assasin");
		new JoinCommand("dvz_join", false);
		new JoinCommand("dvz_joini", true);
		new LeaveCommand("dvz_leave");
		new WorldSaveCommand("dvz_saveworld");
		new WorldCreateCommand("dvz_createworld");
		new ReleaseCommand("dvz_release");
		new GiveCommand("dvz_give");
		new ItemstandCommand("dvz_itemstand");
		new QuarryCommand("dvz_quarry");
	}
	
	
	
	private static HashMap<String, DvZCommand> commands = new HashMap<String, DvZCommand>();
	
	public DvZCommand(String name) {
		if(commands.containsKey(name)) throw new IllegalArgumentException("Commandname "+name+" allready taken by "+commands.get(name)+" when adding "+this);
		
		commands.put(name, this);
	}
	
	/*
	 * Hanlde that command
	 * 
	 * returns successfull
	 */
	public boolean handle(int gameID, CommandSender sender, String[] args) {
		return false;
	}
	
	//get command from its name
	public static DvZCommand getCommand(String name) {
		if(commands.containsKey(name)) {
			return commands.get(name);
		}
		
		return null;
	}
	
	//game helping methods
	protected Game getGameFromID(int gameID, CommandSender sender) {
		Game game;
		if(gameID==-1) {
			game = DvZ.instance.getGame(0);
			
			if(ConfigManager.getStaticConfig().getString("show_game_id","true")=="true")
				sender.sendMessage(ConfigManager.getLanguage().getString("string_using_game","Using Game ID -0-").replace("-0-", ""+0));
		} else {
			game = DvZ.instance.getGame(gameID);
			if(ConfigManager.getStaticConfig().getString("show_game_id","true")=="true")
				if(game!=null) sender.sendMessage(ConfigManager.getLanguage().getString("string_using_game","Using Game ID -0-").replace("-0-", ""+gameID));
				else sender.sendMessage(ConfigManager.getLanguage().getString("string_not_game","Game ID -0- does not exist/is not activated!").replace("-0-", ""+0));
		}
		
		return game;
	}
	
	protected Game getGameFromIDSearchFree(int gameID, CommandSender sender) {
		Game game;
		if(gameID==-1) {
			int id = 0;
			game = DvZ.instance.getGame(0);
			
			//searching for a game in the "Lobby state"
			if(ConfigManager.getStaticConfig().getString("join_free_game", "true").equals("true")) {
				if(game.getState()>1) {
					for(int i=0; i<10; i++) {
						Game g = DvZ.instance.getGame(i);
						if(g!=null) {
							if(g.getState()<2) {
								id = i;
								game = g;
								break;
							}
						}
					}
				}
			}
			
			if(ConfigManager.getStaticConfig().getString("show_game_id","true")=="true")
				sender.sendMessage(ConfigManager.getLanguage().getString("string_using_game","Using Game ID -0-").replace("-0-", ""+id));
		} else {
			game = DvZ.instance.getGame(gameID);
			if(ConfigManager.getStaticConfig().getString("show_game_id","true")=="true")
				if(game!=null) sender.sendMessage(ConfigManager.getLanguage().getString("string_using_game","Using Game ID -0-").replace("-0-", ""+gameID));
				else sender.sendMessage(ConfigManager.getLanguage().getString("string_not_game","Game ID -0- does not exist/is not activated!").replace("-0-", ""+0));
		}
		
		return game;
	}
}
