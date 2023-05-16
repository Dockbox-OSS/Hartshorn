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

public interface ElementModifiersIntrospector {

    int asInt();

    boolean has(AccessModifier modifier);

    boolean isPublic();

    boolean isPrivate();

    boolean isProtected();

    boolean isStatic();

    boolean isFinal();

    boolean isAbstract();

    boolean isTransient();

    boolean isVolatile();

    boolean isSynchronized();

    boolean isNative();

    boolean isStrict();

    boolean isMandated();

    boolean isSynthetic();

    boolean isDefault();
}
