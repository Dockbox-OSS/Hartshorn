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

package org.dockbox.hartshorn.util.introspect;

/**
 * An interface that defines the environment in which introspection is performed. This is used to
 * determine whether certain information is available, such as parameter names.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public interface IntrospectionEnvironment {

    /**
     * Returns whether parameter names are available in the current environment.
     *
     * @return {@code true} if parameter names are available, {@code false} otherwise
     *
     * @see <a href="https://docs.oracle.com/javase%2Ftutorial%2F/reflect/member/methodparameterreflection.html">Obtaining Names of Method Parameters</a>
     */
    boolean parameterNamesAvailable();

}
