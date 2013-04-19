package me.andre111.dvz.monster;

public class CustomMonsterItem {
	private int id;
	private String item;
	private String name;
	private boolean getAtSpawn;
	private int time;
	//private String[] cost;
	private MonsterAttack cast;
	
	//ALL THEM GETTERS AND SETTERS
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean getAtSpawn() {
		return getAtSpawn;
	}
	public void setGetAtSpawn(boolean getAtSpawn) {
		this.getAtSpawn = getAtSpawn;
	}
	public int getTime() {
		return time;
	}
	public void setTime(int time) {
		this.time = time;
	}
	/*public String[] getCost() {
		return cost;
	}
	public void setCost(String[] cost) {
		this.cost = cost;
	}*/
	public MonsterAttack getCast() {
		return cast;
	}
	public void setCast(MonsterAttack cast) {
		this.cast = cast;
	}
	
}
