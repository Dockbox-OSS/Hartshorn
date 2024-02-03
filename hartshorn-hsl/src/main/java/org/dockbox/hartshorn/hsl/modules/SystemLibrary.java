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

package org.dockbox.hartshorn.hsl.modules;

import org.dockbox.hartshorn.hsl.customizer.ScriptContext;
import org.dockbox.hartshorn.hsl.token.type.LiteralTokenType;
import org.slf4j.LoggerFactory;

/**
 * Standard library granting access to environment variables.
 *
 * @author Guus Lieben
 * @since 0.4.12
 */
public class SystemLibrary {

    private final ScriptContext context;

    public SystemLibrary(ScriptContext context) {
        this.context = context;
    }

    /**
     * @see System#getenv(String)
     */
    public String env(String program) {
        return System.getenv(program);
    }

    public void print(Object object) {
        String text = this.stringify(object);
        // Create logger late, in case the script name changes.
        LoggerFactory.getLogger(context.scriptName()).info(text);
    }

    public String stringify(Object object) {
        if (object == null) {
            return LiteralTokenType.NULL.representation();
        }
        // Hack. Work around Java adding ".0" to integer-valued doubles.
        if (object instanceof Double) {
            String text = object.toString();
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }

        return object.toString()
                .replaceAll("\\\\n", "\n")
                .replaceAll("\\\\t", "\t");
    }
}

