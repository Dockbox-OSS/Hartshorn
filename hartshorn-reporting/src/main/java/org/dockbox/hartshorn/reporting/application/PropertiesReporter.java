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

package org.dockbox.hartshorn.reporting.application;

import org.dockbox.hartshorn.reporting.DiagnosticsPropertyCollector;
import org.dockbox.hartshorn.reporting.Reportable;

import java.util.Map.Entry;
import java.util.Properties;

public class PropertiesReporter implements Reportable {

    private final Properties properties;

    public PropertiesReporter(final Properties properties) {
        this.properties = properties;
    }

    @Override
    public void report(final DiagnosticsPropertyCollector collector) {
        for (final Entry<Object, Object> entry : this.properties.entrySet()) {
            collector.property(String.valueOf(entry.getKey())).write(String.valueOf(entry.getValue()));
        }
    }
}
