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

import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.i18n.text.Text;

import java.util.UUID;

public final class BossbarBuilder {
    private String id = "Bossbar#" + System.currentTimeMillis();
    private float percent;
    private Text text = Text.of();
    private BossbarColor color = BossbarColor.WHITE;
    private BossbarStyle style = BossbarStyle.PROGRESS;

    protected BossbarBuilder() {}

    public BossbarBuilder withId(final UUID id) {
        this.id = id.toString();
        return this;
    }

    public BossbarBuilder but() {
        return Bossbar.builder()
                .withId(this.id)
                .withPercent(this.percent)
                .withText(this.text)
                .withColor(this.color)
                .withStyle(this.style);
    }

    public BossbarBuilder withStyle(final BossbarStyle style) {
        this.style = style;
        return this;
    }

    public BossbarBuilder withColor(final BossbarColor color) {
        this.color = color;
        return this;
    }

    public BossbarBuilder withText(final Text text) {
        this.text = text;
        return this;
    }

    public BossbarBuilder withPercent(final float percent) {
        this.percent = percent;
        return this;
    }

    public BossbarBuilder withId(final String id) {
        this.id = id;
        return this;
    }

    public Bossbar build(final ApplicationContext context) {
        return context.get(Bossbar.class, this.id, this.percent, this.text, this.color, this.style);
    }
}
