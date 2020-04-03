package com.darwinreforged.server.modules.friends.util;

import com.darwinreforged.servermodifications.DarwinServer;
import com.darwinreforged.servermodifications.modules.FriendsModule;
import com.darwinreforged.servermodifications.objects.FriendsStorage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.spongepowered.api.Sponge;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.UUID;

public class FriendsStorageManager {

	private static File folder =
			Sponge.getGame().getSavesDirectory().resolve("data/friends").toFile();
	private static File storage;
	private static Gson gson = new Gson();

	public static void setup() {
		if (!folder.exists()) folder.mkdir();
	}

	public static FriendsStorage load(UUID uuid) {
		storage = new File(folder, uuid + ".yml");
		FileReader reader;
		FriendsStorage data = null;
		if (!storage.exists()) {
			try {
				storage.createNewFile();
				FileWriter writer = new FileWriter(storage, false);
				writer.write(gson.toJson(new FriendsStorage(uuid)));
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			reader = new FileReader(storage);
			Type temp = new TypeToken<FriendsStorage>() {}.getType();
			data = gson.fromJson(reader, temp);
			data.setPlayer(uuid);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;
	}


	public static void save(UUID uuid, FriendsStorage playerData) {
		DarwinServer.getModule(FriendsModule.class).ifPresent(module -> {
			module.users.put(uuid, playerData);
			storage = new File(folder, uuid + ".yml");
			FileWriter writer;
			try {
				writer = new FileWriter(storage, false);
				writer.write(gson.toJson(playerData));
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}
}
