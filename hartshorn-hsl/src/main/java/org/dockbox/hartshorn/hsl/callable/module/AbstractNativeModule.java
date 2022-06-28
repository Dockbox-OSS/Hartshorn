package org.dockbox.hartshorn.hsl.callable.module;

import org.dockbox.hartshorn.hsl.ast.statement.NativeFunctionStatement;
import org.dockbox.hartshorn.hsl.callable.NativeExecutionException;
import org.dockbox.hartshorn.hsl.callable.external.ExecutableLookup;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.runtime.RuntimeError;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenType;
import org.dockbox.hartshorn.util.reflect.MethodContext;
import org.dockbox.hartshorn.util.reflect.ParameterContext;
import org.dockbox.hartshorn.util.reflect.TypeContext;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractNativeModule implements NativeModule {

    protected abstract Class<?> moduleClass();

    protected abstract Object instance();

    @Override
    public Object call(final Token at, final Interpreter interpreter, final String function, final List<Object> arguments) throws NativeExecutionException {
        try {
            final TypeContext<Object> type = TypeContext.of((Class<Object>) this.moduleClass());
            final MethodContext<?, Object> method;
            if (arguments.isEmpty()) {
                method = type.method(function).rethrow().get();
            }
            else {
                method = ExecutableLookup.method(at, type, function, arguments);
            }
            return method.invoke(this.instance(), arguments.toArray(new Object[0]));
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
        catch (final Throwable e) {
            throw new RuntimeError(at, e.getMessage());
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
