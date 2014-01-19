package me.andre111.dvz.disguise;

import java.util.ArrayList;

import org.bukkit.entity.EntityType;

public enum DvZDisguiseType {
	//HOSTILE
	ZOMBIE(EntityType.ZOMBIE, "zombie"),
	VILLAGER_ZOMBIE(EntityType.ZOMBIE, "zombievillager", "villagerzombie", "infectedzombie", "infectedvillager"),
	SKELETON(EntityType.SKELETON, "skeleton"),
	WITHER_SKELETON(EntityType.SKELETON, "witherskeleton"),
	CREEPER(EntityType.CREEPER, "creeper"),
	SLIME(EntityType.SLIME, "slime"),
	WITCH(EntityType.WITCH, "witch"),
	BLAZE(EntityType.BLAZE, "blaze"),
	GHAST(EntityType.GHAST, "ghast"),
	MAGMA_CUBE(EntityType.MAGMA_CUBE, "magmacube", "magmaslime"),
	SILVERFISH(EntityType.SILVERFISH, "silverfish"),
	//NEUTRAL
	SPIDER(EntityType.SPIDER, "spider"),
	CAVE_SPIDER(EntityType.CAVE_SPIDER, "cavespider"),
	ENDERMAN(EntityType.ENDERMAN, "enderman"),
	WOLF(EntityType.WOLF, "wolf", "dog"),
	ZOMBIE_PIGMAN(EntityType.PIG_ZOMBIE, "zombiepigman", "pigzombie"),
	//TAMABLE
	OCELOT(EntityType.OCELOT, "ocelot", "cat"),
	HORSE(EntityType.HORSE, "horse"),
	//PASSIVE
	CHICKEN(EntityType.CHICKEN, "chicken"),
	COW(EntityType.COW, "cow"),
	PIG(EntityType.PIG, "pig"),
	SHEEP(EntityType.SHEEP, "sheep"),
	SQUID(EntityType.SQUID, "squid"),
	BAT(EntityType.BAT, "bat"),
	VILLAGER(EntityType.VILLAGER, "villager"),
	MOOSHROOM(EntityType.MUSHROOM_COW, "mooshroom", "mushroomcow"),
	//UTILITY
	SNOW_GOLEM(EntityType.SNOWMAN, "snowgolem", "snowman"),
	IRON_GOLEM(EntityType.IRON_GOLEM, "irongolem"),
	//BOSSES
	ENDER_DRAGON(EntityType.ENDER_DRAGON, "enderdragon", "dragon"),
	WITHER(EntityType.WITHER, "wither"),
	//UNUSED
	GIANT(EntityType.GIANT, "giant");
	
	private ArrayList<String> anames = new ArrayList<String>();
	private EntityType etype;
	
	private DvZDisguiseType(EntityType entitytype, String ...names) {
		etype = entitytype;
		
		for(int i=0; i<names.length; i++) {
			anames.add(names[i]);
		}
	}
	
	public ArrayList<String> getNames() {
		return anames;
	}
	public EntityType getEntityType() {
		return etype;
	}
	
	public static DvZDisguiseType getDisguise(String name) {
		name = name.replace("_", "");
		name = name.replace(" ", "");
		name = name.toLowerCase();
		
		for(DvZDisguiseType dtype : DvZDisguiseType.values()) {
			for(String st : dtype.getNames()) {
				if(st.equals(name)) {
					return dtype;
				}
			}
		}
		
		return null;
	}
}
