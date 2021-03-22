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

package org.dockbox.selene.sponge.objects.bossbar;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import org.dockbox.selene.api.objects.Exceptional;
import org.dockbox.selene.api.objects.bossbar.Bossbar;
import org.dockbox.selene.api.objects.bossbar.BossbarColor;
import org.dockbox.selene.api.objects.bossbar.BossbarStyle;
import org.dockbox.selene.api.objects.player.Player;
import org.dockbox.selene.api.server.Selene;
import org.dockbox.selene.api.tasks.TaskRunner;
import org.dockbox.selene.api.text.Text;
import org.dockbox.selene.api.util.SeleneUtils;
import org.dockbox.selene.common.objects.bossbar.DefaultTickableBossbar;
import org.dockbox.selene.sponge.util.SpongeConversionUtil;
import org.jetbrains.annotations.NonNls;
import org.spongepowered.api.boss.ServerBossBar;

import java.time.Duration;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SpongeBossbar extends DefaultTickableBossbar<ServerBossBar> {

    @AssistedInject
    public SpongeBossbar(
            @Assisted String id,
            @Assisted float percent,
            @Assisted Text text,
            @Assisted BossbarColor color,
            @Assisted BossbarStyle style
    ) {
        super(id, percent, text, color, style);
    }

    @Override
    public void tick() {
        this.updateReference();
    }

    @Override
    public void showTo(Player player) {
        this.getReference().ifPresent(serverBossBar -> {
            SpongeConversionUtil.toSponge(player).ifPresent(serverBossBar::addPlayer);

            if (Bossbar.REGISTRY.containsKey(this.getId()))
                Selene.log().warn("Adding a bossbar with duplicate ID '" + this.getId() + "' to " + player.getName() + ". This may cause unexpected behavior!");

            else if (Bossbar.REGISTRY.containsValue(this))
                Selene.log().warn("Adding identical bossbar with different ID '" + this.getId() + "'. This may cause unexpected behavior!");

            Bossbar.REGISTRY.put(this.getId(), this);
        });
    }

    @Override
    public void showTo(Player player, Duration duration) {
        this.getReference().ifPresent(serverBossBar -> {

            SpongeConversionUtil.toSponge(player).ifPresent(sp -> {
                serverBossBar.addPlayer(sp);
                TaskRunner.create().acceptDelayed(() -> this.hideFrom(player), duration.getSeconds(), TimeUnit.SECONDS);
            });

            if (Bossbar.REGISTRY.containsKey(this.getId()))
                Selene.log().warn("Adding a bossbar with duplicate ID '" + this.getId() + "' to " + player.getName() + ". This may cause unexpected behavior!");

            else if (Bossbar.REGISTRY.containsValue(this))
                Selene.log().warn("Adding identical bossbar with different ID '" + this.getId() + "'. This may cause unexpected behavior!");

            Bossbar.REGISTRY.put(this.getId(), this);
        });
    }

    @Override
    public void hideFrom(Player player) {
        this.getReference().ifPresent(serverBossBar -> {
            SpongeConversionUtil.toSponge(player).ifPresent(serverBossBar::removePlayer);
            if (serverBossBar.getPlayers().isEmpty()) Bossbar.REGISTRY.remove(this.getId());
        });
    }

    @Override
    public Collection<Player> visibleTo() {
        return this.getReference().map(serverBossBar -> serverBossBar.getPlayers().stream()
                .map(SpongeConversionUtil::fromSponge)
                .collect(Collectors.toList()))
                .orElseGet(SeleneUtils::emptyList);
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
        }).orElse(false);
    }

    @Override
    public boolean isVisibleTo(@NonNls String name) {
        return this.getReference().map(serverBossBar -> {
            for (org.spongepowered.api.entity.living.player.Player serverBossBarPlayer :
                    serverBossBar.getPlayers()) {
                if (serverBossBarPlayer.getName().equals(name)) return true;
            }
            return false;
        }).orElse(false);
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
