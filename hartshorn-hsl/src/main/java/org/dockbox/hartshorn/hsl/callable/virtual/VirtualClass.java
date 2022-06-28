package org.dockbox.hartshorn.hsl.callable.virtual;

import org.dockbox.hartshorn.hsl.callable.ArityCheckingCallableNode;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.interpreter.VariableScope;
import org.dockbox.hartshorn.hsl.token.Token;

import java.util.List;
import java.util.Map;

public class VirtualClass extends ArityCheckingCallableNode {

    private final String name;
    private final VirtualClass superClass;
    private final VariableScope variableScope;
    private final Map<String, VirtualFunction> methods;

    public VirtualClass(final String name, final VirtualClass superClass, final VariableScope variableScope, final Map<String, VirtualFunction> methods) {
        this.name = name;
        this.superClass = superClass;
        this.methods = methods;
        this.variableScope = variableScope;
    }

    public String name() {
        return this.name;
    }

    public VirtualClass superClass() {
        return this.superClass;
    }

    public Map<String, VirtualFunction> methods() {
        return this.methods;
    }

    public void addMethod(final String name, final VirtualFunction function) {
        this.methods.put(name, function);
    }

    public VirtualFunction findMethod(final String name) {
        if (this.methods.containsKey(name)) {
            return this.methods.get(name);
        }
        // If we can't find this method in class check if this method is from super class
        if (this.superClass != null) {
            return this.superClass.findMethod(name);
        }
        return null;
    }

    public VariableScope variableScope() {
        return this.variableScope;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public Object call(final Token at, final Interpreter interpreter, final List<Object> arguments) {
        final VirtualInstance instance = new VirtualInstance(this);
        // Acts as a virtual constructor
        final VirtualFunction initializer = this.findMethod(VirtualFunction.CLASS_INIT);
        if (initializer != null) {
            initializer.bind(instance).call(at, interpreter, arguments);
        }
        return instance;
    }
}
