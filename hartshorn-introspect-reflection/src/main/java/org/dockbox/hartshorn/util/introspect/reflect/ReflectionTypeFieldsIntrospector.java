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

package org.dockbox.hartshorn.util.introspect.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.TypeFieldsIntrospector;
import org.dockbox.hartshorn.util.introspect.view.FieldView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

/**
 * TODO: #1059 Add documentation
 *
 * @param <T>> ...
 *
 * @since 0.4.13
 *
 * @author Guus Lieben
 */
public class ReflectionTypeFieldsIntrospector<T> implements TypeFieldsIntrospector<T> {

    private static final Set<String> EXCLUDED_FIELDS = Set.of(
            /*
             * This field is a synthetic field which is added by IntelliJ IDEA when running tests with
             * coverage. Refer to IDEA-274803 for more information.
             */
            "__$lineHits$__"
    );

    private final Map<String, FieldView<T, ?>> fields = new ConcurrentHashMap<>();

    private final Introspector introspector;
    private final TypeView<T> type;

    public ReflectionTypeFieldsIntrospector(Introspector introspector, TypeView<T> type) {
        this.introspector = introspector;
        this.type = type;
    }

    private void collect() {
        if (this.fields.isEmpty()) {
            for (Field declared : this.type.type().getDeclaredFields()) {
                if (EXCLUDED_FIELDS.contains(declared.getName())) {
                    continue;
                }

                this.fields.put(declared.getName(), (FieldView<T, ?>) this.introspector.introspect(declared));
            }
            if (!(this.type.superClass().isVoid() || Object.class.equals(this.type.superClass().type()))) {
                for (FieldView<?, ?> field : this.type.superClass().fields().all()) {
                    this.fields.put(field.name(), (FieldView<T, ?>) field);
                }
            }
        }
    }

    @Override
    public Option<FieldView<T, ?>> named(String name) {
        this.collect();
        if (this.fields.containsKey(name)) {
            return Option.of(this.fields.get(name));
        }
        else if (!this.type.superClass().isVoid()) {
            return this.type.superClass().fields().named(name)
                    .map(field -> (FieldView<T, ?>) field);
        }
        return Option.empty();
    }

    @Override
    public List<FieldView<T, ?>> all() {
        this.collect();
        return List.copyOf(this.fields.values());
    }

    @Override
    public List<FieldView<T, ?>> annotatedWith(Class<? extends Annotation> annotation) {
        return this.all().stream()
                .filter(field -> field.annotations().has(annotation))
                .toList();
    }

}
