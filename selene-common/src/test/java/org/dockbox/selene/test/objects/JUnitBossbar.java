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

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import org.dockbox.selene.api.objects.Exceptional;
import org.dockbox.selene.api.objects.bossbar.BossbarColor;
import org.dockbox.selene.api.objects.bossbar.BossbarStyle;
import org.dockbox.selene.api.objects.player.Player;
import org.dockbox.selene.api.text.Text;
import org.dockbox.selene.common.objects.bossbar.DefaultTickableBossbar;

import java.time.Duration;
import java.util.Collection;
import java.util.UUID;

public class JUnitBossbar extends DefaultTickableBossbar<Void> {

    @AssistedInject
    public JUnitBossbar(
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
