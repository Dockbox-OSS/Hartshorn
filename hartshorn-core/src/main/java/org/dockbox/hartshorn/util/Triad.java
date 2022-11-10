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

package org.dockbox.hartshorn.util;

import java.util.Objects;

/**
 * Represents a triad of values
 *
 * <p>There is no meaning attached to values in this class, it can be used for any purpose. Triple
 * exhibits value semantics, i.e. two triples are equal if all three components are equal.
 *
 * @param <A> type of the first value.
 * @param <B> type of the second value.
 * @param <C> type of the third value.
 */
@Deprecated(forRemoval = true, since = "22.5")
public class Triad<A, B, C> {

    private final A first;
    private final B second;
    private final C third;

    public Triad(final A first, final B second, final C third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public A first() {
        return this.first;
    }

    public B second() {
        return this.second;
    }

    public C third() {
        return this.third;
    }

    @Override
    public String toString() {
        return "Triad[" + this.first + ", " + this.second + ", " + this.third + ']';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        final Triad<?, ?, ?> triad = (Triad<?, ?, ?>) o;
        return Objects.equals(this.first, triad.first) && Objects.equals(this.second, triad.second) && Objects.equals(this.third, triad.third);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.first, this.second, this.third);
    }
}
