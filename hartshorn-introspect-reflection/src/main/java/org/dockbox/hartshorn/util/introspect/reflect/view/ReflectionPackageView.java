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

package org.dockbox.hartshorn.util.introspect.reflect.view;

import java.lang.reflect.AnnotatedElement;

import org.dockbox.hartshorn.reporting.DiagnosticsPropertyCollector;
import org.dockbox.hartshorn.util.StringUtilities;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.view.PackageView;

public class ReflectionPackageView extends ReflectionAnnotatedElementView implements PackageView {

    private final Package pkg;

    public ReflectionPackageView(Introspector introspector, Package pkg) {
        super(introspector);
        this.pkg = pkg;
    }

    @Override
    public String name() {
        return this.pkg.getName();
    }

    @Override
    public String specificationTitle() {
        return StringUtilities.emptyIfNull(this.pkg.getSpecificationTitle());
    }

    @Override
    public String specificationVendor() {
        return StringUtilities.emptyIfNull(this.pkg.getSpecificationVendor());
    }

    @Override
    public String specificationVersion() {
        return StringUtilities.emptyIfNull(this.pkg.getSpecificationVersion());
    }

    @Override
    public String implementationTitle() {
        return StringUtilities.emptyIfNull(this.pkg.getImplementationTitle());
    }

    @Override
    public String implementationVendor() {
        return StringUtilities.emptyIfNull(this.pkg.getImplementationVendor());
    }

    @Override
    public String implementationVersion() {
        return StringUtilities.emptyIfNull(this.pkg.getImplementationVersion());
    }

    @Override
    public boolean isSealed() {
        return this.pkg.isSealed();
    }

    @Override
    public String qualifiedName() {
        return this.name();
    }

    @Override
    protected AnnotatedElement annotatedElement() {
        return this.pkg;
    }

    @Override
    public void report(DiagnosticsPropertyCollector collector) {
        collector.property("name").write(this.name());
        collector.property("specificationTitle").write(this.specificationTitle());
        collector.property("specificationVendor").write(this.specificationVendor());
        collector.property("specificationVersion").write(this.specificationVersion());
    }
}
