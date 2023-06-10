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

package org.dockbox.hartshorn.component.factory;

import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.util.ApplicationRuntimeException;
import org.dockbox.hartshorn.util.introspect.view.ExecutableElementView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

/**
 * @deprecated See {@link Factory}.
 */
@Deprecated(since = "23.1", forRemoval = true)
public class MissingFactoryConstructorException extends ApplicationRuntimeException {

    public MissingFactoryConstructorException(final ComponentKey<?> key, final ExecutableElementView<?> elementView) {
        super("No matching bound constructor found for %s with parameters: %s"
                .formatted(key, elementView.parameters().types().stream().map(TypeView::name).toList()));
    }
}
