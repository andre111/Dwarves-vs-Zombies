package me.andre111.dvz.commands;

import me.andre111.dvz.DvZ;
import me.andre111.items.ItemHandler;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GiveCommand extends DvZCommand {
	//Give special items
	public GiveCommand(String name) {
		super(name);
	}

	@Override
	public boolean handle(int gameID, CommandSender sender, String[] args) {
		if(!sender.hasPermission("dvz.give")) {
			DvZ.sendPlayerMessageFormated(sender, "You don't have the Permission to do that!");
			return false;
		}

		//get the player
		if(args.length>0) {
			Player player = Bukkit.getServer().getPlayerExact(args[0]);
			
			if(player!=null) {
				//recombine all other arguments
				String itemSt = "";
				int ii = 1;
				while(args.length>ii) {
					itemSt = itemSt + " " + args[ii];
					ii++;
				}
				
				//get the tem
				ItemStack it = ItemHandler.decodeItem(itemSt, player);
				if(it!=null) {
					player.getInventory().addItem(it);
					
					return true;
				} else {
					//DvZ.sendPlayerMessageFormated(sender, "Could not decode Itemstring: "+itemSt);
					return false;
				}
			} else {
				DvZ.sendPlayerMessageFormated(sender, "Player "+args[0]+" not found!");
				return false;
			}
		} else {
			DvZ.sendPlayerMessageFormated(sender, "Please specify a player to give the item to!");
			return false;
		}
	}
}
