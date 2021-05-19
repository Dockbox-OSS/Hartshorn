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

package org.dockbox.selene.test.objects;

import org.dockbox.selene.api.domain.Exceptional;
import org.dockbox.selene.api.i18n.text.Text;
import org.dockbox.selene.di.annotations.Wired;
import org.dockbox.selene.server.minecraft.bossbar.BossbarColor;
import org.dockbox.selene.server.minecraft.bossbar.BossbarStyle;
import org.dockbox.selene.server.minecraft.bossbar.DefaultTickableBossbar;
import org.dockbox.selene.server.minecraft.players.Player;

import java.time.Duration;
import java.util.Collection;
import java.util.UUID;

public class JUnitBossbar extends DefaultTickableBossbar<Void> {

    @Wired
    public JUnitBossbar(String id, float percent, Text text, BossbarColor color, BossbarStyle style) {
        super(id, percent, text, color, style);
    }

    @Override
    public void tick() {

    }

    @Override
    public Exceptional<Void> constructInitialReference() {
        return null;
    }

    @Override
    public void showTo(Player player) {

    }

    @Override
    public void showTo(Player player, Duration duration) {

    }

    @Override
    public void hideFrom(Player player) {

    }

    @Override
    public Collection<Player> visibleTo() {
        return null;
    }

    @Override
    public boolean isVisibleTo(Player player) {
        return false;
    }

    @Override
    public boolean isVisibleTo(UUID player) {
        return false;
    }

    @Override
    public boolean isVisibleTo(String name) {
        return false;
    }
}
