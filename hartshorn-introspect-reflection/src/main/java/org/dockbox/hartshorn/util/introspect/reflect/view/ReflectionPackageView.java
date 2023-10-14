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

package org.dockbox.hartshorn.util.introspect.reflect.view;

import org.dockbox.hartshorn.reporting.DiagnosticsPropertyCollector;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.view.IntrospectorAwareView;
import org.dockbox.hartshorn.util.introspect.view.PackageView;

public class ReflectionPackageView implements PackageView, IntrospectorAwareView {

    private final Introspector introspector;
    private final Package pkg;

    public ReflectionPackageView(final Introspector introspector, final Package pkg) {
        this.introspector = introspector;
        this.pkg = pkg;
    }

    @Override
    public String name() {
        return this.pkg.getName();
    }

    @Override
    public String qualifiedName() {
        return this.name();
    }

    @Override
    public Introspector introspector() {
        return this.introspector;
    }

    @Override
    public void report(final DiagnosticsPropertyCollector collector) {
        collector.property("name").write(this.name());
    }
}
