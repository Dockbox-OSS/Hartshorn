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

package org.dockbox.hartshorn.web.spec;

public enum PathSpecPartType {
    /**
     * A static path segment, e.g. "users" in "/users/profile".
     */
    STATIC,

    /**
     * A wildcard path segment, represented by "*", which matches any single segment.
     */
    WILDCARD,

    /**
     * A parameterized path segment, represented by "{param}", which captures the value of the segment as a parameter.
     */
    PARAMETER,
}
