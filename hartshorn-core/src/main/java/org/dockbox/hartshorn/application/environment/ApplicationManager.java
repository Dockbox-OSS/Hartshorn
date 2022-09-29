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

package org.dockbox.hartshorn.application.environment;

import org.dockbox.hartshorn.application.ExceptionHandler;
import org.dockbox.hartshorn.application.lifecycle.LifecycleObservable;
import org.dockbox.hartshorn.context.ContextCarrier;
import org.dockbox.hartshorn.logging.ApplicationLogger;
import org.dockbox.hartshorn.proxy.ApplicationProxier;
import org.dockbox.hartshorn.util.introspect.annotations.AnnotationLookup;

/**
 * The {@link ApplicationManager} is responsible for managing the lifecycle of the application. It combines the
 * most important parts of the application:
 * <ul>
 *     <li>The {@link ApplicationLogger}</li>
 *     <li>The {@link ApplicationProxier}</li>
 *     <li>The {@link LifecycleObservable}</li>
 *     <li>The {@link ApplicationFSProvider}</li>
 *     <li>The {@link ExceptionHandler}</li>
 * </ul>
 *
 * <p>Additionally, the manager is capable of indicating whether an application is active in a CI environment or not.
 *
 * @author Guus Lieben
 * @since 21.9
 */
public interface ApplicationManager extends ContextCarrier, ApplicationLogger, ApplicationProxier, LifecycleObservable, ApplicationFSProvider, ExceptionHandler {

    /**
     * Indicates whether the application is active in a CI environment or not. This can be determined by the presence of
     * environment variables, or other means.
     * @return {@literal true} if the application is active in a CI environment, {@literal false} otherwise.
     * @see ApplicationEnvironment#isCI()
     */
    boolean isCI();

    AnnotationLookup annotationLookup();
}
