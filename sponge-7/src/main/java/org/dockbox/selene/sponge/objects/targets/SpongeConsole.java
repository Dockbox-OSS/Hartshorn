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

package org.dockbox.selene.sponge.objects.targets;

import org.dockbox.selene.core.PlatformConversionService;
import org.dockbox.selene.core.i18n.entry.IntegratedResource;
import org.dockbox.selene.core.objects.Console;
import org.dockbox.selene.core.text.Text;
import org.dockbox.selene.core.text.pagination.Pagination;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.pagination.PaginationList;

public final class SpongeConsole extends Console {

    @Override
    public void execute(@NotNull String command) {
        Sponge.getCommandManager().process(
                Sponge.getServer().getConsole(), command);
    }

    @Override
    public void send(@NotNull Text text) {
        Sponge.getServer().getConsole().sendMessage(PlatformConversionService.<Text, org.spongepowered.api.text.Text>map(text));
    }

    @Override
    public void sendWithPrefix(@NotNull Text text) {
        Sponge.getServer().getConsole().sendMessage(org.spongepowered.api.text.Text.of(
                PlatformConversionService.<Text, org.spongepowered.api.text.Text>map(IntegratedResource.PREFIX.asText()),
                PlatformConversionService.<Text, org.spongepowered.api.text.Text>map(text)
        ));
    }

    @Override
    public void sendPagination(@NotNull Pagination pagination) {
        PlatformConversionService.<@NotNull Pagination, PaginationList>map(pagination).sendTo(Sponge.getServer().getConsole());
    }

    public static Console getInstance() {
        if (null != Console.instance) return Console.instance;
        return new SpongeConsole();
    }
}
