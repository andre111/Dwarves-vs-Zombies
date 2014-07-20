package me.andre111.dvz.dragon;

import java.util.List;

import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;

public class DragonCustomAttack {
	private int type; 				//type: 0=cast directly at dragon,1=targeted Player,2=shot down
	private DragonAttack onHit; 	//the attack to execute on hit
	
	//speed
	private int speed; 				//ticks per block only type 1,2
	private int times;
	private int delay;
	private int maxDistance;
	//Effects
	private int effect;			//has effects?
	private int effectSpeed; 		//blocks per effect
	private int fwType;
	private Color fwColor;
	
	private int manaCost;
	private String name;
	private int itemID;
	
	private boolean canMove;
	private boolean stopAtCol;
	
	private boolean invulnarable;
	
	private List<Integer> dragons;
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public DragonAttack getOnHit() {
		return onHit;
	}
	public void setOnHit(DragonAttack onHit) {
		this.onHit = onHit;
	}
	public int getSpeed() {
		return speed;
	}
	public void setSpeed(int speed) {
		this.speed = speed;
	}
	public int isEffect() {
		return effect;
	}
	public void setEffect(int effect) {
		this.effect = effect;
	}
	public int getEffectSpeed() {
		return effectSpeed;
	}
	public void setEffectSpeed(int effectSpeed) {
		this.effectSpeed = effectSpeed;
	}
	public Type getFwType() {
		switch(fwType) {
		case 0:
		default:
			return Type.BURST;
		case 1:
			return Type.BALL;
		case 2:
			return Type.BALL_LARGE;
		case 3:
			return Type.STAR;
		case 4:
			return Type.CREEPER;
		}
	}
	public void setFwType(int fwType) {
		this.fwType = fwType;
	}
	public int getFxType() {
		return fwType;
	}
	public Color getFwColor() {
		return fwColor;
	}
	public void setFwColor(Color fwColor) {
		this.fwColor = fwColor;
	}
	public int getManaCost() {
		return manaCost;
	}
	public void setManaCost(int manaCost) {
		this.manaCost = manaCost;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getItemID() {
		return itemID;
	}
	public void setItemID(int itemID) {
		this.itemID = itemID;
	}
	public int getTimes() {
		return times;
	}
	public void setTimes(int times) {
		this.times = times;
	}
	public int getDelay() {
		return delay;
	}
	public void setDelay(int delay) {
		this.delay = delay;
	}
	public boolean isCanMove() {
		return canMove;
	}
	public void setCanMove(boolean canMove) {
		this.canMove = canMove;
	}
	public boolean isStopAtCol() {
		return stopAtCol;
	}
	public void setStopAtCol(boolean stopAtCol) {
		this.stopAtCol = stopAtCol;
	}
	public int getMaxDistance() {
		return maxDistance;
	}
	public void setMaxDistance(int maxDistance) {
		this.maxDistance = maxDistance;
	}
	public boolean isInvulnarable() {
		return invulnarable;
	}
	public void setInvulnarable(boolean invulnarable) {
		this.invulnarable = invulnarable;
	}
	public List<Integer> getDragons() {
		return dragons;
	}
	public void setDragons(List<Integer> dragons) {
		this.dragons = dragons;
	}
	
	
}
