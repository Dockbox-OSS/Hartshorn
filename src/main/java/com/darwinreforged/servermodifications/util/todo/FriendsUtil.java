package com.darwinreforged.servermodifications.util.todo;

import com.darwinreforged.servermodifications.objects.FriendsStorage;
import com.darwinreforged.servermodifications.plugins.FriendsPlugin;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;

import java.util.Optional;
import java.util.UUID;

public class FriendsUtil {
	
	public static FriendsStorage getData(UUID uuid) {
		FriendsStorage data;
		//get it from the FriendsPlugin user storage if its there
		if (FriendsPlugin.users.containsKey(uuid)) {
			data = FriendsPlugin.users.get(uuid);
		} else {
			//load it from file if its not, if there is no file it will return an object with no values
			data = FriendsStorageManager.load(uuid);
		}
		return data;
	}
	public static Optional<User> getUser(UUID owner) {
		Optional<UserStorageService> userStorage = Sponge.getServiceManager().provide(UserStorageService.class);
		return userStorage.get().get(owner);
	}
}
