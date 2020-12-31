/*
 *  Copyright (C) 2020 Guus Lieben
 *
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.core.objects.bossbar;

import org.dockbox.selene.core.objects.player.Player;
import org.dockbox.selene.core.text.Text;

import java.time.Duration;
import java.util.Collection;

public interface Bossbar {

    void showTo(Player player);

    void showTo(Player player, Duration duration);

    void hideFrom(Player player);

    void showTo(Collection<Player> players);

    void hideFrom(Collection<Player> players);

    String getId();

    float getPercent();

    void setPercent(float percent);

    Text getText();

    void setText(Text text);

    BossbarColor getColor();

    void setColor(BossbarColor color);

    BossbarStyle getStyle();

    void setStyle(BossbarStyle style);

    static BossbarBuilder builder() {
        return new BossbarBuilder();
    }

}
