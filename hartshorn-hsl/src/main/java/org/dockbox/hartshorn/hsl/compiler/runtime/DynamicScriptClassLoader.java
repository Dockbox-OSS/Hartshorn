/*
 * Copyright 2019-2026 the original author or authors.
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

package org.dockbox.hartshorn.hsl.compiler.runtime;

import java.lang.invoke.MethodHandles;

/**
 * Dynamic in-memory class loader and hidden class definer for compiled HSL script classes.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class DynamicScriptClassLoader extends ClassLoader {

    public DynamicScriptClassLoader() {
        super(DynamicScriptClassLoader.class.getClassLoader());
    }

    public DynamicScriptClassLoader(ClassLoader parent) {
        super(parent);
    }

    public Class<?> defineClass(String name, byte[] bytecode) {
        return super.defineClass(name, bytecode, 0, bytecode.length);
    }

    @SuppressWarnings("unchecked")
    public static <T> Class<? extends T> loadExecutableClass(String name, byte[] bytecode) {
        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            return (Class<? extends T>) lookup.defineHiddenClass(bytecode, true, MethodHandles.Lookup.ClassOption.NESTMATE).lookupClass();
        }
        catch (Throwable t) {
            DynamicScriptClassLoader loader = new DynamicScriptClassLoader();
            return (Class<? extends T>) loader.defineClass(name, bytecode);
        }
    }
}
