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

package org.dockbox.hartshorn.util;

/**
 * An exception thrown when a type conversion fails. This is typically thrown when a value cannot
 * be converted to a specific type.
 *
 * @since 0.4.1
 *
 * @author Guus Lieben
 */
public class TypeConversionException extends ApplicationRuntimeException {

    public TypeConversionException(String message) {
        super(message);
    }

    public TypeConversionException(Class<?> type, String value) {
        super("Could not convert '" + value + "' to type " + type.getSimpleName());
    }

    public TypeConversionException(Class<?> type, String value, Throwable cause) {
        super("Could not convert '" + value + "' to type " + type.getSimpleName(), cause);
    }
}
