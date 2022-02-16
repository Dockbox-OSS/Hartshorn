/*
 * Copyright 2019-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dockbox.hartshorn.data.hibernate;

import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.data.remote.PersistenceConnection;
import org.hibernate.dialect.Dialect;

import javax.inject.Inject;

import lombok.Getter;

@Getter
public class HibernateRemoteImpl implements HibernateRemote {

    private final String driver;
    private final Class<? extends Dialect> dialect;

    private final String username;
    private final String password;
    private final String url;

    @Inject
    public HibernateRemoteImpl(final ApplicationContext context) {
        final Exceptional<String> remoteType = context.property("hartshorn.data.remote");

        this.driver = (String) context.property("hartshorn.data.hibernate.driver_class").orNull();
        if (this.driver == null) throw new IllegalStateException("Driver class was not configured, expected hartshorn.data.hibernate.driver_class or hartshorn.data.remote to be set, but got null");

        final String dialect = (String) context.property("hartshorn.data.hibernate.dialect").orNull();
        if (dialect == null) throw new IllegalStateException("Dialect was not configured, expected hartshorn.data.hibernate.dialect or hartshorn.data.remote to be set, but got null");

        final TypeContext<?> dialectContext = TypeContext.lookup(dialect);
        if (!dialectContext.childOf(Dialect.class)) throw new IllegalStateException("Expected dialect to be a subtype of " + Dialect.class.getCanonicalName());

        this.dialect = (Class<? extends Dialect>) dialectContext.type();

        this.username = (String) context.property("hartshorn.data.username").orNull();
        this.password = (String) context.property("hartshorn.data.password").orNull();

        this.url = (String) context.property("hartshorn.data.url").orNull();
        if (this.url == null) throw new IllegalStateException("Connection string was not configured, expected hartshorn.data.url to be set, but got null");
    }

    @Override
    public PersistenceConnection connection(final Object target, final String user, final String password) {
        throw new UnsupportedOperationException("Cannot create targeted connection from pre-configured remote.");
    }

    @Override
    public PersistenceConnection connection() {
        return new PersistenceConnection(this.url(), this.username(), this.password(), this);
    }
}
