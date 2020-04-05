package com.darwinreforged.server.modules.modbanner.util;

import com.darwinreforged.server.api.DarwinServer;
import com.darwinreforged.server.modules.modbanner.ModBannerModule;
import com.darwinreforged.server.modules.modbanner.ModData;
import com.darwinreforged.server.modules.modbanner.VanillaPlayerException;

import org.spongepowered.api.entity.living.player.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import static com.darwinreforged.server.mcp.entities.Entities.Player.getModList;

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

		try {
			for (Entry<String, String> mod : getModList(player.getUniqueId()).entrySet()) {
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
