package me.andre111.dvz.commands;

import me.andre111.dvz.DvZ;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TestCommand extends DvZCommand {
	//Only testing some stuff
	public TestCommand(String name) {
		super(name);
	}

	@Override
	public boolean handle(int gameID, CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Y U NO PLAYER??!111");
			return true;
		}
		Player player = (Player)sender;
		if(!sender.hasPermission("dvz.test")) {
			sender.sendMessage("You don't have the Permission to do that!");
			return false;
		}
		
		//plugin.resetMainWorld();
		
		//Spellcontroller.spellEnderChest(plugin.game, player);
		
		//player.getInventory().clear();
		//DvZ.instance.getPlayerGame(player.getName()).setPlayerState(player.getName(), 3);
		//DvZ.instance.getPlayerGame(player.getName()).addMonsterItems(player);
		
		player.setItemInHand(DvZ.enchantManager.getEnchantmentByName("testEnchant").enchantItem(player.getItemInHand(), 1));
		
		/*if(test==null) {
			test = DvZWorldProvider.generateNewWorld();
		}
		player.teleport(test.getSpawnLocation());*/
		
		//Spellcontroller.spellItemTrow(player, player);
		
		//(new Dragon(DragonTyp.FIRE)).spawn(player.getLocation().add(0, 20, 0));
		//(new DragonThrow()).castShot(player.getLocation().add(0, 5, 0), Bukkit.getServer().getWorlds().get(0).getSpawnLocation());
		
		//Fireworks.spawnTest(player.getLocation());
		
		/*if(args.length>0)
			DvZ.dragonAtManager.castFromPlayer(player, Integer.parseInt(args[0]));
		else
			player.sendMessage("Please specify Dragonattck ID");*/
		//QuarryGenerator.generateQuarry(player.getLocation().clone().subtract(0, 1, 0), 12, 60);
		
		/*final int itemID = (args.length>0) ? Integer.parseInt(args[0]) : Material.DIAMOND_SWORD.getId();
		//final String rand = ""+ (new Random()).nextInt(100);
		DvZ.item3DHandler.spawnAroundBlock(player, player.getLocation().clone().add(1, 0, 0), itemID, new Item3DRunnable() {
			@Override
			public void run(Player player) {
				player.getWorld().dropItemNaturally(player.getLocation().clone().add(0, 1, 0), new ItemStack(itemID));
			}
		});*/
		
		//DvZ.disguiseP(player, new Disguise(0, "", DisguiseType.Spider));
		return true;
	}
}
