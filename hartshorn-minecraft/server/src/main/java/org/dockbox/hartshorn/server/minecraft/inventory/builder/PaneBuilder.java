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

package org.dockbox.hartshorn.server.minecraft.inventory.builder;

import org.dockbox.hartshorn.di.properties.AttributeHolder;
import org.dockbox.hartshorn.i18n.text.Text;
import org.dockbox.hartshorn.server.minecraft.inventory.ClickContext;
import org.dockbox.hartshorn.server.minecraft.inventory.pane.Pane;
import org.dockbox.hartshorn.server.minecraft.players.Player;

import java.util.function.BiConsumer;
import java.util.function.Function;

public interface PaneBuilder<T extends Pane, B extends PaneBuilder<T, B>> extends AttributeHolder {

    B title(Text text);

    B onClose(BiConsumer<Player, T> onClose);
    B onClick(int index, Function<ClickContext, Boolean> onClick);
    B onClickOutput(Function<ClickContext, Boolean> onClick);

    B lock(boolean lock);

    T build();
}
