package org.dockbox.hartshorn.hsl.interpreter;

import org.dockbox.hartshorn.hsl.runtime.RuntimeError;
import org.dockbox.hartshorn.hsl.token.Token;

import java.util.HashMap;
import java.util.Map;

public class Environment {

    public final Environment enclosing;
    private final Map<String, Object> valuesMap = new HashMap<>();

    public Environment() {
        this.enclosing = null;
    }

    public Environment(final Environment enclosing) {
        this.enclosing = enclosing;
    }

    public Object get(final Token name) {
        if (this.valuesMap.containsKey(name.lexeme())) {
            return this.valuesMap.get(name.lexeme());
        }

        //If the variable isn’t found in this scope, we simply try the enclosing one
        if (this.enclosing != null) return this.enclosing.get(name);

        throw new RuntimeError(name,
                "Undefined variable '" + name.lexeme() + "'.");
    }

    public void define(final String name, final Object value) {
        this.valuesMap.put(name, value);
    }

    public boolean isContain(final String name) {
        return this.valuesMap.containsKey(name);
    }

    public void assign(final Token name, final Object value) {
        if (this.valuesMap.containsKey(name.lexeme())) {
            this.valuesMap.put(name.lexeme(), value);
            return;
        }
        //Again, if the variable isn’t in this environment, it checks the outer one, recursively.
        if (this.enclosing != null) {
            this.enclosing.assign(name, value);
            return;
        }
        throw new RuntimeError(name, "Undefined variable '" + name.lexeme() + "'.");
    }

    void assignAt(final int distance, final Token name, final Object value) {
        this.ancestor(distance).valuesMap.put(name.lexeme(), value);
    }

    public Object getAt(final int distance, final String name) {
        return this.ancestor(distance).valuesMap.get(name);
    }

    Environment ancestor(final int distance) {
        Environment environment = this;
        for (int i = 0; i < distance; i++) {
            environment = environment.enclosing;
        }

        return environment;
    }
}
