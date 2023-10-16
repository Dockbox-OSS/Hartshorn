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

package test.org.dockbox.hartshorn.lifecycle;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.application.lifecycle.LifecycleObserver;
import org.dockbox.hartshorn.component.Service;

@Service
public class TestLifecycleObserver implements LifecycleObserver {

    private boolean started;
    private boolean stopped;

    @Override
    public void onStarted(final ApplicationContext applicationContext) {
        this.started = true;
    }

    @Override
    public void onExit(final ApplicationContext applicationContext) {
        this.stopped = true;
    }

    public boolean started() {
        return this.started;
    }

    public boolean stopped() {
        return this.stopped;
    }
}
