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

package org.dockbox.hartshorn.web.util;

/**
 * Utility class for router-related operations, such as path manipulation and normalization.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public final class RouterUtilities {

    private RouterUtilities() {
    }

    /**
     * Combines multiple path segments into a single path, ensuring that there are no duplicate
     * slashes and that the resulting path is properly formatted. A prefix slash is added if the
     * first segment does not start with one. No trailing slash is added unless the last segment
     * ends with one.
     *
     * @param paths the path segments to combine
     * @return the combined path
     */
    public static String combinePaths(String... paths) {
        StringBuilder combined = new StringBuilder("/");
        for (String path : paths) {
            if (path == null || path.isEmpty()) continue;
            if (!combined.isEmpty() && combined.charAt(combined.length() - 1) != '/') {
                combined.append('/');
            }
            combined.append(path.startsWith("/") ? path.substring(1) : path);
        }
        return combined.toString();
    }
}
