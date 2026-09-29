/*
 * Copyright 2019-2026 the original author or authors.
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

package org.dockbox.hartshorn.hsl.compiler.runtime;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a method is not directly called through static Java references, but is dynamically invoked at runtime
 * via JVM bytecode generation (e.g., {@code invokestatic}, {@code invokevirtual}), dynamic call sites
 * ({@code invokedynamic}), or reflection.
 *
 * <p>This is primarily used internally to document the call paths originating from AST compilers, bootstrap methods,
 * and execution bridges. Internally, this annotation is not used to determine actual call paths, but rather serves
 * as a documentation tool for developers.
 *
 * @author Guus Lieben
 * @since 0.7.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DynamicInvocation {

    /**
     * Declares the internal compiler or runtime classes responsible for generating
     * or executing calls to this element (e.g., {@code VariableStatementCompiler.class},
     * {@code HslBootstrapMethods.class}).
     *
     * @return the array of internal caller classes
     */
    Class<?>[] callers() default {};

    /**
     * Optional contextual descriptions, bytecode instruction names, or specific
     * AST node references when a type reference alone is insufficient.
     *
     * @return contextual source descriptions
     */
    String[] sources() default {};
}
