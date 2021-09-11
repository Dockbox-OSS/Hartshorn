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

package org.dockbox.hartshorn.boot;

import org.dockbox.hartshorn.commands.SystemSubject;
import org.dockbox.hartshorn.di.annotations.inject.Binds;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.i18n.text.Text;
import org.dockbox.hartshorn.i18n.text.pagination.Pagination;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import lombok.Getter;

@Binds(SystemSubject.class)
public class ApplicationSystemSubject extends SystemSubject {

    @Inject
    @Getter
    private ApplicationContext applicationContext;

    @Override
    public void send(final Text text) {
        this.applicationContext().log().info("-> %s".formatted(text.toPlain()));
    }

    @Override
    public void sendWithPrefix(final Text text) {
        this.send(text);
    }

    @Override
    public void send(final Pagination pagination) {
        final List<Text> message = HartshornUtils.asList(pagination.title());
        message.add(pagination.header());
        message.addAll(pagination.content());
        message.add(pagination.footer());
        final String out = message.stream().map(Text::toPlain).collect(Collectors.joining("\n-> "));
        this.applicationContext().log().info("-> %s".formatted(out));
    }
}
