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

package org.dockbox.hartshorn.application.environment.banner;

import org.dockbox.hartshorn.application.Hartshorn;
import org.slf4j.Logger;

/**
 * The default banner of Hartshorn. This banner is printed when the application starts if
 * no custom banner is provided.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class HartshornBanner implements Banner {

    private static final String BANNER = """
                 _   _            _       _                     \s
                | | | | __ _ _ __| |_ ___| |__   ___  _ __ _ __ \s
                | |_| |/ _` | '__| __/ __| '_ \\ / _ \\| '__| '_ \\\s
                |  _  | (_| | |  | |_\\__ \\ | | | (_) | |  | | | |
            ====|_| |_|\\__,_|_|===\\__|___/_|=|_|\\___/|_|==|_|=|_|====
                                             -- Hartshorn v%s --
            """.formatted(Hartshorn.VERSION);

    @Override
    public void print(Logger logger) {
        for (String line : BANNER.split("\n")) {
            logger.info(line);
        }
        logger.info("");
    }
}
