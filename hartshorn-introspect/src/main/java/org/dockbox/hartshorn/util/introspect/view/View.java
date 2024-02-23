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

package org.dockbox.hartshorn.util.introspect.view;

import org.dockbox.hartshorn.context.Context;
import org.dockbox.hartshorn.reporting.Reportable;
import org.dockbox.hartshorn.util.Named;

/**
 * A view is a representation of an element in the Java language. Exact details of the element
 * are implementation specific, but all views provide access to the element's name, and can
 * be reported to extract information about the element. Views are also {@link Context}s, which
 * allows for the element to be enhanced with custom data if required.
 *
 * @since 0.4.13
 *
 * @author Guus Lieben
 */
public interface View extends Named, Reportable, Context {

    /**
     * Returns the qualified name of the element. For example, if the element is a field,
     * this method will return the field's name, qualified by the declaring class.
     *
     * @return the qualified name of the element
     */
    String qualifiedName();

}
