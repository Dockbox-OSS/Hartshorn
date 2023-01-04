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

package org.dockbox.hartshorn.jpa.entitymanager;

import org.dockbox.hartshorn.context.DefaultContext;
import org.dockbox.hartshorn.reporting.DiagnosticsPropertyCollector;
import org.dockbox.hartshorn.reporting.Reportable;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.util.Collection;

public class EntityContext extends DefaultContext implements Reportable {

    private final Collection<TypeView<?>> entities;

    public EntityContext(final Collection<TypeView<?>> entities) {
        this.entities = entities;
    }

    public Collection<TypeView<?>> entities() {
        return this.entities;
    }

    @Override
    public void report(final DiagnosticsPropertyCollector collector) {
        collector.property("entities").write(this.entities.stream()
                .map(TypeView::qualifiedName)
                .toArray(String[]::new)
        );
    }
}
