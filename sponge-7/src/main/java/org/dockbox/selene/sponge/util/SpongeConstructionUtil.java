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

package org.dockbox.selene.sponge.util;

import com.sk89q.worldedit.blocks.BaseBlock;

import org.dockbox.selene.core.ConstructionUtil;
import org.dockbox.selene.core.objects.bossbar.Bossbar;
import org.dockbox.selene.core.objects.bossbar.BossbarColor;
import org.dockbox.selene.core.objects.bossbar.BossbarStyle;
import org.dockbox.selene.core.objects.item.Item;
import org.dockbox.selene.core.tasks.TaskRunner;
import org.dockbox.selene.core.text.Text;
import org.dockbox.selene.core.text.pagination.PaginationBuilder;
import org.dockbox.selene.sponge.objects.bossbar.SpongeBossbar;
import org.dockbox.selene.sponge.objects.item.SpongeItem;
import org.dockbox.selene.sponge.text.navigation.SpongePaginationBuilder;
import org.jetbrains.annotations.NotNull;

public class SpongeConstructionUtil implements ConstructionUtil {

    @Override
    public TaskRunner taskRunner() {
        return new SpongeTaskRunner();
    }

    @NotNull
    @Override
    public PaginationBuilder paginationBuilder() {
        return new SpongePaginationBuilder();
    }

    @NotNull
    @Override
    public Item<?> item(@NotNull String id, int amount) {
        return new SpongeItem(id, amount);
    }

    @Override
    public Item<?> item(BaseBlock baseBlock) {
        return Item.AIR;
//        return SpongeConversionUtil.throughSponge(baseBlock).orElse(Item.AIR);
    }

    @NotNull
    @Override
    public Item<?> item(@NotNull String id) {
        return new SpongeItem(id);
    }

    @Override
    public Bossbar bossbar(String id, float percent, Text text, BossbarColor color, BossbarStyle style) {
        return new SpongeBossbar(id, percent, text, color, style);
    }
}
