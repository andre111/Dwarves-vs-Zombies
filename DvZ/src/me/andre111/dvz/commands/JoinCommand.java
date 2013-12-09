package me.andre111.dvz.commands;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.Game;
import me.andre111.dvz.config.ConfigManager;
import me.andre111.dvz.utils.InventoryHandler;

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
			DvZ.sendPlayerMessageFormated(sender, "Y U NO PLAYER??!111");
			return true;
		}
		Player player = (Player)sender;
		if(!sender.hasPermission("dvz.join")) {
			DvZ.sendPlayerMessageFormated(sender, "You don't have the Permission to do that!");
			return false;
		}

		Game game = getGameFromIDSearchFree(gameID, sender);
		
		if(game!=null) {
			if(!game.isPlayer(player.getName())) {
				if(InventoryHandler.isInvEmpty(player, false) || ignoreItems) {
					DvZ.instance.joinGame(player, game);
				}
				else {
					DvZ.sendPlayerMessageFormated(player, ConfigManager.getLanguage().getString("string_join_items", "Warning! Items in your inventory will be deleted, please emtpy it first. To ignore this use /dvz_joini"));
				}
			}
		}

		return true;
	}
}
