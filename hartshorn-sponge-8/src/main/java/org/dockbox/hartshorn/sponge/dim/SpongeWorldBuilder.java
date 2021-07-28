/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.hartshorn.sponge.dim;

import net.kyori.adventure.text.Component;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.domain.tuple.Vector3N;
import org.dockbox.hartshorn.di.annotations.inject.Binds;
import org.dockbox.hartshorn.server.minecraft.dimension.world.World;
import org.dockbox.hartshorn.server.minecraft.dimension.world.WorldBuilder;
import org.dockbox.hartshorn.server.minecraft.dimension.world.generation.Biome;
import org.dockbox.hartshorn.server.minecraft.dimension.world.generation.Difficulty;
import org.dockbox.hartshorn.server.minecraft.dimension.world.generation.FlatWorldGenerator;
import org.dockbox.hartshorn.server.minecraft.dimension.world.generation.GeneratorType;
import org.dockbox.hartshorn.server.minecraft.dimension.world.generation.WorldGenerator;
import org.dockbox.hartshorn.server.minecraft.players.Gamemode;
import org.dockbox.hartshorn.sponge.Sponge8Application;
import org.dockbox.hartshorn.sponge.util.SpongeConvert;
import org.dockbox.hartshorn.sponge.util.SpongeUtil;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.generation.ChunkGenerator;
import org.spongepowered.api.world.generation.ConfigurableChunkGenerator;
import org.spongepowered.api.world.generation.config.FlatGeneratorConfig;
import org.spongepowered.api.world.generation.config.NoiseGeneratorConfig;
import org.spongepowered.api.world.generation.config.flat.LayerConfig;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.api.world.server.WorldTemplate;
import org.spongepowered.api.world.server.WorldTemplate.Builder;

import java.util.List;

@Binds(WorldBuilder.class)
public class SpongeWorldBuilder implements WorldBuilder {

    private final Builder template;

    public SpongeWorldBuilder() {
        this.template = WorldTemplate.builder();
    }

    @Override
    public WorldBuilder name(String name) {
        final ResourceKey key = ResourceKey.of(Sponge8Application.container(), name.toLowerCase());
        this.template.key(key);
        this.template.displayName(Component.text(name));
        return this;
    }

    @Override
    public WorldBuilder generator(WorldGenerator generator) {
        final Biome biome = generator.biome();
        final GeneratorType type = generator.type();

        if (generator instanceof FlatWorldGenerator flatGenerator) {
            final List<LayerConfig> layers = flatGenerator.layers().stream()
                    .map(SpongeConvert::toSponge)
                    .filter(Exceptional::present)
                    .map(Exceptional::get)
                    .map(block -> LayerConfig.of(1, block))
                    .toList();

            final FlatGeneratorConfig config = FlatGeneratorConfig.builder()
                    .biome(SpongeConvert.toSponge(biome))
                    .addLayers(layers)
                    .performDecoration(false)
                    .populateLakes(false)
                    .build();

            this.template.generator(ChunkGenerator.flat(config));
        }
        else {
            ConfigurableChunkGenerator<NoiseGeneratorConfig> config = switch (generator.type()) {
                case OVERWORLD -> ChunkGenerator.overworld();
                case FLAT -> throw new IllegalArgumentException("Unsupported flat generator: " + generator.getClass().getCanonicalName());
                case END -> ChunkGenerator.theEnd();
                case NETHER -> ChunkGenerator.theNether();
            };
            this.template.generator(config);
        }
        return this;
    }

    @Override
    public WorldBuilder type(GeneratorType type) {
        this.template.worldType(SpongeConvert.toSponge(type));
        return this;
    }

    @Override
    public WorldBuilder gamemode(Gamemode mode) {
        this.template.gameMode(SpongeConvert.toSponge(mode));
        return this;
    }

    @Override
    public WorldBuilder difficulty(Difficulty difficulty) {
        this.template.difficulty(SpongeConvert.toSponge(difficulty));
        return this;
    }

    @Override
    public WorldBuilder loadOnStartup(boolean load) {
        this.template.loadOnStartup(load);
        return this;
    }

    @Override
    public WorldBuilder pvp(boolean pvp) {
        this.template.pvp(pvp);
        return this;
    }

    @Override
    public WorldBuilder spawnPosition(Vector3N position) {
        this.template.spawnPosition(SpongeConvert.toSpongeDouble(position).toInt());
        return this;
    }

    @Override
    public Exceptional<World> build() {
        final WorldTemplate template = this.template.build();
        final boolean saveResult = SpongeUtil.await(Sponge.server().worldManager().saveTemplate(template)).or(false);
        if (saveResult) {
            final Exceptional<ServerWorld> world = SpongeUtil.await(Sponge.server().worldManager().loadWorld(template));
            return world.map(serverWorld -> new SpongeWorld(serverWorld.key()));
        } else {
            return Exceptional.of(new IllegalStateException("Could not save world template"));
        }
    }
}
