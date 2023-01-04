/*
 * Copyright 2019-2023 the original author or authors.
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

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.hsl.customizer.ScriptContext;
import org.dockbox.hartshorn.hsl.runtime.ScriptRuntime;
import org.dockbox.hartshorn.hsl.runtime.ValidateExpressionRuntime;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class HslExpression extends HslScript {

    protected HslExpression(final ApplicationContext context, final String source) {
        super(context, source);
    }

    public static HslExpression of(final ApplicationContext context, final String source) {
        return new HslExpression(context, source);
    }

    public static HslExpression of(final ApplicationContext context, final Path path) throws IOException {
        return of(context, sourceFromPath(path));
    }

    public static HslExpression of(final ApplicationContext context, final File file) throws IOException {
        return of(context, file.toPath());
    }

    public boolean valid() {
        final ScriptContext context = this.evaluate();
        return ValidateExpressionRuntime.valid(context);
    }

    @Override
    protected ScriptRuntime createRuntime() {
        return this.applicationContext().get(ValidateExpressionRuntime.class);
    }
}
