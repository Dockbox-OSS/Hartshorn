/*
 * Copyright 2019-2024 the original author or authors.
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

package org.dockbox.hartshorn.util.introspect.scan;

import java.util.Objects;

/**
 * A {@link TypeReference} that references a class by its fully qualified name. This reference can be used to
 * load the class, or to obtain information about the class.
 *
 * @since 0.4.13
 *
 * @author Guus Lieben
 */
public class ClassNameReference implements TypeReference {

    private final String name;

    public ClassNameReference(String name) {
        this.name = name;
    }

    @Override
    public Class<?> getOrLoad(ClassLoader classLoader) throws ClassReferenceLoadException {
        try {
            return Class.forName(this.name, false, classLoader);
        } catch (ClassNotFoundException e) {
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
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ClassNameReference reference)) {
            return false;
        }
        return this.name.equals(reference.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name);
    }
}
