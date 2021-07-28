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

package org.dockbox.hartshorn.test.objects;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.exceptions.NotImplementedException;
import org.dockbox.hartshorn.api.i18n.text.Text;
import org.dockbox.hartshorn.di.annotations.inject.Bound;
import org.dockbox.hartshorn.server.minecraft.bossbar.BossbarColor;
import org.dockbox.hartshorn.server.minecraft.bossbar.BossbarStyle;
import org.dockbox.hartshorn.server.minecraft.bossbar.DefaultTickableBossbar;
import org.dockbox.hartshorn.server.minecraft.players.Player;

import java.time.Duration;
import java.util.Collection;
import java.util.UUID;

public class JUnitBossbar extends DefaultTickableBossbar<Void> {

    @Bound
    public JUnitBossbar(String id, float percent, Text text, BossbarColor color, BossbarStyle style) {
        super(id, percent, text, color, style);
    }

    @Override
    public void tick() {
        throw new NotImplementedException();
    }

    @Override
    public Exceptional<Void> constructInitialReference() {
        throw new NotImplementedException();
    }

    @Override
    public void showTo(Player player) {
        throw new NotImplementedException();
    }

    @Override
    public void showTo(Player player, Duration duration) {
        throw new NotImplementedException();
    }

    @Override
    public void hideFrom(Player player) {
        throw new NotImplementedException();
    }

    @Override
    public Collection<Player> visible() {
        throw new NotImplementedException();
    }

    @Override
    public boolean visible(Player player) {
        throw new NotImplementedException();
    }

    @Override
    public boolean visible(UUID player) {
        throw new NotImplementedException();
    }

    @Override
    public boolean visible(String name) {
        throw new NotImplementedException();
    }
}
