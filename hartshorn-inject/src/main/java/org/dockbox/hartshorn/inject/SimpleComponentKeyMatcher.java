/*
 * Copyright 2019-2026 the original author or authors.
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

package org.dockbox.hartshorn.inject;

import org.dockbox.hartshorn.util.Tristate;
import org.dockbox.hartshorn.util.introspect.ParameterizableType;

import java.util.List;

/**
 * A simple implementation of {@link ComponentKeyMatcher} which delegates to either
 * {@link StrictComponentKeyMatcher} or {@link FuzzyComponentKeyMatcher} based on the
 * strictness of the requested and actual keys.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class SimpleComponentKeyMatcher implements ComponentKeyMatcher {

    private final InjectorConfiguration configuration;

    private SimpleComponentKeyMatcher(InjectorConfiguration configuration) {
        this.configuration = configuration;
    }

    /**
     * Creates a new {@link SimpleComponentKeyMatcher} with the given configuration.
     *
     * @param configuration the injector configuration
     * @return a new simple component key matcher
     */
    public static ComponentKeyMatcher create(InjectorConfiguration configuration) {
        return new SimpleComponentKeyMatcher(configuration);
    }

    @Override
    public boolean matches(ComponentKey<?> requested, ComponentKey<?> actual) {
        // Strict matching, use equality if either key is strict
        if (this.isStrict(requested) || this.isStrict(actual)) {
            return StrictComponentKeyMatcher.INSTANCE.matches(requested, actual);
        }
        // Fuzzy matching otherwise
        return FuzzyComponentKeyMatcher.INSTANCE.matches(requested, actual);
    }

    @Override
    public boolean matches(ComponentKey<?> requested, ComponentKeyView<?> actual) {
        // Strict matching, use equality if the key is strict
        if (this.isStrict(requested)) {
            return StrictComponentKeyMatcher.INSTANCE.matches(requested, actual);
        }
        // Fuzzy matching otherwise
        return FuzzyComponentKeyMatcher.INSTANCE.matches(requested, actual);
    }

    private boolean isStrict(ComponentKey<?> key) {
        if (key.strict() != Tristate.UNDEFINED) {
            return key.strict().booleanValue();
        }
        return this.configuration.isStrictMode();
    }

    /**
     * An abstract base implementation of {@link ComponentKeyMatcher} which compares common
     * properties of component keys.
     *
     * @since 0.7.0
     *
     * @author Guus Lieben
     */
    public static class AbstractComponentKeyMatcher implements ComponentKeyMatcher {

        @Override
        public boolean matches(ComponentKey<?> requested, ComponentKey<?> actual) {
            if (requested == actual) {
                return true;
            }
            if (actual == null || requested == null) {
                return false;
            }
            if (requested.postConstructionAllowed() != actual.postConstructionAllowed()) {
                return false;
            }
            if (!requested.qualifier().equals(actual.qualifier())) {
                return false;
            }
            return true;
        }

        @Override
        public boolean matches(ComponentKey<?> requested, ComponentKeyView<?> actual) {
            if (requested == null || actual == null) {
                return false;
            }
            if (!requested.qualifier().equals(actual.qualifier())) {
                return false;
            }
            return true;
        }
    }

    /**
     * A strict implementation of {@link ComponentKeyMatcher} which requires exact matches of
     * parameterized types. This means that not only the raw types must match, but also all type
     * parameters must be equal.
     *
     * @since 0.7.0
     *
     * @author Guus Lieben
     */
    public static class StrictComponentKeyMatcher extends AbstractComponentKeyMatcher {

        public static final ComponentKeyMatcher INSTANCE = new StrictComponentKeyMatcher();

        @Override
        public boolean matches(ComponentKey<?> requested, ComponentKey<?> actual) {
            if (!super.matches(requested, actual)) {
                return false;
            }
            if (!requested.parameterizedType().equals(actual.parameterizedType())) {
                return false;
            }
            return true;
        }

        @Override
        public boolean matches(ComponentKey<?> requested, ComponentKeyView<?> actual) {
            if (!super.matches(requested, actual)) {
                return false;
            }
            if (!requested.parameterizedType().equals(actual.type())) {
                return false;
            }
            return true;
        }
    }

    /**
     * A fuzzy implementation of {@link ComponentKeyMatcher} which allows for compatible matches
     * of parameterized types. This means that the raw types must be assignable, and all type
     * parameters must be compatible. As types only required to be assignable, this allows for
     * more flexible matching.
     *
     * @since 0.7.0
     *
     * @author Guus Lieben
     */
    public static class FuzzyComponentKeyMatcher extends AbstractComponentKeyMatcher {

        public static final ComponentKeyMatcher INSTANCE = new FuzzyComponentKeyMatcher();

        @Override
        public boolean matches(ComponentKey<?> requested, ComponentKey<?> actual) {
            if (!super.matches(requested, actual)) {
                return false;
            }
            if (!isCompatible(requested.parameterizedType(), actual.parameterizedType())) {
                return false;
            }
            return true;
        }

        @Override
        public boolean matches(ComponentKey<?> requested, ComponentKeyView<?> actual) {
            if (!super.matches(requested, actual)) {
                return false;
            }
            if (!isCompatible(requested.parameterizedType(), actual.type())) {
                return false;
            }
            return true;
        }

        private boolean isCompatible(
                ParameterizableType requestedType,
                ParameterizableType actualType
        ) {
            if (!requestedType.type().isAssignableFrom(actualType.type())) {
                return false;
            }

            // Implementation note: We assume here that both types have the same number of
            // parameters if they are parameterized types. If they do not, the types are not
            // compatible. While this is a simplification, it is sufficient for most use cases in
            // dependency injection (e.g. ComponentCollection<String> is still compatible with
            // Collection<CharSequence>).
            List<ParameterizableType> originalParameters = requestedType.parameters();
            List<ParameterizableType> targetParameters = actualType.parameters();
            if (originalParameters.size() != targetParameters.size()) {
                return false;
            }
            for (int i = 0; i < originalParameters.size(); i++) {
                ParameterizableType originalParameter = originalParameters.get(i);
                ParameterizableType targetParameter = targetParameters.get(i);
                if (!this.isCompatible(originalParameter, targetParameter)) {
                    return false;
                }
            }
            return true;
        }
    }
}
