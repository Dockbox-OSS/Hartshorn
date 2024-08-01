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

package org.dockbox.hartshorn.util;

import java.util.Arrays;
import java.util.List;

/**
 * A node that contains an array of values. Unlike {@link GroupNode}, this node does not require values
 * to be named.
 *
 * @param <T> the type of the values
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class ArrayNode<T> extends SimpleNode<List<T>> {

    public ArrayNode(String name, List<T> value) {
        super(name, value);
    }

    @SafeVarargs
    public ArrayNode(String name, T... value) {
        super(name, Arrays.asList(value));
    }

    @Override
    public <R> R accept(NodeVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
