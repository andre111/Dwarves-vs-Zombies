package me.andre111.dvz.utils;

import me.andre111.dvz.Game;
import me.andre111.dvz.utils.IconMenu.OptionClickEvent;
import me.andre111.dvz.utils.IconMenu.OptionClickEventHandler;

public class GameOptionClickEventHandler implements OptionClickEventHandler {
	public Game game;
	
	public GameOptionClickEventHandler(Game game) {
		this.game = game;
	}
	
	@Override
	public void onOptionClick(OptionClickEvent event) {}
}
