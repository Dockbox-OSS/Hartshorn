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

package org.dockbox.hartshorn.util.introspect.view;

import org.dockbox.hartshorn.util.introspect.AccessModifier;
import org.dockbox.hartshorn.util.introspect.ElementModifiersIntrospector;

public interface ModifierCarrierView extends View {

    /**
     * @deprecated use {@link #modifiers()} and {@link ElementModifiersIntrospector#isPublic()} instead
     * @return true if the modifier is present
     */
    @Deprecated(forRemoval = true, since = "23.1")
    default boolean isPublic() {
        return this.modifiers().isPublic();
    }

    /**
     * @deprecated use {@link #modifiers()} and {@link ElementModifiersIntrospector#isProtected()} instead
     * @return true if the modifier is present
     */
    @Deprecated(forRemoval = true, since = "23.1")
    default boolean isProtected() {
        return this.modifiers().isProtected();
    }

    /**
     * @deprecated use {@link #modifiers()} and {@link ElementModifiersIntrospector#isPrivate()} instead
     * @return true if the modifier is present
     */
    @Deprecated(forRemoval = true, since = "23.1")
    default boolean isPrivate() {
        return this.modifiers().isPrivate();
    }

    /**
     * @deprecated use {@link #modifiers()} and {@link ElementModifiersIntrospector#has(AccessModifier)} instead
     *
     * @param modifier the modifier to check for
     * @return true if the modifier is present
     */
    @Deprecated(forRemoval = true, since = "23.1")
    boolean has(AccessModifier modifier);

    ElementModifiersIntrospector modifiers();
}
