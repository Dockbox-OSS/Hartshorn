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

import java.util.List;

import org.dockbox.hartshorn.application.Hartshorn;
import org.dockbox.hartshorn.logging.AnsiColor;
import org.dockbox.hartshorn.logging.AnsiMessage;
import org.dockbox.hartshorn.logging.AnsiStyle;

/**
 * The default banner of Hartshorn. This banner is printed when the application starts if
 * no custom banner is provided.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class HartshornBanner extends AbstractConsoleBanner {

    private static final List<String> BANNER_LINES = List.of(
            "",
            "     _   _            _       _",
            "    | | | | __ _ _ __| |_ ___| |__   ___  _ __ _ __",
            "    | |_| |/ _` | '__| __/ __| '_ \\ / _ \\| '__| '_ \\",
            "    |  _  | (_| | |  | |_\\__ \\ | | | (_) | |  | | | |",
            AnsiMessage.of("====", AnsiColor.CYAN, AnsiStyle.BOLD)
                    .append("|_| |_|\\__,_|_|")
                    .append(AnsiMessage.of("===", AnsiColor.CYAN, AnsiStyle.BOLD))
                    .append("\\__|___/_|")
                    .append(AnsiMessage.of("=", AnsiColor.CYAN, AnsiStyle.BOLD))
                    .append("|_|\\___/|_|")
                    .append(AnsiMessage.of("==", AnsiColor.CYAN, AnsiStyle.BOLD))
                    .append("|_|")
                    .append(AnsiMessage.of("=", AnsiColor.CYAN, AnsiStyle.BOLD))
                    .append("|_|")
                    .append(AnsiMessage.of("====", AnsiColor.CYAN, AnsiStyle.BOLD))
                    .toString(),
            AnsiMessage.of("                                  ")
                    .append(AnsiMessage.of("-< ", AnsiColor.WHITE))
                    .append(AnsiMessage.of("Hartshorn v%s".formatted(Hartshorn.VERSION), AnsiColor.GREEN))
                    .append(AnsiMessage.of(" >-\n", AnsiColor.WHITE))
                    .toString()
    );

    @Override
    protected Iterable<String> lines() {
        return BANNER_LINES;
    }
}
