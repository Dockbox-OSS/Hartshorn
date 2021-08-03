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

package org.dockbox.hartshorn.server.minecraft.bossbar;

import org.dockbox.hartshorn.i18n.text.Text;
import org.dockbox.hartshorn.server.minecraft.players.Player;
import org.dockbox.hartshorn.util.ReferencedWrapper;

import java.util.Collection;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class DefaultTickableBossbar<T> extends ReferencedWrapper<T> implements Bossbar {

    private final String id;
    private float percent;
    private Text text;
    private BossbarColor color;
    private BossbarStyle style;

    public void showTo(Collection<Player> players) {
        players.forEach(this::showTo);
    }

    public void hideFrom(Collection<Player> players) {
        players.forEach(this::hideFrom);
    }

    public DefaultTickableBossbar<T> percent(float percent) {
        this.percent = percent;
        this.tick();
        return this;
    }

    public abstract void tick();

    public DefaultTickableBossbar<T> text(Text text) {
        this.text = text;
        this.tick();
        return this;
    }

    public DefaultTickableBossbar<T> color(BossbarColor color) {
        this.color = color;
        this.tick();
        return this;
    }

    public DefaultTickableBossbar<T> style(BossbarStyle style) {
        this.style = style;
        this.tick();
        return this;
    }
}
