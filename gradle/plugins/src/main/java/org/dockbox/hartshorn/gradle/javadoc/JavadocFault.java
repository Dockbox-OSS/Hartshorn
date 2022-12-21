/*
 * Copyright 2019-2022 the original author or authors.
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

package org.dockbox.hartshorn.gradle.javadoc;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;

public class JavadocFault {

    private final Node node;
    private final String name;
    private final String message;

    public JavadocFault(final Node node, final String name, final String message) {
        this.node = node;
        this.name = name;
        this.message = message;
    }

    public Node node() {
        return this.node;
    }

    public String name() {
        return this.name;
    }

    public String message() {
        return this.message;
    }

    public CompilationUnit compilationUnit() {
        return this.node().findCompilationUnit().orElseThrow();
    }
}
