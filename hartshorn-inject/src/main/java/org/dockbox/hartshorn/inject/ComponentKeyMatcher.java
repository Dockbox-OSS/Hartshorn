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

/**
 * A matcher for component keys, to determine whether two component keys are considered equal for
 * a specific purpose or context.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public interface ComponentKeyMatcher {

    /**
     * Determines whether the {@code actual} component key is a match for the {@code requested}
     * component key. Typically the {@code actual} key is the one registered in a container, while
     * the {@code requested} key is the one being looked up.
     *
     * @param requested the component key being requested
     * @param actual the component key being checked for a match
     * @return true if the keys match, false otherwise
     */
    boolean matches(ComponentKey<?> requested, ComponentKey<?> actual);

    /**
     * Determines whether the {@code actual} component key view is a match for the {@code requested}
     * component key. Typically the {@code actual} key view is the one registered in a container,
     * while the {@code requested} key is the one being looked up.
     *
     * @param requested the component key being requested
     * @param actual the component key view being checked for a match
     * @return true if the keys match, false otherwise
     */
    boolean matches(ComponentKey<?> requested, ComponentKeyView<?> actual);
}
