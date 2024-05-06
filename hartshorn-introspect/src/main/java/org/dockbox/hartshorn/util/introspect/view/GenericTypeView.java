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

package org.dockbox.hartshorn.util.introspect.view;

/**
 * Represents a view of an element which may have a generic type. This view can be used to
 * introspect the element's generic type, allowing you to extract information about the type's
 * type parameters.
 *
 * @param <T> the type of the element's generic type
 *
 * @since 0.4.8
 *
 * @author Guus Lieben
 */
public interface GenericTypeView<T> extends View {

    /**
     * Returns a {@link TypeView} for the element's non-generic type. A non-generic type can be
     * for example {@code TypeView<List>} for a {@code List<String>}.
     *
     * @return a view of the element's non-generic type
     */
    TypeView<T> type();

    /**
     * Returns a {@link TypeView} for the element's generic type. A generic type can be for
     * example {@code TypeView<List<String>>} for a {@code List<String>}.
     *
     * @return a view of the element's generic type
     */
    TypeView<T> genericType();

}
