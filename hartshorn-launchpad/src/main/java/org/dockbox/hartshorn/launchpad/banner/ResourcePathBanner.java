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

package org.dockbox.hartshorn.launchpad.banner;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Prints a banner from a given resource path. The resource path is expected to be a (plain) text file.
 *
 * @since 0.4.13
 *
 * @author Guus Lieben
 */
public class ResourcePathBanner extends AbstractConsoleBanner {

    private final InputStream resourcePath;

    public ResourcePathBanner(InputStream resourcePath) {
        this.resourcePath = resourcePath;
    }

    @Override
    protected Iterable<String> lines() {
        try {
            byte[] bytes = this.resourcePath.readAllBytes();
            String string = new String(bytes, StandardCharsets.UTF_8);
            return string.lines().toList();
        }
        catch (Exception e) {
            throw new IllegalStateException("Failed to read banner resource", e);
        }
    }
}
