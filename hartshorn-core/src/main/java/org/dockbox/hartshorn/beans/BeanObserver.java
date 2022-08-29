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

package org.dockbox.hartshorn.beans;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.application.lifecycle.Observer;

public interface BeanObserver extends Observer {

    /**
     * Called when the application is done collecting static beans. This is called directly after the
     * {@link BeanContext} has been created and configured.
     *
     * @param applicationContext The application context
     * @param beanContext The bean context
     */
    void onBeansCollected(ApplicationContext applicationContext, BeanContext beanContext);

}
