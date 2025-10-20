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

import org.dockbox.hartshorn.hsl.objects.external.ExternalClass;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

import java.util.Map;
import java.util.Set;

/**
 * A registry for external classes that can be used in HSL scripts. External classes are classes that are not
 * defined in the script itself, but are imported from outside the script runtime. This registry allows for
 * defining, removing, and querying external classes by their name or alias.
 *
 * <p>Each external class can have multiple names or aliases associated with it, allowing for flexible referencing
 * within scripts. The registry ensures that class names and aliases are unique to prevent conflicts. Similarly,
 * this registry ensures that {@link ExternalClass} instances are re-used for the same alias or class name.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public interface ExternalClassRegistry {

    /**
     * Defines multiple external classes based on the provided map of imports.
     *
     * @param imports a map defining the classes to import.
     * @return a set of defined external classes.
     */
    Set<ExternalClass<?>> defineClasses(Map<String, TypeView<?>> imports);

    /**
     * Defines a single external class based on the provided type view. The class will be exposed
     * under its direct name. For example, a class named {@code com.example.MyClass} will be accessible
     * in scripts as {@code MyClass}.
     *
     * @param typeView the type view representing the class to define.
     * @return the defined external class.
     * @param <T> the type of the class being defined.
     */
    <T> ExternalClass<T> defineClass(TypeView<T> typeView);

    /**
     * Defines a single external class based on the provided type view and exposes it under a custom alias.
     *
     * @param typeView the type view representing the class to define.
     * @param as the alias under which the class will be accessible in scripts.
     * @return the defined external class.
     * @param <T> the type of the class being defined.
     */
    <T> ExternalClass<T> defineClass(TypeView<T> typeView, String as);

    /**
     * Removes an imported external class by its name or alias.
     *
     * @param name the name or alias of the class to remove.
     * @return true if the class was found and removed, false otherwise.
     */
    boolean removeImported(String name);

    /**
     * Removes an imported external class by its type. This will remove all aliases associated with
     * the class as well.
     *
     * @param type the type of the class to remove.
     * @return true if the class was found and removed, false otherwise.
     */
    boolean removeImported(Class<?> type);

    /**
     * Checks if a class with the given name or alias is already imported.
     *
     * @param name the name or alias to check.
     * @return true if a class with the given name or alias is imported, false otherwise.
     */
    boolean containsClassName(String name);

    /**
     * Retrieves an imported external class by its name or alias.
     *
     * @param name the name or alias of the class to retrieve
     * @return an option containing the external class if found, or empty if not found
     */
    Option<ExternalClass<?>> getByClassNameOrAlias(String name);

    /**
     * Retrieves all names and aliases associated with the given class type.
     *
     * @param type the class type to look up
     * @return a set of names and aliases associated with the class type
     */
    Set<String> getNamesForClass(Class<?> type);

    /**
     * Retrieves a map of all imported classes, where the keys are the names or aliases
     * and the values are the corresponding {@link ExternalClass} instances.
     *
     * @return a map of all imported classes
     */
    Map<String, ExternalClass<?>> importedClasses();
}
