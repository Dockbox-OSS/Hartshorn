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

import org.dockbox.hartshorn.data.remote.PersistenceConnection;
import org.hibernate.dialect.Dialect;

public class HibernateRemoteImpl implements HibernateRemote {

    private final Class<? extends Dialect> dialect;
    private final String driver;

    public HibernateRemoteImpl(final Class<? extends Dialect> dialect, final String driver) {
        this.dialect = dialect;
        this.driver = driver;
    }

    @Override
    public Class<? extends Dialect> dialect() {
        return this.dialect;
    }

    @Override
    public PersistenceConnection connection(final Object target, final String user, final String password) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String driver() {
        return this.driver;
    }
}
