package org.dockbox.hartshorn.hsl.parser.expression;

import org.dockbox.hartshorn.context.DefaultContext;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class FunctionParserContext extends DefaultContext {

    private final Set<String> prefixFunctions = new HashSet<>();
    private final Set<String> infixFunctions = new HashSet<>();

    public void addPrefixFunction(final String name) {
        this.prefixFunctions.add(name);
    }

    public void addInfixFunction(final String name) {
        this.infixFunctions.add(name);
    }

    public Set<String> prefixFunctions() {
        return Collections.unmodifiableSet(this.prefixFunctions);
    }

    public Set<String> infixFunctions() {
        return Collections.unmodifiableSet(this.infixFunctions);
    }
}
