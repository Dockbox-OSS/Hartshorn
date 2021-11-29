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

package org.dockbox.hartshorn.data.hibernate;

import org.dockbox.hartshorn.core.annotations.component.Component;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.data.remote.PersistenceConnection;
import org.hibernate.dialect.Dialect;

import javax.inject.Inject;

import lombok.Getter;

@Component(singleton = true)
@Getter
public class HibernateRemoteImpl implements HibernateRemote {

    private final String driver;
    private final Class<? extends Dialect> dialect;

    private final String username;
    private final String password;
    private final String url;

    @Inject
    public HibernateRemoteImpl(final ApplicationContext context) {
        this.driver = (String) context.property("hartshorn.data.hibernate.driver_class").orNull();
        if (this.driver == null) throw new IllegalStateException("Driver class was not configured, expected hartshorn.data.hibernate.driver_class to be set, but got null");

        String dialect = (String) context.property("hartshorn.data.hibernate.dialect").orNull();
        if (dialect == null) throw new IllegalStateException("Dialect was not configured, expected hartshorn.data.hibernate.dialect to be set, but got null");

        TypeContext<?> dialectContext = TypeContext.lookup(dialect);
        if (!dialectContext.childOf(Dialect.class)) throw new IllegalStateException("Expected dialect to be a subtype of " + Dialect.class.getCanonicalName());

        this.dialect = (Class<? extends Dialect>) dialectContext.type();

        this.username = (String) context.property("hartshorn.data.username").orNull();
        this.password = (String) context.property("hartshorn.data.password").orNull();

        this.url = (String) context.property("hartshorn.data.url").orNull();
        if (this.url == null) throw new IllegalStateException("Connection string was not configured, expected hartshorn.data.url to be set, but got null");
    }

    @Override
    public PersistenceConnection connection(Object target, String user, String password) {
        throw new UnsupportedOperationException("Cannot create targeted connection from pre-configured remote.");
    }
}
