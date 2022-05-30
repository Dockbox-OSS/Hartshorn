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

package org.dockbox.hartshorn.data.remote;

// TODO #000: Refactor to single remote datasource with DataSourceConfiguration
public abstract class JdbcRemote implements Remote<JdbcRemoteConfiguration> {

    protected String connectionString(final JdbcRemoteConfiguration server) {
        String connectionString = "jdbc:%s://%s:%s".formatted(this.type(), server.server(), server.port());
        if (this.includeDatabase()) connectionString = "%s/%s".formatted(connectionString, server.database());
        return connectionString;
    }

    protected abstract String type();

    protected boolean includeDatabase() {
        return true;
    }

    @Override
    public PersistenceConnection connection(final JdbcRemoteConfiguration target, final String user, final String password) {
        return new PersistenceConnection(this.connectionString(target), user, password, this);
    }
}
