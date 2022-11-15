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

package org.dockbox.hartshorn.jpa.remote;

import java.sql.Driver;

public class DataSourceConfiguration {
    private String url;
    private String username;
    private String password;
    private Class<? extends Driver> driver;

    protected DataSourceConfiguration() {
        // For configuration
    }

    public DataSourceConfiguration(final String url, final Class<? extends Driver> driver) {
        this.url = url;
        this.driver = driver;
        this.username = null;
        this.password = null;
    }

    public DataSourceConfiguration(final String url, final String username, final String password, final Class<? extends Driver> driver) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.driver = driver;
    }

    public String url() {
        return this.url;
    }

    public String username() {
        return this.username;
    }

    public String password() {
        return this.password;
    }

    public Class<? extends Driver> driver() {
        return this.driver;
    }
}
