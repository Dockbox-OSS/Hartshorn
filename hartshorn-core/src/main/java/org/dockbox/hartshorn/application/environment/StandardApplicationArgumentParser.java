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

package org.dockbox.hartshorn.application.environment;

import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StandardApplicationArgumentParser implements ApplicationArgumentParser {

    private static final Pattern ARGUMENTS = Pattern.compile("--([a-zA-Z0-9\\.:_-]+)=(.+)");

    @Override
    public Properties parse(final List<String> arguments) {
        final Properties properties = new Properties();
        for (final String arg : arguments) {
            final Matcher matcher = ARGUMENTS.matcher(arg);
            if (matcher.find()) {
                properties.put(matcher.group(1), matcher.group(2));
            }
        }
        return properties;
    }
}
