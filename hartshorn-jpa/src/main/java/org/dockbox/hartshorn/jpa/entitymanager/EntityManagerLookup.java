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

package org.dockbox.hartshorn.jpa.entitymanager;

import org.dockbox.hartshorn.util.option.Option;

import jakarta.persistence.EntityManager;

/**
 * Utility class for looking up the entity manager for a given instance. This is
 * used to manage the lifecycle of the entity manager, even if it is not directly
 * injected into the instance. This is useful for situations where the entity manager
 * is attached as a context attribute, or is otherwise managed by the application.
 */
public interface EntityManagerLookup {

    /**
     * Lookup the entity manager for the given instance.
     * @param target the instance to look up the entity manager for
     * @return the entity manager, or {@link Option#empty()} if no entity manager could be found
     */
    Option<EntityManager> lookup(Object target);
}
