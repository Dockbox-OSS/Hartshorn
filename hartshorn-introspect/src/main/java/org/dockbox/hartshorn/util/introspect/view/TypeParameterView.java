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

import org.dockbox.hartshorn.util.option.Option;

import java.util.Set;

public interface TypeParameterView extends View {

    int index();

    boolean isInputParameter();

    boolean isOutputParameter();

    TypeView<?> declaredBy();

    TypeView<?> consumedBy();

    Option<TypeParameterView> definition();

    Set<TypeParameterView> represents();

    Set<TypeView<?>> upperBounds();

    Option<TypeView<?>> resolvedType();

    boolean isBounded();

    boolean isUnbounded();

    boolean isClass();

    boolean isInterface();

    boolean isEnum();

    boolean isAnnotation();

    boolean isRecord();

    boolean isVariable();

    boolean isWildcard();

    TypeParameterView asInputParameter();
}
