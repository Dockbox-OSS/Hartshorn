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

package org.dockbox.hartshorn.hsl.token;

import java.util.Collection;
import java.util.Objects;

import org.dockbox.hartshorn.hsl.token.TokenGraph.TokenNode;
import org.dockbox.hartshorn.hsl.token.type.TokenType;
import org.dockbox.hartshorn.util.graph.GraphNode;
import org.dockbox.hartshorn.util.graph.MutableGraphNode;
import org.dockbox.hartshorn.util.graph.SimpleGraph;
import org.dockbox.hartshorn.util.graph.SimpleGraphNode;

/**
 * Represents a graph of tokens, where each node is a character in a token and each edge is a transition from one character to
 * another. The graph is used to determine the {@link TokenType type of a token} based on its {@link TokenCharacter characters}.
 * The graph is usually built from a {@link TokenRegistry}, which contains all the token types and their characters.
 *
 * <p>Token graphs may be used by {@link org.dockbox.hartshorn.hsl.lexer.Lexer lexers} to dynamically tokenize input strings
 * without making any assumptions about the token types or their characters.
 *
 * <p>Token graphs are directed graphs, where each node has at most one parent and at most one child. The graph may have
 * multiple roots, which represent the starting characters of tokens. A basic example of a token graph is the following:
 *
 * <pre>{@code
 * PLUS (+)
 * |- PLUS_PLUS (++)
 * |- PLUS_EQUALS (+=)
 * }</pre>
 *
 * <p>Here, the root node is the {@code PLUS} node, which has two children: {@code PLUS_PLUS} and {@code PLUS_EQUALS}. The root
 * node {@code PLUS} is identified by the character {@code +}, and its children are identified by the root character, combined
 * with the child character. For example {@code PLUS_EQUALS} consists of the characters {@code +} (from {@code PLUS} and {@code =}
 * (from {@code PLUS_EQUALS}).
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public class TokenGraph extends SimpleGraph<TokenNode> {

    /**
     * Generates a token graph from the given token registry. The graph is built by visiting each token type and its characters,
     * and adding them to the graph. The graph is built by adding a root node for each character, and then adding children for
     * each subsequent character.
     *
     * @param tokenRegistry the token registry to build the graph from
     * @return the token graph
     */
    public static TokenGraph of(TokenRegistry tokenRegistry) {
        TokenGraph graph = new TokenGraph();
        for (TokenType tokenType : tokenRegistry.tokenTypes()) {
            TokenCharacter[] characters = tokenType.characters();
            visitTokenCharacters(tokenType, characters, graph);
        }
        return graph;
    }

    private static void visitTokenCharacters(TokenType tokenType, TokenCharacter[] characters, TokenGraph graph) {
        MutableGraphNode<TokenNode> previous = null;
        for(int i = 0; i < characters.length; i++) {
            TokenCharacter character = characters[i];
            MutableGraphNode<TokenNode> node = findExistingNode(previous, graph, character);
            TokenType nodeType = determineNodeType(tokenType, i, characters);
            previous = visitToken(graph, node, character, nodeType, previous);
        }
    }

    private static MutableGraphNode<TokenNode> visitToken(TokenGraph graph, MutableGraphNode<TokenNode> node,
            TokenCharacter character, TokenType nodeType, MutableGraphNode<TokenNode> previous) {
        if(node == null) {
            node = new SimpleGraphNode<>(new TokenNode(character, nodeType));
            graph.addRoot(node);
        }
        else if (nodeType != null && node.value().character == character) {
            node.value().tokenType(nodeType);
        }
        previous = finalizeNode(previous, node);
        return previous;
    }

    private static MutableGraphNode<TokenNode> findExistingNode(MutableGraphNode<TokenNode> previous, TokenGraph graph,
            TokenCharacter character) {
        Collection<GraphNode<TokenNode>> candidates = previous != null ? previous.children() : graph.roots();
        return getExisting(candidates, character);
    }

    private static TokenType determineNodeType(TokenType tokenType, int i, TokenCharacter[] characters) {
        return i == characters.length - 1 ? tokenType : null;
    }

    private static MutableGraphNode<TokenNode> finalizeNode(MutableGraphNode<TokenNode> previous,
            MutableGraphNode<TokenNode> node) {
        if(previous != null) {
            previous.addChild(node);
        }
        previous = node;
        return previous;
    }

    private static MutableGraphNode<TokenNode> getExisting(Collection<GraphNode<TokenNode>> candidates, TokenCharacter character) {
        return (MutableGraphNode<TokenNode>) candidates.stream()
                .filter(node -> node.value().character().equals(character))
                .findFirst()
                .orElse(null);
    }

    /**
     * Represents a node in the token graph. Each node contains a {@link TokenCharacter character} and a {@link TokenType type}.
     * The character is the character that is added by the node, any preceding characters are added by the parent node (if any).
     * The type is the type of the token that is represented by the characters that are added by the node and its parents, if
     * this is an intermediate node, the type is {@code null}.
     *
     * @since 0.6.0
     *
     * @author Guus Lieben
     */
    public static final class TokenNode {

        private final TokenCharacter character;
        private TokenType tokenType;

        public TokenNode(TokenCharacter character, TokenType tokenType) {
            this.character = character;
            this.tokenType = tokenType;
        }

        /**
         * Returns the character that is added by this node. Any preceding characters are added by the parent node (if any),
         * and are not composed by this node.
         *
         * @return the character that is added by this node
         */
        public TokenCharacter character() {
            return character;
        }

        /**
         * Returns the type of the token that is represented by the characters that are added by this node and its parents. If
         * this is an intermediate node, the type is {@code null}.
         *
         * @return the type of the token that is represented by the characters that are added by this node and its parents
         */
        public TokenType tokenType() {
            return tokenType;
        }

        /**
         * Sets the type of the token that is represented by the characters that are added by this node and its parents. If
         * this node already contains a type, an {@link IllegalStateException} is thrown.
         *
         * @param tokenType the type of the token that is represented by the characters that are added by this node and its parents
         *
         * @throws IllegalStateException if this node already contains a type
         */
        public void tokenType(TokenType tokenType) {
            if (this.tokenType() != null) {
                throw new IllegalStateException("Node already has a value");
            }
            this.tokenType = tokenType;
        }

        @Override
        public boolean equals(Object obj) {
            if(obj == this) {
                return true;
            }
            if(obj == null || obj.getClass() != this.getClass()) {
                return false;
            }
            var that = (TokenNode) obj;
            return Objects.equals(this.character, that.character) &&
                    Objects.equals(this.tokenType, that.tokenType);
        }

        @Override
        public int hashCode() {
            return Objects.hash(character, tokenType);
        }

        @Override
        public String toString() {
            return "TokenNode[" +
                    "character=" + character + ", " +
                    "tokenType=" + tokenType + ']';
        }
    }
}
