package com.darwinreforged.server.sponge.commands;

import com.darwinreforged.server.core.commands.element.ElementFactory;
import com.darwinreforged.server.core.commands.element.function.Filter;
import com.darwinreforged.server.core.commands.element.function.Options;
import com.darwinreforged.server.core.commands.element.function.ValueParser;
import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;

import org.spongepowered.api.CatalogType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.world.World;

/**
 * @author dags <dags@dags.me>
 */
class SpongeElementFactory extends ElementFactory {

    private SpongeElementFactory(Builder builder) {
        super(builder);
    }

    @Override
    public ValueParser<?> getParser(Class<?> type) {
        if (!hasParser(type)) {
            if (CatalogType.class.isAssignableFrom(type)) {
                return SpongeParsers.catalogType(type);
            }
        }
        return super.getParser(type);
    }

    @Override
    public Options getOptions(Class<?> type) {
        if (!hasOptions(type)) {
            if (CatalogType.class.isAssignableFrom(type)) {
                return SpongeOptions.catalogType(type);
            }
        }
        return super.getOptions(type);
    }

    @Override
    public Filter getFilter(Class<?> type) {
        if (!hasFilter(type)) {
            if (CatalogType.class.isAssignableFrom(type)) {
                return Filter.CONTAINS;
            }
        }
        return super.getFilter(type);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static ElementFactory create() {
        return builder().build();
    }

    public static class Builder extends ElementFactory.Builder {

        private Builder() {
                this.parser(Player.class, SpongeParsers.PLAYER)
                    .parser(User.class, SpongeParsers.USER)
                    .parser(World.class, SpongeParsers.WORLD)
                    .parser(Vector3i.class, SpongeParsers.VEC3I)
                    .parser(Vector3d.class, SpongeParsers.VEC3D)
                    .options(Player.class, SpongeOptions.PLAYERS)
                    .options(User.class, SpongeOptions.USERS)
                    .options(World.class, SpongeOptions.WORLDS);
        }

        @Override
        public SpongeElementFactory build() {
            return new SpongeElementFactory(this);
        }
    }
}
