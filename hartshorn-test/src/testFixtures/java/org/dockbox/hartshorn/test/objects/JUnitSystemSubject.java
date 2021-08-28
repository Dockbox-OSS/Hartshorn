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

package org.dockbox.hartshorn.test.objects;

import org.dockbox.hartshorn.api.exceptions.NotImplementedException;
import org.dockbox.hartshorn.commands.SystemSubject;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.i18n.text.Text;
import org.dockbox.hartshorn.i18n.text.pagination.Pagination;

import javax.inject.Inject;

import lombok.Getter;

public class JUnitSystemSubject extends SystemSubject {

    @Inject
    @Getter
    private ApplicationContext applicationContext;

    @Override
    public void execute(final String command) {
        // TODO: CommandBus implementation
        throw new NotImplementedException();
    }

    @Override
    public void send(final Text text) {
        // TODO: Test implementation, mocking client?
        throw new NotImplementedException();
    }

    @Override
    public void sendWithPrefix(final Text text) {
        // TODO: Test implementation, mocking client?
        throw new NotImplementedException();
    }

    @Override
    public void send(final Pagination pagination) {
        // TODO: Test implementation, mocking client?
        throw new NotImplementedException();
    }
}
