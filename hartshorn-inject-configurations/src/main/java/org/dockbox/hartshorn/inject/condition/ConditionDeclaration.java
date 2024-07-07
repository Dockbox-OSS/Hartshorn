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

package org.dockbox.hartshorn.inject.condition;

import org.dockbox.hartshorn.inject.InjectionCapableApplication;

/**
 * Represents a condition declaration, which may put constraints on the usage of a component
 * or element in the application. This may be used to enforce certain conditions, such as
 * the presence of a service activator, or the presence and value of a certain property.
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public interface ConditionDeclaration {

    /**
     * Returns the condition that is declared by this instance. The condition may use any
     * context that is provided by the application context.
     *
     * @param application The application context that is used to resolve the condition
     * @return The condition that is declared by this instance
     */
    Condition condition(InjectionCapableApplication application);

    /**
     * Indicates whether the {@link ConditionMatcher} should fail when the condition does not match.
     * When this method returns {@code true}, the condition matcher will throw a {@link ConditionFailedException}
     * when the condition does not match. When this method returns {@code false}, the condition matcher will
     * only indicate that the condition does not match.
     *
     * @return {@code true} when the condition matcher should fail when the condition does not match, {@code false} otherwise
     */
    boolean failOnNoMatch();

}
