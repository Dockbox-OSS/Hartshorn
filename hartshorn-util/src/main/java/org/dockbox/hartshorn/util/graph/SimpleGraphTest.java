package org.dockbox.hartshorn.util.graph;

import java.util.List;
import java.util.Set;

@SuppressWarnings("UseOfSystemOutOrSystemErr")
public class SimpleGraphTest {

    public static void main(final String[] args) {
        testGraphsWithDefinedRoots();
//        testGraphsWithUndefinedRoots();
    }

    /**
     * <img src="https://static.javatpoint.com/ds/images/tree-vs-graph-data-structure3.png"/>
     */
    private static void testGraphsWithUndefinedRoots() {
        final GraphIterator<Integer> integerVisitor = new PrintGraphVisitor<>();

        System.out.println("Unknown roots:");
        final Graph<Integer> unknownRootsGraph = createUnknownRootsGraph();
        integerVisitor.iterate(unknownRootsGraph);
    }

    /**
     * <img src="https://techdifferences.com/wp-content/uploads/2018/03/Untitled-1.jpg" />
     */
    private static void testGraphsWithDefinedRoots() {
        final GraphIterator<String> visitor = new PrintGraphVisitor<>();

        // BFS: A -> BC -> D -> EH -> F -> G
        // DFS: AC -> BDEFHG
        System.out.println("Graph:");
        final Graph<String> graph = createGraph();
        visitor.iterate(graph);

        // A -> BC -> DEF -> GH
        // DFS: ABD -> EG -> H -> CF
        System.out.println("Tree:");
        final Graph<String> tree = createTree();
        visitor.iterate(tree);
    }

    private static Graph<String> createGraph() {
        final Graph<String> graph = new SimpleGraph<>();

        final GraphNode<String> nodeA = new SimpleGraphNode<>("A");
        final GraphNode<String> nodeB = new SimpleGraphNode<>("B");
        final GraphNode<String> nodeC = new SimpleGraphNode<>("C");
        final GraphNode<String> nodeD = new SimpleGraphNode<>("D");
        final GraphNode<String> nodeE = new SimpleGraphNode<>("E");
        final GraphNode<String> nodeF = new SimpleGraphNode<>("F");
        final GraphNode<String> nodeG = new SimpleGraphNode<>("G");
        final GraphNode<String> nodeH = new SimpleGraphNode<>("H");

        nodeA.addChildren(List.of(nodeB, nodeC));
        nodeB.addChild(nodeD);
        nodeD.addChildren(List.of(nodeE, nodeH));
        nodeE.addChild(nodeF);
        nodeH.addChild(nodeG);
        nodeF.addChild(nodeG);

        graph.addRoot(nodeA);
        return graph;
    }

    private static Graph<String> createTree() {
        final Graph<String> graph = new SimpleGraph<>();
        final GraphNode<String> nodeA = new SimpleGraphNode<>("A");
        final GraphNode<String> nodeB = new SimpleGraphNode<>("B");
        final GraphNode<String> nodeC = new SimpleGraphNode<>("C");
        final GraphNode<String> nodeD = new SimpleGraphNode<>("D");
        final GraphNode<String> nodeE = new SimpleGraphNode<>("E");
        final GraphNode<String> nodeF = new SimpleGraphNode<>("F");
        final GraphNode<String> nodeG = new SimpleGraphNode<>("G");
        final GraphNode<String> nodeH = new SimpleGraphNode<>("H");

        nodeA.addChildren(List.of(nodeB, nodeC));
        nodeB.addChildren(List.of(nodeD, nodeE));
        nodeE.addChildren(List.of(nodeG, nodeH));
        nodeC.addChild(nodeF);

        graph.addRoot(nodeA);

        return graph;
    }

    private static Graph<Integer> createUnknownRootsGraph() {
        final Graph<Integer> graph = new SimpleGraph<>();
        final GraphNode<Integer> node1 = new SimpleGraphNode<>(1);
        final GraphNode<Integer> node2 = new SimpleGraphNode<>(2);
        final GraphNode<Integer> node3 = new SimpleGraphNode<>(3);
        final GraphNode<Integer> node4 = new SimpleGraphNode<>(4);
        final GraphNode<Integer> node5 = new SimpleGraphNode<>(5);
        final GraphNode<Integer> node6 = new SimpleGraphNode<>(6);
        final GraphNode<Integer> node7 = new SimpleGraphNode<>(7);

        node1.addChildren(List.of(node5, node6, node2));
        node2.addChild(node3);
        node4.addChild(node3);
        node5.addChildren(List.of(node4, node7));
        node6.addChild(node3);
        node7.addChild(node3);

        graph.addRoots(Set.of(
                node1,
                node2,
                node3,
                node4,
                node5,
                node6,
                node7
        ));

        return graph;
    }

    private static class PrintGraphVisitor<T> extends DepthFirstGraphVisitor<T> {

        @Override
        protected boolean visit(final GraphNode<T> node) {
            System.out.print(node.value() + " ");
            return true;
        }

        @Override
        protected void afterPathVisited() {
            System.out.println();
        }
    }
}
