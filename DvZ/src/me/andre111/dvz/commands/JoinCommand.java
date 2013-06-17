package me.andre111.dvz.commands;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.Game;
import me.andre111.dvz.config.ConfigManager;
import me.andre111.dvz.utils.ItemHandler;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JoinCommand extends DvZCommand {
	//Join the game
	private boolean ignoreItems;
	
	public JoinCommand(String name, boolean ignoreItems) {
		super(name);
		
		this.ignoreItems = ignoreItems;
	}

	@Override
	public boolean handle(int gameID, CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Y U NO PLAYER??!111");
			return true;
		}
		Player player = (Player)sender;
		if(!sender.hasPermission("dvz.join")) {
			sender.sendMessage("You don't have the Permission to do that!");
			return false;
		}

		Game game = getGameFromIDSearchFree(gameID, sender);
		
		if(game!=null) {
			if(!game.isPlayer(player.getName())) {
				if(ItemHandler.isInvEmpty(player) || ignoreItems) {
					DvZ.instance.joinGame(player, game);
				}
				else {
					player.sendMessage(ConfigManager.getLanguage().getString("string_join_items", "Warning! Items in your inventory will be deleted, please emtpy it first. To ignore this use /dvz_joini"));
				}
			}
		}

		return true;
	}
}
