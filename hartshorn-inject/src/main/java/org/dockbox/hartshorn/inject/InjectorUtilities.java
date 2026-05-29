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

/**
 * Utility class for injector-related operations.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public final class InjectorUtilities {

    private InjectorUtilities() {
    }

    /**
     * Determines whether the given key should be treated in strict mode. If the key has a defined
     * strictness, that value is used. Otherwise, the global configuration is used.
     *
     * @param key the component key
     * @param configuration the injector configuration
     *
     * @return true if the key should be treated in strict mode, false otherwise
     *
     * @see InjectorConfiguration#isStrictMode()
     */
    public static boolean isStrict(ComponentKey<?> key, InjectorConfiguration configuration) {
        return key.strict().booleanValue(configuration::isStrictMode);
    }

    /**
     * Determines whether the given key should include parent scopes when resolving components. If
     * the key has a defined value for including parent scopes, that value is used. Otherwise, the
     * global configuration is used if the key is non-strict (fuzzy).
     *
     * @param key the component key
     * @param configuration the injector configuration
     *
     * @return true if parent scopes should be included when resolving components for the given
     * key, false otherwise
     *
     * @see InjectorConfiguration#includeParentScopeForFuzzyMatching()
     */
    public static boolean includeParentScope(
            ComponentKey<?> key,
            InjectorConfiguration configuration
    ) {
        return key.includeParentScopes().booleanValue(() ->
                !isStrict(key, configuration) && configuration.includeParentScopeForFuzzyMatching()
        );
    }
}
