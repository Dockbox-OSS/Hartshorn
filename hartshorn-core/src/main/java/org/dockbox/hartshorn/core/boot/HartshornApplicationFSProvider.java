/*
 * Copyright 2019-2022 the original author or authors.
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

package org.dockbox.hartshorn.core.boot;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * A {@link HartshornApplicationFSProvider} that uses the current working directory as the root.
 *
 * @author Guus Lieben
 * @since 21.9
 */
public class HartshornApplicationFSProvider implements ApplicationFSProvider{
    @Override
    public Path applicationPath() {
        return Paths.get("");
    }
}
