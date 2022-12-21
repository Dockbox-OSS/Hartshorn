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
