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

package org.dockbox.hartshorn.data;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class NestedElement implements Element {

    private EntityElement child;

    public NestedElement() {
    }

    public NestedElement(final EntityElement child) {
        this.child = child;
    }

    @Override
    @JsonIgnore
    public String name() {
        return this.child.name();
    }

    @Override
    @JsonIgnore
    public NestedElement name(final String name) {
        this.child.name(name);
        return this;
    }

    public EntityElement child() {
        return this.child;
    }

    public NestedElement child(final EntityElement child) {
        this.child = child;
        return this;
    }
}
