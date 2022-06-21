package org.dockbox.hartshorn.hsl.callable;

import org.dockbox.hartshorn.hsl.ast.NativeFunctionStatement;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HslLibrary implements HslCallable {

    private final NativeFunctionStatement declaration;
    private final Map<String, Class<?>> externalModules;

    public HslLibrary(final NativeFunctionStatement declaration, final Map<String, Class<?>> externalModules) {
        this.declaration = declaration;
        this.externalModules = externalModules;
    }

    @Override
    public int arity() {
        return this.declaration.getParams().size();
    }

    @Override
    public Object call(final Interpreter interpreter, final List<Object> arguments) throws NativeExecutionException {
        final String moduleName = this.declaration.getModuleName().lexeme();
        final String functionName = this.declaration.getName().lexeme();

        if (!this.externalModules.containsKey(moduleName)) {
            throw new NativeExecutionException("Module Loader : Can't find class with name : " + functionName);
        }

        try {
            final Class<?> moduleClass = this.externalModules.get(moduleName);
            final Method method;
            if (arguments.isEmpty()) {
                method = moduleClass.getMethod(functionName);
                return method.invoke(moduleClass.newInstance());
            }
            else {
                final List<Class<?>> classArgs = new ArrayList<>();
                for (final Object arg : arguments) {
                    classArgs.add(arg.getClass());
                }
                // TODO: Match best method?
                method = moduleClass.getMethod(functionName, classArgs.toArray(new Class[0]));
                return method.invoke(moduleClass.newInstance(), arguments.toArray(new Object[0]));
            }
        }
        catch (final InstantiationException | InvocationTargetException e) {
            throw new NativeExecutionException("Invalid Module Loader", e);
        }
        catch (final NoSuchMethodException e) {
            throw new NativeExecutionException("Module Loader : Can't find function with name : " + functionName, e);
        }
        catch (final IllegalAccessException e) {
            throw new NativeExecutionException("Module Loader : Can't access module " + moduleName, e);
        }
    }
}
