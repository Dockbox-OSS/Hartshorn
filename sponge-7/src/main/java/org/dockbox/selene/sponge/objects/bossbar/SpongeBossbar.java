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

import org.dockbox.selene.core.impl.objects.bossbar.DefaultTickableBossbar;
import org.dockbox.selene.core.objects.Exceptional;
import org.dockbox.selene.core.objects.FieldReferenceHolder;
import org.dockbox.selene.core.objects.bossbar.Bossbar;
import org.dockbox.selene.core.objects.bossbar.BossbarColor;
import org.dockbox.selene.core.objects.bossbar.BossbarStyle;
import org.dockbox.selene.core.objects.player.Player;
import org.dockbox.selene.core.server.Selene;
import org.dockbox.selene.core.tasks.TaskRunner;
import org.dockbox.selene.core.text.Text;
import org.dockbox.selene.core.util.SeleneUtils;
import org.dockbox.selene.sponge.util.SpongeConversionUtil;
import org.jetbrains.annotations.NonNls;
import org.spongepowered.api.boss.ServerBossBar;

import java.time.Duration;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class SpongeBossbar extends DefaultTickableBossbar {

    private final FieldReferenceHolder<ServerBossBar> reference;

    @AssistedInject
    public SpongeBossbar(
            @Assisted String id,
            @Assisted float percent,
            @Assisted Text text,
            @Assisted BossbarColor color,
            @Assisted BossbarStyle style
    ) {
        super(id, percent, text, color, style);
        this.reference = new FieldReferenceHolder<>(Exceptional.of(this.constructReference()), bar -> {
            bar.setName(SpongeConversionUtil.toSponge(this.getText()));
            bar.setPercent(this.getPercent()/100);
            bar.setColor(SpongeConversionUtil.toSponge(this.getColor()));
            bar.setOverlay(SpongeConversionUtil.toSponge(this.getStyle()));
            return Exceptional.of(bar);
        }, ServerBossBar.class);
    }

    @Override
    public void tick() {
        this.reference.updateReference();
    }

    @Override
    public void showTo(Player player) {
        this.reference.getReference().ifPresent(serverBossBar -> {
            SpongeConversionUtil.toSponge(player).ifPresent(serverBossBar::addPlayer);
            if (Bossbar.REGISTRY.containsKey(this.getId())) Selene.log().warn("Adding a bossbar with duplicate ID '" + this.getId() + "' to " + player.getName() + ". This may cause unexpected behavior!");
            else if (Bossbar.REGISTRY.containsValue(this)) Selene.log().warn("Adding identical bossbar with different ID '" + this.getId()  + "'. This may cause unexpected behavior!");
            Bossbar.REGISTRY.put(this.getId(), this);
        });
    }

    @Override
    public void showTo(Player player, Duration duration) {
        this.reference.getReference().ifPresent(serverBossBar -> {
            SpongeConversionUtil.toSponge(player).ifPresent(sp -> {
                serverBossBar.addPlayer(sp);
                TaskRunner.create().acceptDelayed(
                        () -> this.hideFrom(player),
                        duration.getSeconds(),
                        TimeUnit.SECONDS
                );
            });
            if (Bossbar.REGISTRY.containsKey(this.getId())) Selene.log().warn("Adding a bossbar with duplicate ID '" + this.getId() + "' to " + player.getName() + ". This may cause unexpected behavior!");
            else if (Bossbar.REGISTRY.containsValue(this)) Selene.log().warn("Adding identical bossbar with different ID '" + this.getId()  + "'. This may cause unexpected behavior!");
            Bossbar.REGISTRY.put(this.getId(), this);
        });
    }

    @Override
    public void hideFrom(Player player) {
        this.reference.getReference().ifPresent(serverBossBar -> {
            SpongeConversionUtil.toSponge(player).ifPresent(serverBossBar::removePlayer);
            if (serverBossBar.getPlayers().isEmpty()) Bossbar.REGISTRY.remove(this.getId());
        });
    }

    @Override
    public Collection<Player> visibleTo() {
        return this.reference.getReference().map(serverBossBar -> serverBossBar.getPlayers().stream()
                .map(SpongeConversionUtil::fromSponge).collect(Collectors.toList()))
                .orElseGet(SeleneUtils.COLLECTION::emptyList);
    }

    @Override
    public boolean isVisibleTo(Player player) {
        return this.isVisibleTo(player.getUniqueId());
    }

    @Override
    public boolean isVisibleTo(UUID player) {
        return this.reference.getReference().map(serverBossBar -> {
            for (org.spongepowered.api.entity.living.player.Player serverBossBarPlayer : serverBossBar.getPlayers()) {
                if (serverBossBarPlayer.getUniqueId().equals(player)) return true;
            }
            return false;
        }).orElse(false);
    }

    @Override
    public boolean isVisibleTo(@NonNls String name) {
        return this.reference.getReference().map(serverBossBar -> {
            for (org.spongepowered.api.entity.living.player.Player serverBossBarPlayer : serverBossBar.getPlayers()) {
                if (serverBossBarPlayer.getName().equals(name)) return true;
            }
            return false;
        }).orElse(false);
    }

    private ServerBossBar constructReference() {
        return ServerBossBar.builder()
                .name(SpongeConversionUtil.toSponge(this.getText()))
                .percent(this.getPercent()/100)
                .color(SpongeConversionUtil.toSponge(this.getColor()))
                .overlay(SpongeConversionUtil.toSponge(this.getStyle()))
                .build();
    }
}
