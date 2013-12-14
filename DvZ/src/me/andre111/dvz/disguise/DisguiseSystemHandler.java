package me.andre111.dvz.disguise;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.config.ConfigManager;
import me.andre111.dvz.event.DvZInvalidInteractEvent;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.comphenix.protocol.events.ConnectionSide;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.FieldAccessException;

public class DisguiseSystemHandler implements Listener {
	private static SupportedDisguises dsystem = SupportedDisguises.NOONE;
	private static DSystem disguisesystem = null;
	
	public static boolean init() {
		//Disguise-Plugin check
		if (!ConfigManager.getStaticConfig().getString("disable_dcraft_check", "false").equals("true")) {
			if (Bukkit.getPluginManager().isPluginEnabled("DisguiseCraft"))
			{
				dsystem = SupportedDisguises.DISGUISECRAFT;
			} else if (Bukkit.getPluginManager().isPluginEnabled("BeTheMob")) {
				dsystem = SupportedDisguises.BETHEMOB;
			} else if (Bukkit.getPluginManager().isPluginEnabled("LibsDisguises")) {
				dsystem = SupportedDisguises.LIBSDISGUISES;
			}
			
			if(dsystem==SupportedDisguises.NOONE) {
				DvZ.sendPlayerMessageFormated(Bukkit.getServer().getConsoleSender(), DvZ.prefix+ChatColor.RED+"No supported disguising Plugin could be found, disabling...");
				Bukkit.getPluginManager().disablePlugin(DvZ.instance);
				return false;
			}
		}
		
		//init disguisesystem
		DvZ.sendPlayerMessageFormated(Bukkit.getServer().getConsoleSender(), DvZ.prefix+"Using "+dsystem.getName()+" to handle Disguising");
		
		switch(dsystem) {
		case DISGUISECRAFT:
			disguisesystem = new DSystem_DisguiseCraft();
			break;
		case BETHEMOB:
			disguisesystem = new DSystem_BeTheMob();
			break;
		case LIBSDISGUISES:
			disguisesystem = new DSystem_LibsDisguises();
			break;
		}
		disguisesystem.initListeners(DvZ.instance);
		
		//setup playerinteract with ivalid entity stuffs
		setupInteractListener();
		
		return true;
	}
	
	public static void disguiseP(Player player, String disguise) {
		disguisesystem.disguiseP(player, disguise);
	}
	
	public static void undisguiseP(Player player) {
		disguisesystem.undisguiseP(player);
	}
	
	public static void redisguiseP(Player player) {
		disguisesystem.redisguiseP(player);
	}
	
	public static int newEntityID() {
		return disguisesystem.newEntityID();
	}
	
	//Interact with invalid stuff listener
	public static void setupInteractListener() {
		DvZ.protocolManager.addPacketListener(new PacketAdapter(DvZ.instance,
			ConnectionSide.CLIENT_SIDE, ListenerPriority.NORMAL, 0x02) {
			    @Override
			    public void onPacketReceiving(PacketEvent event) {
			    	Player player = event.getPlayer();
			        if (event.getPacketID() == 0x02) {
			            try {
			            	PacketContainer packet = event.getPacket();
			                int target = packet.getSpecificModifier(int.class).read(1);
			                int action = packet.getSpecificModifier(byte.class).read(1);
			                
			                if (packet.getEntityModifier(player.getWorld()).read(1) == null) {
			                	DvZInvalidInteractEvent newEvent = new DvZInvalidInteractEvent(player, target, action);
			                    plugin.getServer().getPluginManager().callEvent(newEvent);
			                }
			            } catch (FieldAccessException e) {
			                DvZ.log("Couldn't access a field in an 0x07-UseEntity packet!");
			            }
			        }
			    }
		});
		
		DvZ.instance.getServer().getPluginManager().registerEvents(new DisguiseSystemHandler(), DvZ.instance);
	}
	
	//rightclicking invalidstuffs
	@EventHandler
	public void onPlayerInvalidInteractEntity(final DvZInvalidInteractEvent event) {
		Bukkit.getScheduler().runTask(DvZ.instance, new Runnable() {
			public void run() {
				Player player = event.getPlayer();
				if(player!=null) {
					//clicking on "3D-Items"
					DvZ.item3DHandler.clickOnInvalidEntity(player, event.getTarget());
				}
			}
		});
	}
}
