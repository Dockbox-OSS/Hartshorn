/*
 * Copyright 2019-2023 the original author or authors.
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

package org.dockbox.hartshorn.application.scan;

import java.util.Objects;

public class ClassNameReference implements TypeReference {

    private final String name;

    public ClassNameReference(final String name) {
        this.name = name;
    }

    @Override
    public Class<?> getOrLoad() throws ClassReferenceLoadException {
        try {
            return Class.forName(this.name);
        } catch (final ClassNotFoundException e) {
            throw new ClassReferenceLoadException(e);
        }
    }

    @Override
    public String qualifiedName() {
        return this.name;
    }

    @Override
    public String simpleName() {
        return this.name.substring(this.name.lastIndexOf('.') + 1);
    }

    @Override
    public String packageName() {
        return this.name.substring(0, this.name.lastIndexOf('.'));
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof final ClassNameReference that)) return false;
        return this.name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name);
    }
}
