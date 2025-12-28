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

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.AssertFactory;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ObjectAssert;
import org.assertj.core.api.OptionalAssert;
import org.dockbox.hartshorn.util.option.Option;

/**
 * Custom assertions for {@link Option} instances.
 *
 * @param <E> the element type of the {@link Option}
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class OptionAssert<E> extends AbstractAssert<OptionAssert<E>, Option<E>> {

    protected OptionAssert(Option<E> option) {
        super(option, OptionAssert.class);
    }

    /**
     * Asserts the {@link Option} does not contain any value.
     *
     * @return the current assertion, for fluent expressions
     *
     * @see Option#absent()
     */
    public OptionAssert<E> absent() {
        return this.matches(Option::absent);
    }

    /**
     * Asserts the {@link Option} contains a value.
     *
     * @return the current assertion, for fluent expressions
     *
     * @see Option#present()
     */
    public OptionAssert<E> present() {
        return this.matches(Option::present);
    }

    /**
     * Extracts the value contained in the {@link Option} and returns an {@link ObjectAssert} for
     * further assertions on the value.
     *
     * @return an {@link ObjectAssert} for the value contained in the {@link Option}
     */
    public ObjectAssert<E> value() {
        return this.present().extracting(Option::get, Assertions::assertThat);
    }

    /**
     * Extracts the value contained in the {@link Option} and returns an {@link AbstractAssert} of
     * the specified type for further assertions on the value.
     *
     * @param assertFactory the factory to create the specific assert type
     * @param <A> the type of the specific assert
     *
     * @return an {@link AbstractAssert} of the specified type for the value contained in the
     * {@link Option}
     */
    public <A extends AbstractAssert<?, E>> A value(AssertFactory<Object, A> assertFactory) {
        return this.present().extracting(Option::get, assertFactory);
    }

    /**
     * Asserts the {@link Option} contains the expected value.
     *
     * @param expected the expected value
     *
     * @return the current assertion, for fluent expressions
     *
     * @see Option#contains(Object)
     */
    public OptionAssert<E> contains(E expected) {
        return this.matches(option -> option.contains(expected));
    }

    /**
     * Casts the value contained in the {@link Option} to the specified type and returns a new
     * {@link OptionAssert} for further assertions.
     *
     * @param type the type to cast the value to
     * @param <U> the type of the value after casting
     *
     * @return a new {@link OptionAssert} for the casted value
     *
     * @see Option#cast(Class)
     */
    public <U> OptionAssert<U> cast(Class<U> type) {
        return this.extracting(option -> option.cast(type), OptionAssert::new);
    }

    /**
     * Converts the {@link Option} to an {@link OptionalAssert} for further assertions using
     * AssertJ's built-in optional assertions.
     *
     * @return an {@link OptionalAssert} for the {@link Option}
     *
     * @see Option#optional()
     */
    public OptionalAssert<E> asOptional() {
        return this.extracting(Option::optional, Assertions::assertThat);
    }
}
