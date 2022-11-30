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

package org.dockbox.hartshorn.reporting.collect;

import java.util.Arrays;
import java.util.List;

public class ArrayNode<T> extends SimpleNode<List<T>> {

    private ArrayNode(final String name, final List<T> value) {
        super(name, value);
    }

    @Override
    public <R> R accept(final NodeVisitor<R> visitor) {
        return visitor.visit(this);
    }

    public static <T> ArrayNode<T> of(final String name, final T[] value) {
        return new ArrayNode<>(name, Arrays.asList(value));
    }

    public static <T> ArrayNode<T> of(final String name, final List<T> value) {
        return new ArrayNode<>(name, value);
    }
}
