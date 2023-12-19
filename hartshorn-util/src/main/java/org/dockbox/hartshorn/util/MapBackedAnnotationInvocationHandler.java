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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;

public class MapBackedAnnotationInvocationHandler implements InvocationHandler {

    private final Class<?> type;
    private final Map<String, Object> values;

    public MapBackedAnnotationInvocationHandler(Class<?> type, Map<String, Object> values) {
        this.type = type;
        this.values = this.checkValues(type, values);
    }

    private Map<String, Object> checkValues(Class<?> type, Map<String, Object> values) {
        if (values == null) {
            throw new IllegalStateException("Values map is null");
        }
        if (type == null) {
            throw new IllegalStateException("Type is null");
        }
        for (Entry<String, Object> entry : values.entrySet()) {
            try {
                Method method = type.getDeclaredMethod(entry.getKey());
                if (!TypeUtils.isAssignable(method.getReturnType(), entry.getValue().getClass())) {
                    throw new IllegalStateException("Value " + entry.getValue() + " is not assignable to " + method.getReturnType());
                }
            }
            catch (NoSuchMethodException e) {
                throw new IllegalStateException("No such method " + entry.getKey() + " on annotation " + type.getCanonicalName());
            }
        }
        return values;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        if ("annotationType".equals(methodName)) {
            return this.type;
        }
        if ("toString".equals(methodName)) {
            return "@" + this.type;
        }
        if ("hashCode".equals(methodName)) {
            return this.values.hashCode();
        }
        if ("equals".equals(methodName)) {
            return proxy == args[0];
        }
        if (this.values.containsKey(methodName)) {
            return this.values.get(methodName);
        }
        else {
            return method.getDefaultValue();
        }
    }

    public Class<?> type() {
        return this.type;
    }
}
