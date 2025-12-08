/*
 * Copyright 2019-2025 the original author or authors.
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

package org.dockbox.hartshorn.hsl;

import org.dockbox.hartshorn.hsl.customizer.DefaultScriptStatementsParserCustomizer;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.parser.StandardTokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.runtime.ScriptRuntime;
import org.dockbox.hartshorn.hsl.runtime.StandardRuntime;
import org.dockbox.hartshorn.hsl.runtime.ValidateExpressionRuntime;
import org.dockbox.hartshorn.hsl.semantic.Resolver;
import org.dockbox.hartshorn.hsl.semantic.SimpleResolver;
import org.dockbox.hartshorn.hsl.token.DefaultTokenRegistry;
import org.dockbox.hartshorn.inject.annotations.SupportPriority;
import org.dockbox.hartshorn.inject.annotations.configuration.Configuration;
import org.dockbox.hartshorn.inject.annotations.configuration.Prototype;
import org.dockbox.hartshorn.inject.annotations.configuration.Singleton;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.launchpad.condition.RequiresActivator;

/**
 * Default configuration for HSL, supporting the full range of tokens and runtime features.
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
@Configuration
@RequiresActivator(UseExpressionValidation.class)
public class ScriptLanguageConfiguration {

    /**
     * Provides a standard script component factory for creating script components.
     *
     * @return the standard script component factory
     */
    @Singleton
    @SupportPriority
    private ScriptComponentFactory languageFactory() {
        return new StandardScriptComponentFactory();
    }

    /**
     * Provides a standard token parser with the default token registry.
     *
     * @return the standard token parser
     */
    @Prototype
    @SupportPriority
    private TokenParser tokenParser() {
        return new StandardTokenParser(DefaultTokenRegistry.createDefault());
    }

    /**
     * Provides a standard resolver for script semantics.
     *
     * @param interpreter the interpreter related to the resolver
     *
     * @return the standard resolver
     */
    @Prototype
    @SupportPriority
    private Resolver resolver(Interpreter interpreter) {
        return new SimpleResolver(interpreter);
    }

    /**
     * Provides the standard script runtime with full capabilities.
     *
     * @param applicationContext the application context in which the runtime operates
     * @param factory the script component factory used to create necessary components
     * @param parserCustomizer the parser customizer for script statements
     *
     * @return the standard script runtime
     */
    @Prototype
    @SupportPriority
    public ScriptRuntime runtime(
        ApplicationContext applicationContext,
        ScriptComponentFactory factory,
        ParserCustomizer parserCustomizer
    ) {
        return new StandardRuntime(applicationContext, factory, parserCustomizer);
    }

    /**
     * Provides a runtime specialized in expression validation.
     *
     * @param applicationContext the application context in which the runtime operates
     * @param factory the script component factory used to create necessary components
     * @param parserCustomizer the parser customizer for script statements
     *
     * @return the validate expression runtime
     */
    @Prototype
    @SupportPriority
    public ValidateExpressionRuntime expressionRuntime(
        ApplicationContext applicationContext,
        ScriptComponentFactory factory,
        ParserCustomizer parserCustomizer
    ) {
        return new ValidateExpressionRuntime(applicationContext, factory, parserCustomizer);
    }

    /**
     * Provides a default parser customizer for script statements, enabling all standard features.
     *
     * @return the parser customizer
     *
     * @see DefaultScriptStatementsParserCustomizer
     */
    @Singleton
    @SupportPriority
    public ParserCustomizer parserCustomizer() {
        return new DefaultScriptStatementsParserCustomizer();
    }
}
