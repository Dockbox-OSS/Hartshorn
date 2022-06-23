package org.dockbox.hartshorn.hsl.callable;

import org.dockbox.hartshorn.hsl.interpreter.Environment;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;

import java.util.List;
import java.util.Map;

public class VirtualClass extends ArityCheckingCallable {

    private final String name;
    private final VirtualClass superClass;
    private final Environment environment;
    private final Map<String, VirtualFunction> methods;

    public VirtualClass(final String name, final VirtualClass superClass, final Environment environment, final Map<String, VirtualFunction> methods) {
        this.name = name;
        this.superClass = superClass;
        this.methods = methods;
        this.environment = environment;
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

    public Environment environment() {
        return this.environment;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public int arity() {
        final VirtualFunction initializer = this.findMethod("init");
        if (initializer == null) return 0;
        return initializer.arity();
    }

    @Override
    public Object call(final Interpreter interpreter, final List<Object> arguments) {
        final VirtualInstance instance = new VirtualInstance(this);
        //for Constructing the class
        final VirtualFunction initializer = this.findMethod("init");
        if (initializer != null) {
            initializer.bind(instance).call(interpreter, arguments);
        }
        return instance;
    }
}
