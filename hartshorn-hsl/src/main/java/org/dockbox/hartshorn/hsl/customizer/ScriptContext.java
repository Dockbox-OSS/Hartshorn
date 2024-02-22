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

package org.dockbox.hartshorn.hsl.customizer;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.dockbox.hartshorn.context.DefaultApplicationAwareContext;
import org.dockbox.hartshorn.hsl.ast.statement.Statement;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.interpreter.ResultCollector;
import org.dockbox.hartshorn.hsl.lexer.Comment;
import org.dockbox.hartshorn.hsl.lexer.Lexer;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.runtime.ScriptRuntime;
import org.dockbox.hartshorn.hsl.semantic.Resolver;
import org.dockbox.hartshorn.hsl.token.DefaultTokenRegistry;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenRegistry;
import org.dockbox.hartshorn.util.option.Option;

/**
 * The standard context which tracks the state of the execution of a script. It is used by the
 * {@link org.dockbox.hartshorn.hsl.runtime.StandardRuntime} to track the state of the script's
 * execution, and store its various executors. Everything but the original script can be
 * customized during the script's execution, though some runtimes may not support this.
 *
 * @author Guus Lieben
 * @since 0.4.12
 */
public class ScriptContext extends DefaultApplicationAwareContext implements ResultCollector {

    private static final String GLOBAL_RESULT = ScriptContext.createSafeRuntimeVariable("result");
    protected Map<String, Object> results = new ConcurrentHashMap<>();

    private final ScriptRuntime runtime;
    private final String source;

    private String scriptName = "HSL Script %d".formatted(this.hashCode());

    private TokenRegistry tokenRegistry = DefaultTokenRegistry.createDefault();
    private List<Token> tokens;
    private List<Statement> statements;
    private List<Comment> comments;

    private Lexer lexer;
    private TokenParser parser;
    private Resolver resolver;
    private Interpreter interpreter;

    public ScriptContext(ScriptRuntime runtime, String source) {
        super(runtime.applicationContext());
        this.source = source;
        this.runtime = runtime;
    }

    public String source() {
        return this.source;
    }

    public String scriptName() {
        return this.scriptName;
    }

    public ScriptContext scriptName(String scriptName) {
        this.scriptName = scriptName;
        return this;
    }

    public List<Token> tokens() {
        return this.tokens;
    }

    public ScriptContext tokens(List<Token> tokens) {
        this.tokens = tokens;
        return this;
    }

    public TokenRegistry tokenRegistry() {
        return this.tokenRegistry;
    }

    public ScriptContext tokenRegistry(TokenRegistry tokenRegistry) {
        this.tokenRegistry = tokenRegistry;
        return this;
    }

    public List<Statement> statements() {
        return this.statements;
    }

    public ScriptContext statements(List<Statement> statements) {
        this.statements = statements;
        return this;
    }

    public List<Comment> comments() {
        return this.comments;
    }

    public ScriptContext comments(List<Comment> comments) {
        this.comments = comments;
        return this;
    }

    public Lexer lexer() {
        return this.lexer;
    }

    public ScriptContext lexer(Lexer lexer) {
        this.lexer = lexer;
        return this;
    }

    public TokenParser parser() {
        return this.parser;
    }

    public ScriptContext parser(TokenParser parser) {
        this.parser = parser;
        return this;
    }

    public Resolver resolver() {
        return this.resolver;
    }

    public ScriptContext resolver(Resolver resolver) {
        this.resolver = resolver;
        return this;
    }

    public Interpreter interpreter() {
        return this.interpreter;
    }

    public ScriptContext interpreter(Interpreter interpreter) {
        this.interpreter = interpreter;
        return this;
    }

    public ScriptRuntime runtime() {
        return this.runtime;
    }

    @Override
    public void addResult(Object value) {
        this.addResult(GLOBAL_RESULT, value);
    }

    @Override
    public void addResult(String id, Object value) {
        this.results.put(id, value);
    }

    @Override
    public <T> Option<T> result(Class<T> type) {
        return this.result(GLOBAL_RESULT, type);
    }

    @Override
    public Option<?> result() {
        return this.result(GLOBAL_RESULT);
    }

    @Override
    public <T> Option<T> result(String id, Class<T> type) {
        return Option.of(this.results.get(id)).ofType(type);
    }

    @Override
    public Option<?> result(String id) {
        return Option.of(this.results.get(id));
    }

    @Override
    public void clear() {
        this.results.clear();
    }

    public static String createSafeRuntimeVariable(String name) {
        return "$__%s__$".formatted(name);
    }
}
