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

package org.dockbox.hartshorn.hsl.interpreter;

import java.util.Map;
import java.util.Set;
import org.dockbox.hartshorn.hsl.objects.external.ExternalClass;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

public interface ExternalClassRegistry {

    Set<ExternalClass<?>> defineClasses(Map<String, TypeView<?>> imports);

    <T> ExternalClass<T> defineClass(TypeView<T> typeView);

    <T> ExternalClass<T> defineClass(TypeView<T> typeView, String as);

    boolean removeImported(String name);

    boolean removeImported(Class<?> type);

    boolean containsClassName(String name);

    Option<ExternalClass<?>> getByClassNameOrAlias(String name);

    Set<String> getNamesForClass(Class<?> type);

    Map<String, ExternalClass<?>> importedClasses();
}
