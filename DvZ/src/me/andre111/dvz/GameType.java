package me.andre111.dvz;

public enum GameType {
	OLD("Old DvZ", 1),
	NEW("New DvZ", 2),
	ALTERNATE("Alternating DvZ", 3),
	
	COMPETITIVE_OLD("Competitive New DvZ", 11),
	COMPETITIVE_NEW("Competitive Old DvZ", 12),
	COMPETITIVE_ALTERNATE("Competitive Alternating DvZ", 13),
	
	ALTERNATE_ALL("Alternating between all", 30);
	
	
	public String name;
	public int id;
	
	public int getFirstType() {
		switch(id) {
		case 1:
		case 2:
		case 11:
		case 12:
			return id;
		case 3:
			return 1;
		case 13:
			return 11;
		case 30:
			return 1;
		}
		
		return 0;
	}
	
	//Get the next gameType from the old
	public int getNextType(int oldType) {
		switch(id) {
		//keep normal gametypes
		case 1:
		case 2:
		case 11:
		case 12:
			return id;
		//alternate normal dvz
		case 3:
			return 3 - oldType;
		//alternate competitive dvz;
		case 13:
			return 23 - oldType;
		//Alternate between all types and modes
		case 30:
			if(oldType==1) return 2;
			if(oldType==2) return 11;
			if(oldType==11) return 12;
			if(oldType==12) return 1;
		}
		
		return 0;
	}
	
	//get which dwarf/monsters should be used 1 or 2
	public static int getDwarfAndMonsterTypes(int gameType) {
		switch(gameType) {
		case 1:
		case 11:
			return 1;
		case 2:
		case 12:
			return 2;
		}
		
		return 0;
	}
	
	//Get the Gametype from its id
	public static GameType fromID(int i) {
		for(GameType gt : GameType.values()) {
			if(gt.id == i) {
				return gt;
			}
		}
		
		return null;
	}
	
	GameType(String n, int i) {
		name = n;
		id = i;
	}
}
