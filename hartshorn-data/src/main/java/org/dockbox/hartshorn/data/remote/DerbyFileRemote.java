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

import java.nio.file.Path;

public final class DerbyFileRemote implements Remote<Path> {

    @Override
    public PersistenceConnection connection(final Path target, final String user, final String password) {
        return new PersistenceConnection(
                "jdbc:derby:directory:%s/db;create=true".formatted(target.toFile().getAbsolutePath()),
                user,
                password,
                this
        );
    }

    @Override
    public String driver() {
        return "org.apache.derby.jdbc.EmbeddedDriver";
    }
}
