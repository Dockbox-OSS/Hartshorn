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

import org.dockbox.hartshorn.inject.InjectionCapableApplication;

/**
 * A component processor that allows invoking a method when processing of managed components has
 * completed, and the application is about to release.
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 *
 * @deprecated Due to the limitation of this interface only applying to managed components, and its
 * inherent coupling to the application lifecycle, this interface is deprecated and will be removed in
 * a future version.
 */
@Deprecated(forRemoval = true, since = "0.7.0")
public interface ExitingComponentProcessor {
    void exit(InjectionCapableApplication application);
}
