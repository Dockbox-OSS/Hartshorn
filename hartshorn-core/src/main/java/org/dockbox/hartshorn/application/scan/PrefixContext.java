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

package org.dockbox.hartshorn.application.scan;

import org.dockbox.hartshorn.context.Context;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Set;

public interface PrefixContext extends Context {

    void prefix(String prefix);

    Set<String> prefixes();

    <A extends Annotation> Collection<TypeView<?>> types(final Class<A> annotation);

    <A extends Annotation> Collection<TypeView<?>> types(final Class<A> annotation, final boolean skipParents);

    <A extends Annotation> Collection<TypeView<?>> types(final String prefix, final Class<A> annotation, final boolean skipParents);

    <T> Collection<TypeView<? extends T>> children(final Class<T> type);

    <T> Collection<TypeView<? extends T>> children(final TypeView<T> parent);
}
