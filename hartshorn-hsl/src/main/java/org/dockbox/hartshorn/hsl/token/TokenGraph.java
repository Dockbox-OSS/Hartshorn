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
import org.jetbrains.annotations.Nullable;

/**
 * TODO: Document this
 */
public class TokenGraph extends SimpleGraph<TokenNode> {

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
            updateNodeTokenType(node, nodeType);
        }
        previous = finalizeNode(previous, node);
        return previous;
    }

    private static MutableGraphNode<TokenNode> findExistingNode(MutableGraphNode<TokenNode> previous, TokenGraph graph,
            TokenCharacter character) {
        Collection<GraphNode<TokenNode>> candidates = previous != null ? previous.children() : graph.roots();
        return getExisting(candidates, character);
    }

    @Nullable
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

    private static void updateNodeTokenType(MutableGraphNode<TokenNode> node, TokenType nodeType) {
        if (node.value().tokenType() != null) {
            throw new IllegalStateException("Node already has a value");
        }
        node.value().tokenType(nodeType);
    }

    private static MutableGraphNode<TokenNode> getExisting(Collection<GraphNode<TokenNode>> candidates, TokenCharacter character) {
        return (MutableGraphNode<TokenNode>) candidates.stream()
                .filter(node -> node.value().character().equals(character))
                .findFirst()
                .orElse(null);
    }

    public static final class TokenNode {

        private final TokenCharacter character;
        private TokenType tokenType;

        public TokenNode(TokenCharacter character, TokenType tokenType) {
            this.character = character;
            this.tokenType = tokenType;
        }

        public TokenCharacter character() {
            return character;
        }

        public TokenType tokenType() {
            return tokenType;
        }

        public void tokenType(TokenType tokenType) {
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
