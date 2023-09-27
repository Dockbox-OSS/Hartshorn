/*
 * Copyright 2019-2023 the original author or authors.
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

import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.util.ApplicationException;

public class NoSuchProviderException extends ApplicationException {

    public enum ProviderType {
        TYPE_AWARE,
        NON_TYPE_AWARE,
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
