/*
 * Copyright 2019-2025 the original author or authors.
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

package org.dockbox.hartshorn.test;

import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactory;
import org.dockbox.hartshorn.util.option.Option;

import java.util.function.Function;

/**
 * Assertion extensions for Hartshorn specific types.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class HartshornAssertions {

    /**
     * {@link InstanceOfAssertFactory} to allow {@link Option} instances to be used inline with
     * {@link AbstractObjectAssert#extracting(Function, InstanceOfAssertFactory)}, yielding a
     * {@link OptionAssert} instance which is type-checked for the given element type.
     *
     * @param element the element type inside the {@link Option}
     * @param <E> the element type
     *
     * @return a new {@link InstanceOfAssertFactory} for {@link Option} types
     */
    public static <E> InstanceOfAssertFactory<Option, OptionAssert<E>> option(Class<E> element) {
        return new InstanceOfAssertFactory<>(
                Option.class,
                new Class[] {element},
                e -> assertThat(e).cast(element)
        );
    }

    /**
     * Creates a new assertion for the given non-null {@link Option}.
     *
     * @param option the non-null {@link Option}
     * @param <E> the element type
     *
     * @return a new {@link OptionAssert}
     */
    public static <E> OptionAssert<E> assertThat(Option<E> option) {
        Assertions.assertThat(option).isNotNull();
        return new OptionAssert<>(option);
    }
}
