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

package org.dockbox.hartshorn.util.introspect;

import java.lang.reflect.ParameterizedType;
import java.util.Objects;

record ParameterizableParameterizedTypeWrapper<T>(ParameterizableType<T> type) implements ParameterizedType {

    @Override
    public java.lang.reflect.Type[] getActualTypeArguments() {
        return this.type.parameters().stream()
                .map(ParameterizableType::asParameterizedType)
                .toArray(java.lang.reflect.Type[]::new);
    }

    @Override
    public java.lang.reflect.Type getRawType() {
        return this.type.type();
    }

    @Override
    public java.lang.reflect.Type getOwnerType() {
        return null;
    }

    @Override
    public boolean equals(Object object) {
        if(this == object) {
            return true;
        }
        if(!(object instanceof ParameterizableParameterizedTypeWrapper<?> that)) {
            return false;
        }
        return Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }
}
