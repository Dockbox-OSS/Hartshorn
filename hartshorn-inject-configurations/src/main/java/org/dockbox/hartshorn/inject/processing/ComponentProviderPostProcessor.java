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

package org.dockbox.hartshorn.inject.processing;

import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.ComponentRequestContext;
import org.dockbox.hartshorn.inject.provider.ComponentProvider;
import org.dockbox.hartshorn.inject.provider.ObjectContainer;
import org.dockbox.hartshorn.util.ApplicationException;

/**
 * A post-processor which is invoked after a component instance has been created, but before it is returned to
 * the caller. This is typically used to bridge {@link ComponentPostProcessor component post-processors} to a
 * {@link ComponentProvider component provider}, allowing for additional processing of the component instance
 * before it is returned.
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public interface ComponentProviderPostProcessor {

    /**
     * Processes the given component instance, allowing for additional processing before it is returned to the
     * caller.
     *
     * @param componentKey the key of the component being processed, which may be used to identify the component
     * @param objectContainer the container in which the component instance is stored, allowing for retrieval of the instance
     * @param requestContext the context of the request, which may contain additional information about the request
     *
     * @param <T> the type of the component being processed
     *
     * @return the processed component instance, which may be the same as the original instance or a new instance
     *
     * @throws ApplicationException if an error occurs during processing, allowing for handling of the error by the caller
     */
    <T> T processInstance(
            ComponentKey<T> componentKey,
            ObjectContainer<T> objectContainer,
            ComponentRequestContext requestContext
    ) throws ApplicationException;
}
