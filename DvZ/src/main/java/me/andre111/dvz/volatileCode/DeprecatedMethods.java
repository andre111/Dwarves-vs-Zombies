package me.andre111.dvz.volatileCode;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffectType;

//TODO - get rid of any usage of these methods
@SuppressWarnings("deprecation")
public class DeprecatedMethods {
	public static Player getPlayerByName(String name) {
		return Bukkit.getPlayerExact(name);
	}
	
	public static int getMaterialID(Material mat) {
		return mat.getId();
	}
	
	public static byte getDatavalue(MaterialData data) {
		return data.getData();
	}
	
	public static int getBlockID(Block block) {
		return block.getTypeId();
	}
	
	public static void setBlockID(Block block, int id) {
		block.setTypeId(id);
	}
	
	public static byte getBlockData(Block block) {
		return block.getData();
	}
	
	public static void setBlockData(Block block, byte data) {
		block.setData(data);
	}
	
	public static void setBlockData(Block block, byte data, boolean applyPhysics) {
		block.setData(data, applyPhysics);
	}
	
	public static void setBlockIDandData(Block block, int id, byte data, boolean applyPhysics) {
		block.setTypeIdAndData(id, data, applyPhysics);
	}
	
	public static Material getMaterialByID(int id) {
		return Material.getMaterial(id);
	}
	
	public static PotionEffectType getPotionEffectByID(int id) {
		return PotionEffectType.getById(id);
	}
	
	public static FallingBlock spawnFallingBlock(Location loc, Material mat, byte data) {
		return loc.getWorld().spawnFallingBlock(loc, mat, data);
	}
	
	public static void sendBlockChange(Player player, Location loc, Material mat, byte data) {
		player.sendBlockChange(loc, mat, data);
	}
	
	public static ItemStack createItemStackByID(int id) {
		return new ItemStack(id);
	}
	
	public static int getEntityTypeID(EntityType type) {
		return type.getTypeId();
	}
}
