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

package org.dockbox.hartshorn.util.introspect.view.wildcard;

import org.dockbox.hartshorn.util.introspect.ElementAnnotationsIntrospector;
import org.dockbox.hartshorn.util.option.Option;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Set;

/**
 * An {@link ElementAnnotationsIntrospector} that does not provide access to any annotations. This
 * introspector is commonly used for wildcard types.
 *
 * @since 0.4.13
 *
 * @author Guus Lieben
 */
public class WildcardElementAnnotationsIntrospector implements ElementAnnotationsIntrospector {

    @Override
    public Set<Annotation> all() {
        return Collections.emptySet();
    }

    @Override
    public Set<Annotation> annotedWith(Class<? extends Annotation> annotation) {
        return Collections.emptySet();
    }

    @Override
    public boolean has(Class<? extends Annotation> annotation) {
        return false;
    }

    @Override
    public boolean hasAny(Set<Class<? extends Annotation>> annotations) {
        return false;
    }

    @Override
    public boolean hasAll(Set<Class<? extends Annotation>> annotations) {
        return false;
    }

    @Override
    public <T extends Annotation> Option<T> get(Class<T> annotation) {
        return Option.empty();
    }

    @Override
    public <T extends Annotation> Set<T> all(Class<T> annotation) {
        return Collections.emptySet();
    }

    @Override
    public int count() {
        return 0;
    }
}
