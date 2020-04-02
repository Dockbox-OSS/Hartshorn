package com.darwinreforged.servermodifications.util.plugins;

import com.darwinreforged.servermodifications.DarwinServer;
import com.darwinreforged.servermodifications.exceptions.VanillaPlayerException;
import com.darwinreforged.servermodifications.modules.ModBannerModule;
import com.darwinreforged.servermodifications.objects.ModData;
import com.darwinreforged.servermodifications.util.todo.ModBannerDataFile;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraftforge.fml.common.network.handshake.NetworkDispatcher;

import org.spongepowered.api.entity.living.player.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class PlayerModsUtil {
	/**
	 This method return the mod list of a player, it will throw a VanillaPlayerException if the player don't send any mod
	 handshake info

	 @param player
	 player to get mods

	 @return mod list of null if vanilla

	 @throws VanillaPlayerException
	 if player don't send any mod handshake info, in newest versions of forge it just will return a empty
	 list...
	 */
	@SuppressWarnings("unchecked")
	public static List<ModData> getPlayerMods(Player player)
			throws VanillaPlayerException {
		List<ModData> data = new ArrayList<>();
		EntityPlayerMP pmp = ((EntityPlayerMP) player);
		NetHandlerPlayServer connection;
		NetworkManager nm;
		NetworkDispatcher np;
		try {
			connection = (NetHandlerPlayServer) pmp.getClass().getField("field_71135_a").get(pmp);
			nm = (NetworkManager) connection.getClass().getField("field_147371_a").get(connection);
			np = NetworkDispatcher.get(nm);

			// In old versions of Forge modList by default its null, then when you use the normal np.getModList() it try to translate a normal Map into a unmodifiableMap
			// It obviously throws a null pointer exception, and you can't catch it
			// Then we check the native Map to see it that player is playing in vanilla
			// You can see that in newest version of Forge they changed that to a empty collection
			// Check this: https://github.com/MinecraftForge/MinecraftForge/blob/1.12.x/src/main/java/net/minecraftforge/fml/common/network/handshake/NetworkDispatcher.java#L113
			// This cause a ton of problems to ModBanner... and a lot of time to found the problem
			Class<?> npClass = np.getClass();

			// Now to avoid problems we use the normal method
			java.lang.reflect.Field modListF = npClass.getField("modList");
			modListF.setAccessible(true);
			Map<String, String> modList = (java.util.Map<String, String>) modListF.get(np);

			for (Entry<String, String> mod : modList.entrySet()) {
				data.add(new ModData() {
					@Override
					public String getName() {
						return mod.getKey();
					}

					@Override
					public String getVersion() {
						return mod.getValue();
					}
				});
			}
			setData(player.getName(), data);
			return data;
		} catch (IllegalArgumentException e1) {
			DarwinServer.getLogger().error("IllegalArgumentException: "+e1.getMessage());
		} catch (IllegalAccessException e1) {
			DarwinServer.getLogger().error("IllegalAccessException: "+e1.getMessage());
		} catch (NoSuchFieldException e1) {
			DarwinServer.getLogger().error("NoSuchFieldException: "+e1.getMessage());
		} catch (SecurityException e1) {
			DarwinServer.getLogger().error("Security Exception: "+e1.getMessage());
		} catch (NullPointerException e1) {
			DarwinServer.getLogger().error("NullPointerException: "+e1.getMessage());
		}
		return new ArrayList<>();
	}
	
	public static void setData(String player, List<ModData> mods){
		List<String> r = new ArrayList<>();
		
		for(ModData mod : mods){
			r.add(mod.getName()+":"+mod.getVersion());
		}
		
		HashMap<String, String> dd = ModBannerDataFile.read(ModBannerModule.mod_dataF);
		dd.put(player, String.join(",", r));
		ModBannerDataFile.write(ModBannerModule.mod_dataF, dd);
	}
	
	public static List<ModData> getLastPlayerData(String player){
		HashMap<String, String> data = ModBannerDataFile.read(ModBannerModule.mod_dataF);
		if(!data.containsKey(player)){
			return new ArrayList<>();
		}
		List<String> playerData = Arrays.asList(data.get(player).split(","));
		List<ModData> rs = new ArrayList<>();
		for(String s : playerData){
			rs.add(new ModData() {
				@Override
				public String getVersion() {
					return s.split(":")[1];
				}
				
				@Override
				public String getName() {
					return s.split(":")[0];
				}
			});
		}
		return rs;
	}
	
	public static List<String> getPlayersWhoUseMod(String mod){
		List<String> players = new ArrayList<>();
		for(String player : ModBannerDataFile.read(ModBannerModule.mod_dataF).keySet()){
			for(ModData data : getLastPlayerData(player)){
				if(data.getName().toLowerCase().contains(mod)){
					players.add(player);
				}
			}
		}
		return players;
	}
//	
//	public static boolean useMod(List<ModData> mods, String mod){
//		for(ModData modData : mods){
//			if(modData.getName().toLowerCase().contains(mod)){
//				return true;
//			}
//		}
//	}

//	public static Optional<HashMap<String, String>> getLastData(String name){
//		if(Main.modsData.containsKey(name)){
//			return Optional.of(Main.modsData.get(name));
//		}else{
//			return Optional.empty();
//		}
//	}
//	
//	public static Optional<Map<String, String>> getLastModData(String mod){
//		List<String> playersWhoUse = new ArrayList<>();
//		for(Entry<String, HashMap<String, String>> player : Main.modsData.entrySet()){
//			for(Entry<String, V>player.getValue()){
//				
//			}
//		}
//	}
}
