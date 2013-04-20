package me.andre111.dvz.item.spell;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import me.andre111.dvz.Game;
import me.andre111.dvz.item.ItemSpell;

public class ItemCommand extends ItemSpell {
	private boolean console = false;
	private ArrayList<String> commands = new ArrayList<String>();
	
	@Override
	public void setCastVar(int id, double var) {
		if(id==0) console = var==1;
	}
	@Override
	public void setCastVar(int id, String var) {
		if(id>0) commands.add(var);
	}

	@Override
	public boolean cast(Game game, Player player) {
		for(String st : commands) {
			if(!st.contains("-1-")) {
				String command = st.replaceAll("-0-", player.getName());
				if(console)
					Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
				else
					Bukkit.getServer().dispatchCommand(player, command);
			}
		}
		return true;
	}
	@Override
	public boolean cast(Game game, Player player, Block block) {
		for(String st : commands) {
			if(!st.contains("-1-")) {
				String command = st.replaceAll("-0-", player.getName());
				if(console)
					Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
				else
					Bukkit.getServer().dispatchCommand(player, command);
			}
		}
		return true;
	}
	@Override
	public boolean cast(Game game, Player player, Player target) {
		for(String st : commands) {
			String command = st.replaceAll("-0-", player.getName());
			command = command.replaceAll("-1-", target.getName());
			if(console)
				Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
			else
				Bukkit.getServer().dispatchCommand(player, command);
		}
		return true;
	}
	@Override
	//casted by another spell on that location
	public boolean cast(Game game, Player player, Location loc) {
		for(String st : commands) {
			if(!st.contains("-1-")) {
				String command = st.replaceAll("-0-", player.getName());
				if(console)
					Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
				else
					Bukkit.getServer().dispatchCommand(player, command);
			}
		}
		return true;
	}
}
