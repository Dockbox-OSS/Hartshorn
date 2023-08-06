package org.dockbox.hartshorn.util.introspect.reflect.view;

import org.dockbox.hartshorn.util.graph.ContainableGraphNode;
import org.dockbox.hartshorn.util.graph.MutableContainableGraphNode;
import org.dockbox.hartshorn.util.graph.SimpleGraph;
import org.dockbox.hartshorn.util.graph.SimpleGraphNode;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.util.List;

public class TypeHierarchyGraph extends SimpleGraph<TypeView<?>> {

    public static TypeHierarchyGraph of(final TypeView<?> type) {
        final TypeHierarchyGraph graph = new TypeHierarchyGraph();
        graph.addRoot(createNode(type));
        return graph;
    }

    private static ContainableGraphNode<TypeView<?>> createNode(final TypeView<?> type) {
        final MutableContainableGraphNode<TypeView<?>> node = new SimpleGraphNode<>(type);
        final List<TypeView<?>> interfaces = type.genericInterfaces();
        for (final TypeView<?> anInterface : interfaces) {
            node.addChild(createNode(anInterface));
        }
        final TypeView<?> superClass = type.genericSuperClass();
        if (!superClass.isVoid()) {
            node.addChild(createNode(superClass));
        }
        return node;
    }
}
