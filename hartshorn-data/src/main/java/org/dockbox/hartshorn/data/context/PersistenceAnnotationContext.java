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

package org.dockbox.hartshorn.data.context;

import org.dockbox.hartshorn.data.FileFormats;
import org.dockbox.hartshorn.data.annotations.Deserialize;
import org.dockbox.hartshorn.data.annotations.File;
import org.dockbox.hartshorn.data.annotations.Serialize;

public class PersistenceAnnotationContext {

    private final FileFormats filetype;
    private final File file;

    public PersistenceAnnotationContext(final FileFormats filetype, final File file) {
        this.filetype = filetype;
        this.file = file;
    }

    public PersistenceAnnotationContext(final Serialize serialize) {
        this.file = serialize.path();
        this.filetype = serialize.filetype();
    }

    public PersistenceAnnotationContext(final Deserialize deserialize) {
        this.file = deserialize.path();
        this.filetype = deserialize.filetype();
    }

    public FileFormats filetype() {
        return this.filetype;
    }

    public File file() {
        return this.file;
    }
}
