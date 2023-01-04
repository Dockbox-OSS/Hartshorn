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

package org.dockbox.hartshorn.gradle.javadoc;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.AnnotationDeclaration;
import com.github.javaparser.ast.body.AnnotationMemberDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.EnumConstantDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.InitializerDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;
import com.github.javaparser.ast.nodeTypes.NodeWithJavadoc;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import com.github.javaparser.ast.nodeTypes.modifiers.NodeWithAccessModifiers;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.javadoc.JavadocBlockTag.Type;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public class JavadocVerificationVisitor extends VoidVisitorAdapter<Set<JavadocFault>> {

    private static final Type[] TYPE_LEVEL_TAGS = {Type.AUTHOR, Type.SINCE};

    @Override
    public void visit(final AnnotationDeclaration node, final Set<JavadocFault> arg) {
        arg.addAll(this.verify(node, TYPE_LEVEL_TAGS));
        super.visit(node, arg);
    }

    @Override
    public void visit(final AnnotationMemberDeclaration node, final Set<JavadocFault> arg) {
        arg.addAll(this.verify(node));
        super.visit(node, arg);
    }

    @Override
    public void visit(final ClassOrInterfaceDeclaration node, final Set<JavadocFault> arg) {
        arg.addAll(this.verify(node, TYPE_LEVEL_TAGS));
        super.visit(node, arg);
    }

    @Override
    public void visit(final ConstructorDeclaration node, final Set<JavadocFault> arg) {
        arg.addAll(this.verify(node));
        super.visit(node, arg);
    }

    @Override
    public void visit(final EnumConstantDeclaration node, final Set<JavadocFault> arg) {
        arg.addAll(this.verify(node));
        super.visit(node, arg);
    }

    @Override
    public void visit(final EnumDeclaration node, final Set<JavadocFault> arg) {
        arg.addAll(this.verify(node, TYPE_LEVEL_TAGS));
        super.visit(node, arg);
    }

    @Override
    public void visit(final FieldDeclaration node, final Set<JavadocFault> arg) {
        arg.addAll(this.verify(node, node.getVariable(0).getNameAsString()));
        super.visit(node, arg);
    }

    @Override
    public void visit(final InitializerDeclaration node, final Set<JavadocFault> arg) {
        arg.addAll(this.verify(node, "initializer"));
        super.visit(node, arg);
    }

    @Override
    public void visit(final MethodDeclaration node, final Set<JavadocFault> arg) {
        arg.addAll(this.verify(node));
        super.visit(node, arg);
    }

    // TODO: #907 Implement Javadoc verification for all node types. Currently not possible
    //  due to Gradle-level inclusion of JavaParser 3.17.0, which does not fully support
    //  records yet. This will require a Gradle upgrade once it is available.

    // @Override
    // public void visit(final RecordDeclaration node, final Set<JavadocFault> arg) {
    //     this.verify(node, Type.AUTHOR, Type.SINCE).ifPresent(arg::add);
    //     super.visit(node, arg);
    // }

    // @Override
    // public void visit(final CompactConstructorDeclaration node, final Set<JavadocFault> arg) {
    //     this.verify(node).ifPresent(arg::add);
    //     super.visit(node, arg);
    // }

    private <N extends Node & NodeWithJavadoc<N> & NodeWithAnnotations<N> & NodeWithSimpleName<N>> Set<JavadocFault> verify(final N node, final Type... requiredTypes) {
        return this.verify(node, node.getNameAsString(), requiredTypes);
    }

    private <N extends Node & NodeWithJavadoc<N> & NodeWithAnnotations<N>> Set<JavadocFault> verify(final N node, final String name, final Type... requiredTypes) {
        return node.getJavadocComment()
                .map(comment -> {
                    final Set<JavadocFault> messages = new HashSet<>();
                    final Javadoc javadoc = comment.parse();

                    final EnumSet<Type> tagTypes = requiredTypes.length == 0
                            ? EnumSet.noneOf(Type.class)
                            : EnumSet.copyOf(Arrays.asList(requiredTypes));

                    if (node.isAnnotationPresent(Deprecated.class)) tagTypes.add(Type.DEPRECATED);

                    for (final Type requiredType : tagTypes) {
                        if (javadoc.getBlockTags().stream().noneMatch(tag -> tag.getType() == requiredType)) {
                            messages.add(new JavadocFault(node, name, "Missing " + requiredType.name().toLowerCase() + " tag in javadoc"));
                        }
                    }

                    return messages;
                })
                .orElseGet(() -> {
                    if (node.isAnnotationPresent(Override.class)) return Collections.emptySet();
                    if (node instanceof NodeWithAccessModifiers<?> nodeWithAccessModifiers) {
                        if (nodeWithAccessModifiers.isPrivate()) return Collections.emptySet();
                    }
                    return Set.of(new JavadocFault(node, name, "Missing Javadoc comment"));
                });
    }
}
