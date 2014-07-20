package me.andre111.dvz.commands;

import java.io.File;

import me.andre111.dvz.DvZ;
import me.andre111.items.ItemHandler;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ItemstandCommand extends DvZCommand {
	//Create an itemstand
	public ItemstandCommand(String name) {
		super(name);
	}

	@Override
	public boolean handle(int gameID, CommandSender sender, String[] args) {
		if(!sender.hasPermission("dvz.itemstand")) {
			DvZ.sendPlayerMessageFormated(sender, "You don't have the Permission to do that!");
			return false;
		}
		if (!(sender instanceof Player)) {
			DvZ.sendPlayerMessageFormated(sender, "Y U NO PLAYER??!111");
			return true;
		}
		Player player = (Player)sender;

		//get the player
		if(args.length>0) {
			if(args.length>1) {
				if(args.length>2) {
					if(args.length>3) {
						boolean once = Boolean.parseBoolean(args[0]);
						boolean onlyClicking = Boolean.parseBoolean(args[1]);
						int itemID = Integer.parseInt(args[2]);
						
						//recombine all other arguments
						String itemSt = "";
						int ii = 3;
						while(args.length>ii) {
							itemSt = itemSt + " " + args[ii];
							ii++;
						}
						
						//get the item
						if(ItemHandler.decodeItem(itemSt, null)!=null) {
							DvZ.itemStandManager.createAndSaveStand(new File(Bukkit.getServer().getWorldContainer().getPath()+"/"+player.getWorld().getName()+"/dvz/"), player.getLocation(), itemID, once, itemSt, onlyClicking);
							return true;
						} else {
							DvZ.sendPlayerMessageFormated(sender, "Could not decode Itemstring: "+itemSt);
							return false;
						}
					} else {
						DvZ.sendPlayerMessageFormated(sender, "Please specify a formated Item to give!");
						return false;
					}
				} else {
					DvZ.sendPlayerMessageFormated(sender, "Please specify a display Item ID!");
					return false;
				}
			} else {
				DvZ.sendPlayerMessageFormated(sender, "Please specify whether only the clicking Player can pickup the item!");
				return false;
			}
		} else {
			DvZ.sendPlayerMessageFormated(sender, "Please specify a if it should be once per Player!");
			return false;
		}
	}
}
