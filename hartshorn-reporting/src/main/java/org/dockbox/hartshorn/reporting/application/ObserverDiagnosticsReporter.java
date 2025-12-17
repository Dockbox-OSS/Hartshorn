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

package org.dockbox.hartshorn.reporting.application;

import org.dockbox.hartshorn.launchpad.lifecycle.Observer;
import org.dockbox.hartshorn.reporting.DiagnosticsPropertyCollector;
import org.dockbox.hartshorn.reporting.Reportable;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

public class ObserverDiagnosticsReporter implements Reportable {

    private final Introspector introspector;
    private final Observer observer;

    public ObserverDiagnosticsReporter(Introspector introspector, Observer observer) {
        this.introspector = introspector;
        this.observer = observer;
    }

    @Override
    public void report(DiagnosticsPropertyCollector collector) {
        TypeView<Observer> type = this.introspector.introspect(this.observer);
        collector.property("type").writeDelegate(type);
        collector.property("priority").writeInt(this.observer.priority());

        if (observer instanceof Reportable reportable) {
            // Delegate further reporting to the observer itself. Allowed to override
            // the default reporting.
            reportable.report(collector);
        }
    }
}
