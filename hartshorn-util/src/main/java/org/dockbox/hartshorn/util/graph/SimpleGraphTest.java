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

package org.dockbox.hartshorn.util.graph;

import java.util.List;
import java.util.Set;

@SuppressWarnings("UseOfSystemOutOrSystemErr")
public class SimpleGraphTest {

    public static void main(String[] args) throws GraphException {
        testGraphsWithDefinedRoots();
//        testGraphsWithUndefinedRoots();
    }

    /**
     * <img src="https://static.javatpoint.com/ds/images/tree-vs-graph-data-structure3.png"/>
     */
    private static void testGraphsWithUndefinedRoots() throws GraphException {
        GraphIterator<String> integerVisitor = new PrintGraphVisitor<>();

        System.out.println("Unknown roots:");
        Graph<String> unknownRootsGraph = createUnknownRootsGraph();
        integerVisitor.iterate(unknownRootsGraph);
    }

    /**
     * <img src="https://techdifferences.com/wp-content/uploads/2018/03/Untitled-1.jpg" />
     */
    private static void testGraphsWithDefinedRoots() throws GraphException {
        GraphIterator<String> visitor = new PrintGraphVisitor<>();

        // BFS: A -> BC -> D -> EH -> F -> G
        // DFS: AC -> BDH -> EFG
        System.out.println("Graph:");
        Graph<String> graph = createGraph();
        visitor.iterate(graph);

        // A -> BC -> DEF -> GH
        // DFS: ABD -> EG -> H -> CF
        System.out.println("Tree:");
        Graph<String> tree = createTree();
        visitor.iterate(tree);
    }

    private static Graph<String> createGraph() {
        Graph<String> graph = new SimpleGraph<>();

        MutableContainableGraphNode<String> nodeA = new SimpleGraphNode<>("A");
        MutableContainableGraphNode<String> nodeB = new SimpleGraphNode<>("B");
        MutableContainableGraphNode<String> nodeC = new SimpleGraphNode<>("C");
        MutableContainableGraphNode<String> nodeD = new SimpleGraphNode<>("D");
        MutableContainableGraphNode<String> nodeE = new SimpleGraphNode<>("E");
        MutableContainableGraphNode<String> nodeF = new SimpleGraphNode<>("F");
        MutableContainableGraphNode<String> nodeG = new SimpleGraphNode<>("G");
        MutableContainableGraphNode<String> nodeH = new SimpleGraphNode<>("H");

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
        Graph<String> graph = new SimpleGraph<>();
        MutableContainableGraphNode<String> nodeA = new SimpleGraphNode<>("A");
        MutableContainableGraphNode<String> nodeB = new SimpleGraphNode<>("B");
        MutableContainableGraphNode<String> nodeC = new SimpleGraphNode<>("C");
        MutableContainableGraphNode<String> nodeD = new SimpleGraphNode<>("D");
        MutableContainableGraphNode<String> nodeE = new SimpleGraphNode<>("E");
        MutableContainableGraphNode<String> nodeF = new SimpleGraphNode<>("F");
        MutableContainableGraphNode<String> nodeG = new SimpleGraphNode<>("G");
        MutableContainableGraphNode<String> nodeH = new SimpleGraphNode<>("H");

        nodeA.addChildren(List.of(nodeB, nodeC));
        nodeB.addChildren(List.of(nodeD, nodeE));
        nodeE.addChildren(List.of(nodeG, nodeH));
        nodeC.addChild(nodeF);

        graph.addRoot(nodeA);

        return graph;
    }

    private static Graph<String> createUnknownRootsGraph() {
        Graph<String> graph = new SimpleGraph<>();
        MutableContainableGraphNode<String> nodeA = new SimpleGraphNode<>("A");
        MutableContainableGraphNode<String> nodeB = new SimpleGraphNode<>("B");
        MutableContainableGraphNode<String> nodeC = new SimpleGraphNode<>("C");
        MutableContainableGraphNode<String> nodeD = new SimpleGraphNode<>("D");
        MutableContainableGraphNode<String> nodeE = new SimpleGraphNode<>("E");
        MutableContainableGraphNode<String> nodeF = new SimpleGraphNode<>("F");
        MutableContainableGraphNode<String> nodeG = new SimpleGraphNode<>("G");

        nodeA.addChildren(List.of(nodeE, nodeF, nodeB));
        nodeB.addChild(nodeC);
        nodeD.addChild(nodeC);
        nodeE.addChildren(List.of(nodeD, nodeG));
        nodeF.addChild(nodeC);
        nodeG.addChild(nodeC);

        graph.addRoots(Set.of(
                nodeA,
                nodeB,
                nodeC,
                nodeD,
                nodeE,
                nodeF,
                nodeG
        ));

        return graph;
    }

    private static class PrintGraphVisitor<T> extends DepthFirstGraphVisitor<T> {

        @Override
        protected boolean visit(GraphNode<T> node) {
            System.out.print(node.value() + " ");
            return true;
        }

        @Override
        protected void afterPathVisited() {
            System.out.println();
        }
    }
}
