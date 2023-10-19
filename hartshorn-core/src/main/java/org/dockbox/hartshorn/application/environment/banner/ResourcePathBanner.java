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

package org.dockbox.hartshorn.application.environment.banner;

import org.slf4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;

public class ResourcePathBanner implements Banner {

    private final Path resourcePath;

    public ResourcePathBanner(Path resourcePath) {
        this.resourcePath = resourcePath;
    }

    @Override
    public void print(Logger logger) {
        try {
            for (String line : Files.readAllLines(this.resourcePath)) {
                logger.info(line);
            }
            logger.info("");
        } catch (Exception e) {
            logger.error("Failed to print banner", e);
        }
    }
}
