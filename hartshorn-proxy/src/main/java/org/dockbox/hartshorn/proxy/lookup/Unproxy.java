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

package org.dockbox.hartshorn.proxy.lookup;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to indicate a method parameter should be unproxied, if the parameter is a proxy. If the parameter is not a proxy, the method should
 * be called as normal. If the parameter is a proxy, but there is no underlying instance, the parameter will be transformed to {@code null}, unless
 * {@link #fallbackToProxy()} is set to {@code true}, in which case the proxy instance will be provided to the method.
 *
 * @author Guus Lieben
 * @since 0.5.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.PARAMETER})
public @interface Unproxy {

    /**
     * If {@code true}, the proxy instance will be provided to the method if the parameter is a proxy, but
     * there is no underlying delegate instance.
     *
     * @return {@code true} if the proxy instance may be used as a fallback, {@code false} otherwise
     */
    boolean fallbackToProxy() default false;
}
