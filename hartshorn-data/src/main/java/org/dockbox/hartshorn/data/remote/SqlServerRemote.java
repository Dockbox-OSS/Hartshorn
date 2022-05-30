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

public final class SqlServerRemote extends JdbcRemote {

    public static final SqlServerRemote INSTANCE = new SqlServerRemote();

    private SqlServerRemote() {
    }

    @Override
    protected boolean includeDatabase() {
        return false;
    }

    @Override
    protected String connectionString(final JdbcRemoteConfiguration server) {
        return super.connectionString(server) + ";encrypt=true;trustServerCertificate=true;";
    }

    @Override
    protected String type() {
        return "sqlserver";
    }

    @Override
    public String driver() {
        return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    }
}
