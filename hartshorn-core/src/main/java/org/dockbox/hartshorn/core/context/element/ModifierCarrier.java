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

package org.dockbox.hartshorn.core.context.element;

import java.util.List;

@FunctionalInterface
public interface ModifierCarrier {

    List<AccessModifier> modifiers();

    default boolean isAbstract() {
        return this.has(AccessModifier.ABSTRACT);
    }

    default boolean isPublic() {
        return this.has(AccessModifier.PUBLIC);
    }

    default boolean isFinal() {
        return this.has(AccessModifier.FINAL);
    }

    default boolean has(final AccessModifier modifier) {
        return this.modifiers().contains(modifier);
    }

}
