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

package org.dockbox.hartshorn.launchpad.environment;

/**
 * An application component that is directly bound to an active {@link ApplicationEnvironment}. This is respected
 * by the {@link ApplicationEnvironment}, which will set itself as the component's environment when the component
 * is used by the environment.
 *
 * @see ApplicationEnvironment
 *
 * @since 0.4.8
 *
 * @author Guus Lieben
 */
public interface ApplicationManaged {

    /**
     * @return the {@link ApplicationEnvironment} that is managing this component.
     */
    ApplicationEnvironment environment();

    /**
     * Sets the {@link ApplicationEnvironment} that is managing this component.
     * @param environment the {@link ApplicationEnvironment} that is managing this component.
     */
    void environment(ApplicationEnvironment environment);
}
