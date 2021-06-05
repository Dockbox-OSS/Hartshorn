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

package org.dockbox.hartshorn.sponge.objects.bossbar;

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.i18n.text.Text;
import org.dockbox.hartshorn.api.task.TaskRunner;
import org.dockbox.hartshorn.di.annotations.Wired;
import org.dockbox.hartshorn.server.minecraft.bossbar.Bossbar;
import org.dockbox.hartshorn.server.minecraft.bossbar.BossbarColor;
import org.dockbox.hartshorn.server.minecraft.bossbar.BossbarStyle;
import org.dockbox.hartshorn.server.minecraft.bossbar.DefaultTickableBossbar;
import org.dockbox.hartshorn.server.minecraft.players.Player;
import org.dockbox.hartshorn.sponge.util.SpongeConversionUtil;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.jetbrains.annotations.NonNls;
import org.spongepowered.api.boss.ServerBossBar;

import java.time.Duration;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SpongeBossbar extends DefaultTickableBossbar<ServerBossBar> {

    public SpongeBossbar() {
        super("", -1, null, null, null);
        throw new IllegalArgumentException("This type should be instantiated through autowiring only!");
    }

    @Wired
    public SpongeBossbar(String id, float percent, Text text, BossbarColor color, BossbarStyle style) {
        super(id, percent, text, color, style);
    }

    @Override
    public void tick() {
        this.updateReference();
    }

    @Override
    public void showTo(Player player) {
        this.getReference().present(serverBossBar -> {
            SpongeConversionUtil.toSponge(player).present(serverBossBar::addPlayer);

            if (Bossbar.REGISTRY.containsKey(this.getId()))
                Hartshorn.log().warn("Adding a bossbar with duplicate ID '" + this.getId() + "' to " + player.getName() + ". This may cause unexpected behavior!");

            else if (Bossbar.REGISTRY.containsValue(this))
                Hartshorn.log().warn("Adding identical bossbar with different ID '" + this.getId() + "'. This may cause unexpected behavior!");

            Bossbar.REGISTRY.put(this.getId(), this);
        });
    }

    @Override
    public void showTo(Player player, Duration duration) {
        this.getReference().present(serverBossBar -> {

            SpongeConversionUtil.toSponge(player).present(sp -> {
                serverBossBar.addPlayer(sp);
                TaskRunner.create().acceptDelayed(() -> this.hideFrom(player), duration.getSeconds(), TimeUnit.SECONDS);
            });

            if (Bossbar.REGISTRY.containsKey(this.getId()))
                Hartshorn.log().warn("Adding a bossbar with duplicate ID '" + this.getId() + "' to " + player.getName() + ". This may cause unexpected behavior!");

            else if (Bossbar.REGISTRY.containsValue(this))
                Hartshorn.log().warn("Adding identical bossbar with different ID '" + this.getId() + "'. This may cause unexpected behavior!");

            Bossbar.REGISTRY.put(this.getId(), this);
        });
    }

    @Override
    public void hideFrom(Player player) {
        this.getReference().present(serverBossBar -> {
            SpongeConversionUtil.toSponge(player).present(serverBossBar::removePlayer);
            if (serverBossBar.getPlayers().isEmpty()) Bossbar.REGISTRY.remove(this.getId());
        });
    }

    @Override
    public Collection<Player> visibleTo() {
        return this.getReference().map(serverBossBar -> serverBossBar.getPlayers().stream()
                .map(SpongeConversionUtil::fromSponge)
                .collect(Collectors.toList()))
                .get(HartshornUtils::emptyList);
    }

    @Override
    public boolean isVisibleTo(Player player) {
        return this.isVisibleTo(player.getUniqueId());
    }

    @Override
    public boolean isVisibleTo(UUID player) {
        return this.getReference().map(serverBossBar -> {
            for (org.spongepowered.api.entity.living.player.Player serverBossBarPlayer :
                    serverBossBar.getPlayers()) {
                if (serverBossBarPlayer.getUniqueId().equals(player)) return true;
            }
            return false;
        }).or(false);
    }

    @Override
    public boolean isVisibleTo(@NonNls String name) {
        return this.getReference().map(serverBossBar -> {
            for (org.spongepowered.api.entity.living.player.Player serverBossBarPlayer :
                    serverBossBar.getPlayers()) {
                if (serverBossBarPlayer.getName().equals(name)) return true;
            }
            return false;
        }).or(false);
    }

    @Override
    public Function<ServerBossBar, Exceptional<ServerBossBar>> getUpdateReferenceTask() {
        return bar -> {
            bar.setName(SpongeConversionUtil.toSponge(this.getText()));
            bar.setPercent(this.getPercent() / 100);
            bar.setColor(SpongeConversionUtil.toSponge(this.getColor()));
            bar.setOverlay(SpongeConversionUtil.toSponge(this.getStyle()));
            return Exceptional.of(bar);
        };
    }

    @Override
    public Exceptional<ServerBossBar> constructInitialReference() {
        return Exceptional.of(
                ServerBossBar.builder()
                        .name(SpongeConversionUtil.toSponge(this.getText()))
                        .percent(this.getPercent() / 100)
                        .color(SpongeConversionUtil.toSponge(this.getColor()))
                        .overlay(SpongeConversionUtil.toSponge(this.getStyle()))
                        .build());
    }
}