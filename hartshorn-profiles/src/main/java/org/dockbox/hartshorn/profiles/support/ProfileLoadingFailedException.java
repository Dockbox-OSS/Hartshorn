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

package org.dockbox.hartshorn.profiles.support;

import org.dockbox.hartshorn.util.ApplicationRuntimeException;

import java.net.URI;

/**
 * Exception thrown when a profile could not be loaded from a given resource. This exception
 * typically indicates issues such as missing files, inaccessible resources, or parsing errors
 * during profile loading.
 *
 * @author Guus Lieben
 * @since 0.7.0
 */
public class ProfileLoadingFailedException extends ApplicationRuntimeException {
    public ProfileLoadingFailedException(String profile, URI resource, Throwable cause) {
        super("Profile with name '"
            + profile
            + "' could not be loaded from resource '"
            + resource
            + "'", cause);
    }
}
