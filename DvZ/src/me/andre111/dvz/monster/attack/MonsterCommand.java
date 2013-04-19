package me.andre111.dvz.monster.attack;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.andre111.dvz.Game;
import me.andre111.dvz.monster.MonsterAttack;

public class MonsterCommand extends MonsterAttack {
	private int console = 0;
	private ArrayList<String> commands = new ArrayList<String>();

	@Override
	public void setCastVar(int id, double var) {
		if(id==0) console = (int) Math.round(var);
	}
	
	@Override
	public void setCastVar(int id, String var) {
		if(id!=0) {
			commands.add(var);
		}
	}
	
	@Override
	public void spellCast(Game game, Player player) {
		CommandSender sender = player;
		if(console==1) sender = Bukkit.getServer().getConsoleSender();
		
		for(int i=0; i<commands.size(); i++) {
			Bukkit.getServer().dispatchCommand(sender, commands.get(i));
		}
	}
	
	@Override
	public int getType() {
		return 0;
	}
}
