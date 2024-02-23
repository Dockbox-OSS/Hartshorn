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

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

import org.dockbox.hartshorn.util.introspect.TypeConstructorsIntrospector;
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.option.Option;

/**
 * A {@link TypeConstructorsIntrospector} implementation for wildcard types.
 *
 * @since 0.4.13
 *
 * @author Guus Lieben
 */
public class WildcardTypeConstructorsIntrospector implements TypeConstructorsIntrospector<Object> {

    @Override
    public Option<ConstructorView<Object>> defaultConstructor() {
        return Option.empty();
    }

    @Override
    public List<ConstructorView<Object>> annotatedWith(Class<? extends Annotation> annotation) {
        return Collections.emptyList();
    }

    @Override
    public Option<ConstructorView<Object>> withParameters(List<Class<?>> parameters) {
        return Option.empty();
    }

    @Override
    public List<ConstructorView<Object>> all() {
        return Collections.emptyList();
    }

    @Override
    public int count() {
        return 0;
    }
}
