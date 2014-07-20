package me.andre111.dvz.utils;

public class Item3DInfo {
	private String worldName;
	private int x;
	private int y;
	private int z;
	private byte rotation;
	private int itemID;
	
	public String getWorldName() {
		return worldName;
	}
	public void setWorldName(String worldName) {
		this.worldName = worldName;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public int getZ() {
		return z;
	}
	public void setZ(int z) {
		this.z = z;
	}
	public byte getRotation() {
		return rotation;
	}
	public void setRotation(byte rotation) {
		this.rotation = rotation;
	}
	public int getItemID() {
		return itemID;
	}
	public void setItemID(int itemID) {
		this.itemID = itemID;
	}
}
