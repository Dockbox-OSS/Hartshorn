package org.dockbox.hartshorn.hsl.callable.module;

import org.dockbox.hartshorn.hsl.ast.statement.NativeFunctionStatement;
import org.dockbox.hartshorn.hsl.callable.NativeExecutionException;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenType;
import org.dockbox.hartshorn.util.reflect.MethodContext;
import org.dockbox.hartshorn.util.reflect.ParameterContext;
import org.dockbox.hartshorn.util.reflect.TypeContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractNativeModule implements NativeModule {

    protected abstract Class<?> moduleClass();

    protected abstract Object instance();

    @Override
    public Object call(final Interpreter interpreter, final String function, final List<Object> arguments) throws NativeExecutionException {
        try {
            final Method method;
            if (arguments.isEmpty()) {
                method = this.moduleClass().getMethod(function);
                return method.invoke(this.instance());
            }
            else {
                final List<Class<?>> classArgs = new ArrayList<>();
                for (final Object arg : arguments) {
                    classArgs.add(arg.getClass());
                }
                // TODO: Match best method?
                method = this.moduleClass().getMethod(function, classArgs.toArray(new Class[0]));
                return method.invoke(this.instance(), arguments.toArray(new Object[0]));
            }
        }
        catch (final InvocationTargetException e) {
            throw new NativeExecutionException("Invalid Module Loader", e);
        }
        catch (final NoSuchMethodException e) {
            throw new NativeExecutionException("Module Loader : Can't find function with name : " + function, e);
        }
        catch (final IllegalAccessException e) {
            throw new NativeExecutionException("Module Loader : Can't access function with name : " + function, e);
        }
    }

    @Override
    public List<NativeFunctionStatement> supportedFunctions(final Token moduleName) {
        final List<NativeFunctionStatement> functionStatements = new ArrayList<>();

        for (final MethodContext<?, ?> method : TypeContext.of(this.moduleClass()).methods()) {
            if (!method.isPublic()) continue;
            final Token token = new Token(TokenType.IDENTIFIER, method.name(), -1);

            final List<Token> parameters = new ArrayList<>();
            for (final ParameterContext<?> parameter : method.parameters()) {
                parameters.add(new Token(TokenType.IDENTIFIER, parameter.name(), -1));
            }
            final NativeFunctionStatement functionStatement = new NativeFunctionStatement(token, moduleName, parameters);
            functionStatements.add(functionStatement);
        }
        return functionStatements;
    }
}
