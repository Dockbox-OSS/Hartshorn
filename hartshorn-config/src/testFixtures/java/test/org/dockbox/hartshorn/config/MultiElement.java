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

package test.org.dockbox.hartshorn.config;

import java.util.Objects;

public class MultiElement implements Element {

    private String name;
    private String other;

    public MultiElement() {
    }

    public MultiElement(final String name, final String other) {
        this.name = name;
        this.other = other;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public MultiElement name(final String name) {
        this.name = name;
        this.other = name;
        return this;
    }

    public String other() {
        return this.other;
    }

    public MultiElement other(final String other) {
        this.other = other;
        return this;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) return true;
        if (other == null || this.getClass() != other.getClass()) return false;
        final MultiElement element = (MultiElement) other;
        return Objects.equals(this.name, element.name) && Objects.equals(this.other, element.other);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.other);
    }
}
