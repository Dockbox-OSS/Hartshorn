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

package org.dockbox.selene.core.impl.objects.bossbar;

import org.dockbox.selene.core.objects.ReferencedWrapper;
import org.dockbox.selene.core.objects.bossbar.Bossbar;
import org.dockbox.selene.core.objects.bossbar.BossbarColor;
import org.dockbox.selene.core.objects.bossbar.BossbarStyle;
import org.dockbox.selene.core.objects.player.Player;
import org.dockbox.selene.core.text.Text;

import java.util.Collection;

public abstract class DefaultTickableBossbar<T> extends ReferencedWrapper<T> implements Bossbar {

    private final String id;
    private float percent;
    private Text text;
    private BossbarColor color;
    private BossbarStyle style;

    protected DefaultTickableBossbar(String id, float percent, Text text, BossbarColor color, BossbarStyle style) {
        this.id = id;
        this.percent = percent;
        this.text = text;
        this.color = color;
        this.style = style;
    }

    public abstract void tick();

    public void showTo(Collection<Player> players) {
        players.forEach(this::showTo);
    }

    public void hideFrom(Collection<Player> players) {
        players.forEach(this::hideFrom);
    }

    public String getId() {
        return this.id;
    }

    public float getPercent() {
        return this.percent;
    }

    public void setPercent(float percent) {
        this.percent = percent;
        this.tick();
    }

    public Text getText() {
        return this.text;
    }

    public void setText(Text text) {
        this.text = text;
        this.tick();

    }

    public BossbarColor getColor() {
        return this.color;
    }

    public void setColor(BossbarColor color) {
        this.color = color;
        this.tick();
    }

    public BossbarStyle getStyle() {
        return this.style;
    }

    public void setStyle(BossbarStyle style) {
        this.style = style;
        this.tick();
    }

}
