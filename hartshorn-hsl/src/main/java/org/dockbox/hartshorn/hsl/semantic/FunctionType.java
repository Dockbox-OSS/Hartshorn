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

package org.dockbox.hartshorn.hsl.semantic;

import org.dockbox.hartshorn.hsl.ast.statement.ClassStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ConstructorStatement;
import org.dockbox.hartshorn.hsl.ast.statement.FunctionStatement;
import org.dockbox.hartshorn.hsl.ast.statement.TestStatement;

/**
 * The type of the function that is currently being resolved. This is used to determine whether
 * certain operations are allowed or not.
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public enum FunctionType {
    /**
     * No function type, indicating that we are resolving outside the scope of any function.
     */
    NONE,
    /**
     * An inline function, which is a function that is not attached to a class (method). Usually
     * linked only to {@link FunctionStatement function statements}.
     */
    INLINE_FUNCTION,
    /**
     * A function that is attached to a class. Usually linked to {@link FunctionStatement functions}
     * encountered while visiting a {@link ClassStatement}.
     */
    CLASS_FUNCTION,
    /**
     * A constructor, which is a method that is used to create a new instance of a class. Usually
     * linked to {@link ConstructorStatement constructor} encountered while visiting a
     * {@link ClassStatement}.
     */
    INITIALIZER,
    /**
     * A test function, which is an inline function-like assertion. Usually linked to
     * {@link TestStatement test statements}.
     */
    TEST,
    /**
     * A field member, which is a function-like expression used to assign get/set statements to
     * field in a class. Usually linked to {@link ClassStatement class statements} that have field
     * members.
     */
    FIELD_MEMBER,
}
