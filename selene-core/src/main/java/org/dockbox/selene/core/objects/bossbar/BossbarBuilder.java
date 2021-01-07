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

package org.dockbox.selene.core.objects.bossbar;

import org.dockbox.selene.core.text.Text;
import org.dockbox.selene.core.util.SeleneUtils;

import java.util.UUID;

public final class BossbarBuilder {
    private String id = "Bossbar#" + System.currentTimeMillis();
    private float percent;
    private Text text = Text.of();
    private BossbarColor color = BossbarColor.WHITE;
    private BossbarStyle style = BossbarStyle.PROGRESS;

    BossbarBuilder() {}

    public BossbarBuilder withId(String id) {
        this.id = id;
        return this;
    }

    public BossbarBuilder withId(UUID id) {
        this.id = id.toString();
        return this;
    }

    public BossbarBuilder withPercent(float percent) {
        this.percent = percent;
        return this;
    }

    public BossbarBuilder withText(Text text) {
        this.text = text;
        return this;
    }

    public BossbarBuilder withColor(BossbarColor color) {
        this.color = color;
        return this;
    }

    public BossbarBuilder withStyle(BossbarStyle style) {
        this.style = style;
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

    public Bossbar build() {
        return SeleneUtils.INJECT.getInstance(BossbarFactory.class)
                .create(this.id, this.percent, this.text, this.color, this.style);
    }
}
