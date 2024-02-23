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

package org.dockbox.hartshorn.util.introspect.view.wildcard;

import org.dockbox.hartshorn.util.introspect.AccessModifier;
import org.dockbox.hartshorn.util.introspect.ElementModifiersIntrospector;

/**
 * A {@link ElementModifiersIntrospector} implementation for wildcard types.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class WildcardElementModifiersIntrospector implements ElementModifiersIntrospector {

    @Override
    public int asInt() {
        return 0;
    }

    @Override
    public boolean has(AccessModifier modifier) {
        return false;
    }

    @Override
    public boolean isPublic() {
        return false;
    }

    @Override
    public boolean isPrivate() {
        return false;
    }

    @Override
    public boolean isProtected() {
        return false;
    }

    @Override
    public boolean isStatic() {
        return false;
    }

    @Override
    public boolean isFinal() {
        return false;
    }

    @Override
    public boolean isAbstract() {
        return false;
    }

    @Override
    public boolean isTransient() {
        return false;
    }

    @Override
    public boolean isVolatile() {
        return false;
    }

    @Override
    public boolean isSynchronized() {
        return false;
    }

    @Override
    public boolean isNative() {
        return false;
    }

    @Override
    public boolean isStrict() {
        return false;
    }

    @Override
    public boolean isMandated() {
        return false;
    }

    @Override
    public boolean isSynthetic() {
        return false;
    }

    @Override
    public boolean isDefault() {
        return false;
    }
}
