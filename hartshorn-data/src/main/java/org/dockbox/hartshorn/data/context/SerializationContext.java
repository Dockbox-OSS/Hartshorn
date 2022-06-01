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

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.context.DefaultContext;
import org.dockbox.hartshorn.data.FileFormats;

import java.nio.file.Path;

public class SerializationContext extends DefaultContext {

    private SerializationTarget target;
    private FileFormats fileFormat;

    @Nullable
    private Path predeterminedPath;

    public SerializationTarget target() {
        return this.target;
    }

    public SerializationContext target(final SerializationTarget target) {
        this.target = target;
        return this;
    }

    public FileFormats fileFormat() {
        return this.fileFormat;
    }

    public SerializationContext fileFormat(final FileFormats fileFormat) {
        this.fileFormat = fileFormat;
        return this;
    }

    public Path predeterminedPath() {
        return this.predeterminedPath;
    }

    public SerializationContext predeterminedPath(final Path predeterminedPath) {
        this.predeterminedPath = predeterminedPath;
        return this;
    }
}