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

package org.dockbox.hartshorn.component;

import java.util.Objects;

public class ScopeKey<T extends Scope> {

    private final Class<T> scopeType;

    protected ScopeKey(Class<T> scopeType) {
        this.scopeType = scopeType;
    }

    public String name() {
        return this.scopeType.getSimpleName();
    }

    public Class<T> scopeType() {
        return this.scopeType;
    }

    public static <T extends Scope> ScopeKey<T> of(Class<T> scopeType) {
        return new ScopeKey<>(scopeType);
    }

    @Override
    public boolean equals(Object object) {
        if(this == object) {
            return true;
        }
        if(!(object instanceof ScopeKey<?> scopeKey)) {
            return false;
        }
        return Objects.equals(scopeType, scopeKey.scopeType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scopeType);
    }
}
