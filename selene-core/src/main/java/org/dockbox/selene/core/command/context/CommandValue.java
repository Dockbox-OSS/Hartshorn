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

package org.dockbox.selene.core.command.context;

import org.dockbox.selene.core.command.parsing.Parser;
import org.dockbox.selene.core.objects.Exceptional;

@SuppressWarnings("ClassReferencesSubclass")
public class CommandValue<T> {

    private final String key;
    private final T value;

    public CommandValue(T value, String key) {
        this.key = key;
        this.value = value;
    }

    public Argument<T> asArgument() {
        return (Argument<T>) this;
    }

    public Flag<T> asFlag() {
        return (Flag<T>) this;
    }

    public <P> Exceptional<P> parse(Parser<P> parser, Class<P> type) {
        return parser.parse(this, type);
    }

    public String getKey() {
        return this.key;
    }

    public T getValue() {
        return this.value;
    }

    public enum Type {
        ARGUMENT, FLAG, BOTH
    }

    public static class Argument<T> extends CommandValue<T> {
        public Argument(T value, String key) {
            super(value, key);
        }
    }

    public static class Flag<T> extends CommandValue<T> {
        public Flag(T value, String key) {
            super(value, key);
        }
    }
}
