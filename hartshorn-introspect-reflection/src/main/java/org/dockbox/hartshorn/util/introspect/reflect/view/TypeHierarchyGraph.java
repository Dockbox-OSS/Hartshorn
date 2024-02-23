/*
 * Copyright 2019-2024 the original author or authors.
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

package org.dockbox.hartshorn.util.introspect.reflect.view;

import java.util.List;

import org.dockbox.hartshorn.util.graph.ContainableGraphNode;
import org.dockbox.hartshorn.util.graph.MutableContainableGraphNode;
import org.dockbox.hartshorn.util.graph.SimpleGraph;
import org.dockbox.hartshorn.util.graph.SimpleGraphNode;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

/**
 * A graph representation of a type hierarchy. The graph is created by traversing the type hierarchy
 * of a given type, and adding each type as a node.
 *
 * <p>Given the following type hierarchy:
 * <pre>
 * interface A {}
 * interface B {}
 * interface C {}
 * interface D extends A, B {}
 * interface E extends D, C {}
 * </pre>
 *
 * <p>The resulting graph would be:
 * <pre>
 * E
 * |\
 * D C
 * |\
 * A B
 * </pre>
 *
 * @since 0.5.0
 *
 * @see TypeView
 *
 * @author Guus Lieben
 */
public class TypeHierarchyGraph extends SimpleGraph<TypeView<?>> {

    /**
     * Creates a new graph for the given type. If the type has no super class or interfaces, the graph will
     * only contain a single node.
     *
     * @param type the type to create a graph for
     * @return the graph
     */
    public static TypeHierarchyGraph of(TypeView<?> type) {
        TypeHierarchyGraph graph = new TypeHierarchyGraph();
        graph.addRoot(createNode(type));
        return graph;
    }

    private static ContainableGraphNode<TypeView<?>> createNode(TypeView<?> type) {
        MutableContainableGraphNode<TypeView<?>> node = new SimpleGraphNode<>(type);
        List<TypeView<?>> interfaces = type.genericInterfaces();
        for (TypeView<?> anInterface : interfaces) {
            node.addChild(createNode(anInterface));
        }
        TypeView<?> superClass = type.genericSuperClass();
        if (!superClass.isVoid()) {
            node.addChild(createNode(superClass));
        }
        return node;
    }
}
