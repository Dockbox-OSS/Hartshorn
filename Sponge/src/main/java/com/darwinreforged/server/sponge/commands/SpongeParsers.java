package com.darwinreforged.server.sponge.commands;

import com.darwinreforged.server.core.util.commands.command.CommandException;
import com.darwinreforged.server.core.util.commands.command.Input;
import com.darwinreforged.server.core.util.commands.element.function.Filter;
import com.darwinreforged.server.core.util.commands.element.function.ValueParser;
import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;

import org.spongepowered.api.CatalogType;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.world.World;

import java.util.Optional;

/**
 * @author dags <dags@dags.me>
 */
class SpongeParsers {

    static final ValueParser<Player> PLAYER = s -> {
        for (Player player : Sponge.getServer().getOnlinePlayers()) {
            if (Filter.STARTS_WITH.test(player.getName(), s)) {
                return player;
            }
        }
        throw new CommandException("Could not find Player '%s'", s);
    };

    static final ValueParser<User> USER = s -> {
        UserStorageService service = Sponge.getServiceManager().provideUnchecked(UserStorageService.class);
        for (GameProfile profile : service.match(s)) {
            String name = profile.getName().orElse("");
            if (name.isEmpty()) {
                continue;
            }
            if (Filter.EQUALS_IGNORE_CASE.test(name, s)) {
                Optional<User> user = service.get(profile.getUniqueId());
                if (user.isPresent()) {
                    return user.get();
                }
            }
        }
        throw new CommandException("Could not find User '%s'", s);
    };

    static final ValueParser<World> WORLD = s -> {
        for (World world : Sponge.getServer().getWorlds()) {
            if (Filter.EQUALS_IGNORE_CASE.test(world.getName(), s)) {
                return world;
            }
        }
        throw new CommandException("Could not find World '%s'", s);
    };

    static final ValueParser<Vector3i> VEC3I = new ValueParser<Vector3i>() {
        @Override
        public Vector3i parse(Input input) throws CommandException {
            try {
                int x = Integer.parseInt(input.next());
                int y = Integer.parseInt(input.next());
                int z = Integer.parseInt(input.next());
                return new Vector3i(x, y, z);
            } catch (NumberFormatException e) {
                throw new CommandException(e.getMessage());
            }
        }

        @Override
        public Vector3i parse(String s) throws CommandException {
            return parse(new Input(s));
        }
    };

    static final ValueParser<Vector3d> VEC3D = new ValueParser<Vector3d>() {
        @Override
        public Vector3d parse(Input input) throws CommandException {
            try {
                double x = Double.parseDouble(input.next());
                double y = Double.parseDouble(input.next());
                double z = Double.parseDouble(input.next());
                return new Vector3d(x, y, z);
            } catch (NumberFormatException e) {
                throw new CommandException(e.getMessage());
            }
        }

        @Override
        public Vector3d parse(String s) throws CommandException {
            return parse(new Input(s));
        }
    };

    @SuppressWarnings("unchecked")
    static ValueParser<Object> catalogType(Class<?> type) {
        return new ValueParser<Object>() {
            @Override
            public Object parse(Input input) throws CommandException {
                String s = input.next();
                Optional<?> val = Sponge.getRegistry().getType((Class<? extends CatalogType>) type, s);
                if (val.isPresent()) {
                    return val.get();
                }
                throw new CommandException("'%s' is not a valid %s", s, type.getSimpleName());
            }

            @Override
            public Object parse(String s) throws CommandException {
                return parse(new Input(s));
            }
        };
    }
}
