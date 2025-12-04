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

/**
 * The type of the class that is currently being resolved. This is used to determine
 * whether certain operations are allowed or not.
 *
 * @author Guus Lieben
 * @since 0.4.12
 */
public enum ClassType {
    /**
     * No class type, indicating that we are resolving outside the scope of any class.
     */
    NONE,
    /**
     * A single class, which is a class that does not extend another class.
     */
    CLASS,
    /**
     * A subclass, which is a class that extends another class, indicated by the presence
     * of a {@link ClassStatement#superClass() superclass expression}.
     */
    SUBCLASS,
}
