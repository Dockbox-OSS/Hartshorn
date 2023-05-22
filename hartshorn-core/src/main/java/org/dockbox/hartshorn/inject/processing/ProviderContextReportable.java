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

package org.dockbox.hartshorn.inject.processing;

import org.dockbox.hartshorn.reporting.DiagnosticsPropertyCollector;
import org.dockbox.hartshorn.reporting.Reportable;
import org.dockbox.hartshorn.util.StringUtilities;

public class ProviderContextReportable implements Reportable {
    private final ProviderContext context;

    public ProviderContextReportable(final ProviderContext context) {
        this.context = context;
    }

    @Override
    public void report(final DiagnosticsPropertyCollector elementCollector) {
        elementCollector.property("key").write(keyCollector -> {
            keyCollector.property("type").write(this.context.key().type().getCanonicalName());
            if (this.context.key().name() != null) {
                keyCollector.property("name").write(this.context.key().name());
            }
        });
        elementCollector.property("phase").write(this.context.provider().phase());
        elementCollector.property("lazy").write(this.context.provider().lazy());
        elementCollector.property("priority").write(this.context.provider().priority());
        if (StringUtilities.notEmpty(this.context.provider().value())) {
            elementCollector.property("name").write(this.context.provider().value());
        }
        elementCollector.property("element").write(this.context.element());
    }
}
