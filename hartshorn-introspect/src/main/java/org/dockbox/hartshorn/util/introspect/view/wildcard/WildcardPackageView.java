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

package org.dockbox.hartshorn.util.introspect.view.wildcard;

import org.dockbox.hartshorn.context.DefaultContext;
import org.dockbox.hartshorn.reporting.DiagnosticsPropertyCollector;
import org.dockbox.hartshorn.util.introspect.ElementAnnotationsIntrospector;
import org.dockbox.hartshorn.util.introspect.view.EnclosableView;
import org.dockbox.hartshorn.util.introspect.view.PackageView;
import org.dockbox.hartshorn.util.option.Option;

/**
 * A {@link PackageView} implementation for wildcard types.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class WildcardPackageView extends DefaultContext implements PackageView {

    @Override
    public String name() {
        return "";
    }

    @Override
    public void report(DiagnosticsPropertyCollector collector) {
        // No-op, only report in WildcardTypeView
    }

    @Override
    public String qualifiedName() {
        return "";
    }

    @Override
    public ElementAnnotationsIntrospector annotations() {
        return new WildcardElementAnnotationsIntrospector();
    }

    @Override
    public String specificationTitle() {
        return "";
    }

    @Override
    public String specificationVendor() {
        return "";
    }

    @Override
    public String specificationVersion() {
        return "";
    }

    @Override
    public String implementationTitle() {
        return "";
    }

    @Override
    public String implementationVendor() {
        return "";
    }

    @Override
    public String implementationVersion() {
        return "";
    }

    @Override
    public boolean isSealed() {
        return false;
    }

    @Override
    public boolean isEnclosed() {
        return false;
    }

    @Override
    public Option<EnclosableView> enclosingView() {
        return Option.empty();
    }
}
