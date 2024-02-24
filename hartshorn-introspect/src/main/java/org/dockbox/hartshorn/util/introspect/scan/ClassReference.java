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

/**
 * A {@link TypeReference} that represents a {@link Class}. No guarantees are made about the state of the class
 * represented by this reference. It is possible that the class is not yet initialized.
 *
 * @since 0.4.13
 *
 * @author Guus Lieben
 */
public class ClassReference implements TypeReference {

    private final Class<?> type;

    public ClassReference(Class<?> type) {
        this.type = type;
    }

    @Override
    public Class<?> getOrLoad(ClassLoader classLoader) {
        return this.type;
    }

    @Override
    public String qualifiedName() {
        return this.type.getCanonicalName();
    }

    @Override
    public String simpleName() {
        return this.type.getSimpleName();
    }

    @Override
    public String packageName() {
        return this.type.getPackage().getName();
    }
}
