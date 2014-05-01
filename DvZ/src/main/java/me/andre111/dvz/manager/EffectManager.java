package me.andre111.dvz.manager;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.Game;
import me.andre111.dvz.teams.Team;
import me.andre111.dvz.utils.PlayerHandler;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EffectManager {
	private int[][] effectDay;
	private int[][] effectNight;
	private int[][] effectMidNight;
	private int[][] effectAbove;
	private int[][] effectBelow;
	
	private boolean killEffectEnabled;
	private int killEffectTime;
	private boolean killEffectParticles;
	
	private Team team;
	
	public EffectManager(Team t) {
		team = t;
	}
	
	public void playerEffects(Game game) {
		addDaytimeEffects(game);
		addLightlevelEffects(game);
	}
	
	private void addDaytimeEffects(Game game) {
		int id = DvZ.instance.getGameID(game);
		World w =  Bukkit.getServer().getWorld(DvZ.instance.getConfig().getString("world_prefix", "DvZ_")+"Main"+id+"");
		if(w==null) return;
		
		long time = w.getTime();
		int[][] effects = effectDay;
		//Day
		if(time>=0 && time<=12000) {
			effects = effectDay;
		//Night
		} else {
			//midnight
			if(time>=18000-500 && time<=18000+500) {
				effects = effectMidNight;
			} else {
				effects = effectNight;
			}
		}
		
		for(int i=0; i<effects.length; i++) {
			PotionEffectType effect = PotionEffectType.getById(effects[i][0]);
			int level = effects[i][1];
			
			if(effect!=null) {
				addDaytimeEffect(game, effect, level);
			}
		}
	}
	
	private void addDaytimeEffect(Game game, PotionEffectType id, int level) {
		for(Map.Entry<UUID, Integer> e : game.playerstate.entrySet()){
			UUID playern = e.getKey();
			
			if(game.playerteam.get(playern).equals(team.getName())) {
				Player player = PlayerHandler.getPlayerFromUUID(playern);
				
				if(player!=null) {
					if(!PlayerHandler.hasHigherPotionEffect(player, id, level)) {
						player.addPotionEffect(new PotionEffect(id, 3*20, level), true);
					}
				}
			}
		}
	}
	
	private void addLightlevelEffects(Game game) {
		for(Map.Entry<UUID, Integer> e : game.playerstate.entrySet()){
			UUID playern = e.getKey();
			
			if(game.playerteam.get(playern).equals(team.getName())) {
				Player player = PlayerHandler.getPlayerFromUUID(playern);
				if(player!=null) {
					int light = player.getLocation().getBlock().getLightLevel();

					//above
					for(int i=light-1; i>=0; i--) {
						PotionEffectType effect = PotionEffectType.getById(effectAbove[i][0]);
						int level = effectAbove[i][1];
						if(effect!=null)
						if(!PlayerHandler.hasHigherPotionEffect(player, effect, level)) {
							player.addPotionEffect(new PotionEffect(effect, 3*20, level), true);
						}
					}
					//below
					for(int i=light+1; i<16; i++) {
						PotionEffectType effect = PotionEffectType.getById(effectBelow[i][0]);
						int level = effectBelow[i][1];
						if(effect!=null)
						if(!PlayerHandler.hasHigherPotionEffect(player, effect, level)) {
							player.addPotionEffect(new PotionEffect(effect, 3*20, level), true);
						}
					}
				}
			}
		}
	}
	
	public void killEffects(Game game) {
		if(!killEffectParticles) return;
		
		for(Map.Entry<UUID, Integer> e : game.playerstate.entrySet()){
			UUID playern = e.getKey();
			Player player = PlayerHandler.getPlayerFromUUID(playern);
			
			if(player!=null) {
				if(game.playerteam.get(playern).equals(team.getName())) {
					if(game.getCustomCooldown(playern, "effects_kill")>=0) {
						spawnParticle(game, player);
					}
				}
			}
		}
	}
	
	public void killedPlayer(Game game, Player dwarf) {
		if(killEffectEnabled) {
			//Strenght is broken with Disguisecraft
			//dwarf.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, killEffectTime*20, 20), true);
			
			game.setCustomCooldown(dwarf.getUniqueId(), "effects_kill", killEffectTime);
		}
	}
	
	public double getKillMultiplier(Game game, UUID dwarf) {
		if(game.getCustomCooldown(dwarf, "effects_kill")>0) {
			return 20;
		}
		
		return 1;
	}
	
	private void spawnParticle(Game game, Player player) {
		player.getWorld().playEffect(player.getLocation(), Effect.MOBSPAWNER_FLAMES, 0);
	}
	//not working
	/*
		PacketContainer packet = DvZ.protocolManager.createPacket(0x3F);
		
		packet.getStrings().
			write(0, "magicCrit");
		packet.getFloat().
			write(0, (float) player.getLocation().getX()).
			write(1, (float) player.getLocation().getY()).
			write(2, (float) player.getLocation().getZ()).
			write(3, 0F).
			write(4, 0F).
			write(5, 0F).
			write(6, 0F);
		packet.getIntegers().
			write(0, 1);
		
		sendToAll(game, packet);
	}
	
	private void sendToAll(Game game, PacketContainer packet) {
		System.out.println("Sparticles!");
		try {
			for(Map.Entry<String, Integer> e : game.playerstate.entrySet()){
				String playern = e.getKey();
				Player player = Bukkit.getServer().getPlayerExact(playern);
				
				if(player!=null) {
					DvZ.protocolManager.sendServerPacket(player, packet);
				}
			}
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}*/
	
	public void loadEffects(ConfigurationSection teamSec, String st) {
		//monster
		List<String> monsterDayEffects = teamSec.getStringList(st+".effects.day");
		List<String> monsterNightEffects = teamSec.getStringList(st+".effects.night");
		List<String> monsterMidNightEffects = teamSec.getStringList(st+".effects.midnight");

		//Day
		effectDay = new int[monsterDayEffects.size()][2];
		for(int i=0; i<monsterDayEffects.size(); i++) {
			String effect = monsterDayEffects.get(i);
			while(effect.startsWith(" ")) effect = effect.substring(1);
			while(effect.endsWith(" ")) effect = effect.substring(0, effect.length()-1);

			int eid = -1;
			int elevel = 0;

			String[] effectPart = effect.split(" ");
			if(effectPart.length>0) eid = Integer.parseInt(effectPart[0]);
			if(effectPart.length>1) elevel = Integer.parseInt(effectPart[1]);

			effectDay[i][0] = eid;
			effectDay[i][1] = elevel;
		}
		//Night
		effectNight = new int[monsterNightEffects.size()][2];
		for(int i=0; i<monsterNightEffects.size(); i++) {
			String effect = monsterNightEffects.get(i);
			while(effect.startsWith(" ")) effect = effect.substring(1);
			while(effect.endsWith(" ")) effect = effect.substring(0, effect.length()-1);

			int eid = -1;
			int elevel = 0;

			String[] effectPart = effect.split(" ");
			if(effectPart.length>0) eid = Integer.parseInt(effectPart[0]);
			if(effectPart.length>1) elevel = Integer.parseInt(effectPart[1]);

			effectNight[i][0] = eid;
			effectNight[i][1] = elevel;
		}
		//Night
		effectMidNight = new int[monsterMidNightEffects.size()][2];
		for(int i=0; i<monsterMidNightEffects.size(); i++) {
			String effect = monsterMidNightEffects.get(i);
			while(effect.startsWith(" ")) effect = effect.substring(1);
			while(effect.endsWith(" ")) effect = effect.substring(0, effect.length()-1);

			int eid = -1;
			int elevel = 0;

			String[] effectPart = effect.split(" ");
			if(effectPart.length>0) eid = Integer.parseInt(effectPart[0]);
			if(effectPart.length>1) elevel = Integer.parseInt(effectPart[1]);

			effectMidNight[i][0] = eid;
			effectMidNight[i][1] = elevel;
		}

		//dwarves
		effectAbove = new int[16][2];
		effectBelow = new int[16][2];
		for(int k=0; k<16; k++) {
			//above
			String dwarfEffects = teamSec.getString(st+".effects.lightlevel.above."+k, "");

			effectAbove[k][0] = -1;
			effectAbove[k][1] = 0;

			if(!dwarfEffects.equals("")) {
				while(dwarfEffects.startsWith(" ")) dwarfEffects = dwarfEffects.substring(1);
				while(dwarfEffects.endsWith(" ")) dwarfEffects = dwarfEffects.substring(0, dwarfEffects.length()-1);

				int eid = -1;
				int elevel = 0;

				String[] effectPart = dwarfEffects.split(" ");
				if(effectPart.length>0) eid = Integer.parseInt(effectPart[0]);
				if(effectPart.length>1) elevel = Integer.parseInt(effectPart[1]);

				effectAbove[k][0] = eid;
				effectAbove[k][1] = elevel;
			}

			//below
			String dwarfEffects2 = teamSec.getString(st+".effects.lightlevel.below."+k, "");

			effectBelow[k][0] = -1;
			effectBelow[k][1] = 0;

			if(!dwarfEffects2.equals("")) {
				while(dwarfEffects2.startsWith(" ")) dwarfEffects2 = dwarfEffects2.substring(1);
				while(dwarfEffects2.endsWith(" ")) dwarfEffects2 = dwarfEffects2.substring(0, dwarfEffects2.length()-1);

				int eid2 = -1;
				int elevel2 = 0;

				String[] effectPart2 = dwarfEffects2.split(" ");
				if(effectPart2.length>0) eid2 = Integer.parseInt(effectPart2[0]);
				if(effectPart2.length>1) elevel2 = Integer.parseInt(effectPart2[1]);

				effectBelow[k][0] = eid2;
				effectBelow[k][1] = elevel2;
			}
		}
		
		//kill effects
		killEffectEnabled = teamSec.getString(st+".effects.kill.enabled", "true").equals("true");
		killEffectTime = teamSec.getInt(st+".effects.kill.duration", 3);
		killEffectParticles = teamSec.getString(st+".effects.kill.particles", "true").equals("true");
	}
}
