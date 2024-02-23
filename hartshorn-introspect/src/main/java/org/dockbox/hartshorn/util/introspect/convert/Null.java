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

package org.dockbox.hartshorn.util.introspect.convert;

/**
 * A marker class used to represent a null value. This is used to avoid unnecessary conversions
 * when using {@link DefaultValueProvider}s. The instance of this class is not intended to be
 * used outside the {@link org.dockbox.hartshorn.util.introspect.convert} package, and is thus
 * reserved for internal use only.
 *
 * @author Guus Lieben
 * @since 0.5.0
 */
public final class Null {

    /**
     * The type of the {@link Null} instance.
     */
    public static final Class<Null> TYPE = Null.class;

    /**
     * The singleton instance of {@link Null}. Should not be used directly through public APIs,
     * as the conversion service itself should only expect {@code null} and translate that to
     * {@link Null} internally.
     */
    static final Null INSTANCE = new Null();

    private Null() {
    }
}
