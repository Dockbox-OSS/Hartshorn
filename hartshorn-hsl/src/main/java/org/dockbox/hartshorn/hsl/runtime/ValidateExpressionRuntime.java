/*
 * Copyright 2019-2022 the original author or authors.
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

package org.dockbox.hartshorn.hsl.runtime;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.Component;
import org.dockbox.hartshorn.hsl.HslLanguageFactory;
import org.dockbox.hartshorn.hsl.customizer.ExpressionCustomizer;
import org.dockbox.hartshorn.hsl.interpreter.ResultCollector;
import org.dockbox.hartshorn.util.option.Option;

import jakarta.inject.Inject;

/**
 * A customized runtime specifically targeted at evaluating single expressions.
 * This makes use of the {@link StandardRuntime} to evaluate the script, and
 * customizes the input using the {@link ExpressionCustomizer}.
 *
 * @author Guus Lieben
 * @since 22.4
 */
@Component
public class ValidateExpressionRuntime extends StandardRuntime {

    @Inject
    public ValidateExpressionRuntime(final ApplicationContext applicationContext, final HslLanguageFactory factory) {
        super(applicationContext, factory);
        this.customizer(new ExpressionCustomizer());
    }

    /**
     * Looks up the validation result in the given {@link ResultCollector}. If there
     * is no validation result present, {@code false} is returned.
     *
     * @param collector The result collector in which the validation result may be stored.
     * @return The validation result, or {@code false} if it does not exist.
     */
    public static boolean valid(final ResultCollector collector) {
        final Option<Boolean> result = collector.result(ExpressionCustomizer.VALIDATION_ID);
        return Boolean.TRUE.equals(result.orElse(false));
    }
}
