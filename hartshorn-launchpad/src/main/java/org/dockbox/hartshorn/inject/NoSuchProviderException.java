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

package org.dockbox.hartshorn.inject;

import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.provider.NonTypeAwareProvider;
import org.dockbox.hartshorn.inject.provider.Provider;
import org.dockbox.hartshorn.inject.provider.TypeAwareProvider;
import org.dockbox.hartshorn.util.ApplicationException;

/**
 * Thrown when a provider cannot be found for a given component key.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class NoSuchProviderException extends ApplicationException {

    /**
     * The type of provider that was requested.
     *
     * @since 0.5.0
     *
     * @author Guus Lieben
     */
    public enum ProviderType {

        /**
         * A provider that is aware of the type it provides.
         * @see TypeAwareProvider
         */
        TYPE_AWARE,

        /**
         * A provider that is not aware of the type it provides.
         * @see NonTypeAwareProvider
         */
        NON_TYPE_AWARE,

        /**
         * Any provider, whether it is aware of the type it provides or not.
         * @see Provider
         */
        ANY,
    }

    public NoSuchProviderException(ComponentKey<?> componentKey) {
        this(ProviderType.ANY, componentKey);
    }

    public NoSuchProviderException(ProviderType providerType, ComponentKey<?> componentKey) {
        super("No %s found for component key '%s'".formatted(
                switch(providerType) {
                    case TYPE_AWARE -> "type-aware provider";
                    case NON_TYPE_AWARE -> "non type-aware provider";
                    case ANY -> "provider";
                },
                componentKey));
    }
}
