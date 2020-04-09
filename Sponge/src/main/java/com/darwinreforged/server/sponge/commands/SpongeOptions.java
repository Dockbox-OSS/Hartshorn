package com.darwinreforged.server.sponge.commands;

import com.darwinreforged.server.core.commands.element.function.Options;

import org.spongepowered.api.CatalogType;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.world.World;

import java.util.Comparator;
import java.util.Optional;

/**
 * @author dags <dags@dags.me>
 */
class SpongeOptions {

    static final Options WORLDS = () -> Sponge.getServer().getWorlds().stream().map(World::getName);

    static final Options PLAYERS = () -> Sponge.getServer().getOnlinePlayers().stream().map(Player::getName);

    static final Options USERS = () -> {
        UserStorageService service = Sponge.getServiceManager().provideUnchecked(UserStorageService.class);
        return service.getAll()
                .stream()
                .map(service::get)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .sorted((u1, u2) -> {
                    if (u1.isOnline() != u2.isOnline()) {
                        return Boolean.compare(u1.isOnline(), u2.isOnline());
                    }
                    return Integer.compare(u1.getName().length(), u2.getName().length());
                })
                .map(User::getName);
    };

    @SuppressWarnings("unchecked")
    static Options catalogType(Class<?> type) {
        return () -> Sponge.getRegistry().getAllOf((Class<? extends CatalogType>) type)
                .stream()
                .map(CatalogType::getId)
                .sorted(Comparator.comparingInt(String::length));
    }
}
