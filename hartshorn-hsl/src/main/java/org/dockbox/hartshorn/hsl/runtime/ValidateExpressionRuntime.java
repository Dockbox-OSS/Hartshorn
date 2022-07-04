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
import org.dockbox.hartshorn.hsl.HslLanguageFactory;
import org.dockbox.hartshorn.hsl.customizer.ExpressionCustomizer;
import org.dockbox.hartshorn.hsl.interpreter.ResultCollector;
import org.dockbox.hartshorn.util.Result;

import jakarta.inject.Inject;

public class ValidateExpressionRuntime extends StandardRuntime {

    @Inject
    public ValidateExpressionRuntime(final ApplicationContext applicationContext, final HslLanguageFactory factory) {
        super(applicationContext, factory);
        this.customizer(new ExpressionCustomizer());
    }

    public static boolean valid(final ResultCollector context) {
        final Result<Boolean> result = context.result(ExpressionCustomizer.VALIDATION_ID);
        return result.or(false);
    }
}
