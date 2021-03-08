/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.api.objects.tuple;

/**
 * Represents a triad of values
 *
 * <p>There is no meaning attached to values in this class, it can be used for any purpose. Triple
 * exhibits value semantics, i.e. two triples are equal if all three components are equal.
 *
 * @param <A>
 *         type of the first value.
 * @param <B>
 *         type of the second value.
 * @param <C>
 *         type of the third value.
 */
public class Triad<A, B, C> {

    private final A first;
    private final B second;
    private final C third;

    public Triad(A first, B second, C third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public A getFirst() {
        return this.first;
    }

    public B getSecond() {
        return this.second;
    }

    public C getThird() {
        return this.third;
    }

    @Override
    public String toString() {
        return "Triad[" + this.first + ", " + this.second + ", " + this.third + ']';
    }
}
