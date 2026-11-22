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

package org.dockbox.hartshorn.web;

import jakarta.servlet.Servlet;
import org.dockbox.hartshorn.web.spec.PathSpec;

/**
 * A registrar for servlets, allowing for the manual registration of servlets outside of standard
 * routing.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public interface ServletRegistrar {

    /**
     * Registers a servlet with the given path specification.
     *
     * @param pathSpec the path specification for the servlet
     * @param servlet the servlet to register
     */
    void register(PathSpec pathSpec, Servlet servlet);
}
