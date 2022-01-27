/*
 * Copyright 2019-2022 the original author or authors.
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

package org.dockbox.hartshorn.core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Simple implementation of {@link TypedOwner}
 */
@Getter
@AllArgsConstructor
public final class TypedOwnerImpl implements TypedOwner {

    private final String id;

    /**
     * Creates a new {@link TypedOwnerImpl} from the given ID.
     *
     * @param id The ID of the owner
     *
     * @return The new {@link TypedOwner}
     */
    public static TypedOwner of(final String id) {
        return new TypedOwnerImpl(id);
    }

}
