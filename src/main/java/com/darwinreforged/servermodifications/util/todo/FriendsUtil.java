package com.darwinreforged.servermodifications.util.todo;

import com.darwinreforged.servermodifications.DarwinServer;
import com.darwinreforged.servermodifications.objects.FriendsStorage;
import com.darwinreforged.servermodifications.modules.FriendsModule;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class FriendsUtil {
	
	public static FriendsStorage getData(UUID uuid) {
		AtomicReference<FriendsStorage> data = new AtomicReference<>();
		//get it from the FriendsPlugin user storage if its there
		DarwinServer.getModule(FriendsModule.class).ifPresent(module -> {
			if (module.users.containsKey(uuid)) {
				data.set(module.users.get(uuid));
			} else {
				//load it from file if its not, if there is no file it will return an object with no values
				data.set(FriendsStorageManager.load(uuid));
			}
		});
		return data.get();
	}
	public static Optional<User> getUser(UUID owner) {
		Optional<UserStorageService> userStorage = Sponge.getServiceManager().provide(UserStorageService.class);
		return userStorage.get().get(owner);
	}
}
