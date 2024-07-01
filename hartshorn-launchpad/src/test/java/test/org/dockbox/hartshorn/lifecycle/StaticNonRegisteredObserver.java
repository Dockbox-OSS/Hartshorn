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

package test.org.dockbox.hartshorn.lifecycle;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.application.lifecycle.LifecycleObserver;

public class StaticNonRegisteredObserver implements LifecycleObserver {

    private static boolean started;
    private static boolean stopped;

    @Override
    public void onStarted(ApplicationContext applicationContext) {
        started = true;
    }

    @Override
    public void onExit(ApplicationContext applicationContext) {
        stopped = true;
    }

    public static boolean started() {
        return started;
    }

    public static boolean stopped() {
        return stopped;
    }

}
