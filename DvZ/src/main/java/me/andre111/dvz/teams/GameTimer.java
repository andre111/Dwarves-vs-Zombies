package me.andre111.dvz.teams;

import java.util.ArrayList;

public class GameTimer {
	private GameTeamSetup teamSetup;
	
	private String name;
	private boolean isStarted = false;
	private int maxTime;
	private int time;
	private boolean showDisplay;
	private String display;
	private ArrayList<String> commands = new ArrayList<String>();

	public GameTimer(GameTeamSetup ts) {
		teamSetup = ts;
	}
	
	public void tick() {
		if(isStarted) {
			if(time>0) {
				time--;
				if(time==0) {
					teamSetup.performCommands(getCommands());
				}
			}
		}
	}
	
	public void finish() {
		time = 0;
		teamSetup.performCommands(getCommands());
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isStarted() {
		return isStarted;
	}
	public void start() {
		isStarted = true;
		time = maxTime;
	}
	public ArrayList<String> getCommands() {
		return commands;
	}
	public void addCommand(String command) {
		commands.add(command);
	}
	public int getMaxTime() {
		return maxTime;
	}
	public void setMaxTime(int maxTime) {
		this.maxTime = maxTime;
		this.time = maxTime;
	}
	public int getTime() {
		return time;
	}
	public void setTime(int time) {
		this.time = time;
	}
	public boolean isShowDisplay() {
		return showDisplay;
	}
	public void setShowDisplay(boolean showDisplay) {
		this.showDisplay = showDisplay;
	}
	public String getDisplay() {
		return display;
	}
	public void setDisplay(String display) {
		this.display = display;
	}
}
