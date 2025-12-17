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

package org.dockbox.hartshorn.inject;

import org.dockbox.hartshorn.util.introspect.ParameterizableType;

import java.util.List;

public class SimpleComponentKeyMatcher implements ComponentKeyMatcher {

    public static final ComponentKeyMatcher INSTANCE = new SimpleComponentKeyMatcher();

    @Override
    public boolean matches(ComponentKey<?> left, ComponentKey<?> right) {
        // Strict matching, use equality if either key is strict
        if (left.strict().booleanValue() || right.strict().booleanValue()) {
            return StrictComponentKeyMatcher.INSTANCE.matches(left, right);
        }
        // Fuzzy matching otherwise
        return FuzzyComponentKeyMatcher.INSTANCE.matches(left, right);
    }

    @Override
    public boolean matches(ComponentKey<?> left, ComponentKeyView<?> right) {
        // Strict matching, use equality if the key is strict
        if (left.strict().booleanValue()) {
            return StrictComponentKeyMatcher.INSTANCE.matches(left, right);
        }
        // Fuzzy matching otherwise
        return FuzzyComponentKeyMatcher.INSTANCE.matches(left, right);
    }

    public static class AbstractComponentKeyMatcher implements ComponentKeyMatcher {

        @Override
        public boolean matches(ComponentKey<?> left, ComponentKey<?> right) {
            if (left == right) {
                return true;
            }
            if (right == null || left == null) {
                return false;
            }
            if (left.postConstructionAllowed() != right.postConstructionAllowed()) {
                return false;
            }
            if (!left.qualifier().equals(right.qualifier())) {
                return false;
            }
            return true;
        }

        @Override
        public boolean matches(ComponentKey<?> left, ComponentKeyView<?> right) {
            if (left == null || right == null) {
                return false;
            }
            if (!left.qualifier().equals(right.qualifier())) {
                return false;
            }
            return true;
        }
    }

    public static class StrictComponentKeyMatcher extends AbstractComponentKeyMatcher {

        public static final ComponentKeyMatcher INSTANCE = new StrictComponentKeyMatcher();

        @Override
        public boolean matches(ComponentKey<?> left, ComponentKey<?> right) {
            if (!super.matches(left, right)) {
                return false;
            }
            if (!left.parameterizedType().equals(right.parameterizedType())) {
                return false;
            }
            return true;
        }

        @Override
        public boolean matches(ComponentKey<?> requestedType, ComponentKeyView<?> actualType) {
            if (!super.matches(requestedType, actualType)) {
                return false;
            }
            if (!requestedType.parameterizedType().equals(actualType.type())) {
                return false;
            }
            return true;
        }
    }

    public static class FuzzyComponentKeyMatcher extends AbstractComponentKeyMatcher {

        public static final ComponentKeyMatcher INSTANCE = new FuzzyComponentKeyMatcher();

        @Override
        public boolean matches(ComponentKey<?> left, ComponentKey<?> right) {
            if (!super.matches(left, right)) {
                return false;
            }
            if (!isCompatible(left.parameterizedType(), right.parameterizedType())) {
                return false;
            }
            return true;
        }

        @Override
        public boolean matches(ComponentKey<?> requestedType, ComponentKeyView<?> actualType) {
            if (!super.matches(requestedType, actualType)) {
                return false;
            }
            if (!isCompatible(requestedType.parameterizedType(), actualType.type())) {
                return false;
            }
            return true;
        }

        private boolean isCompatible(ParameterizableType requestedType, ParameterizableType actualType) {
            if (!requestedType.type().isAssignableFrom(actualType.type())) {
                return false;
            }
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
