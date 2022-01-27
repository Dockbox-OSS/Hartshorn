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

package org.dockbox.hartshorn.demo.commands.services;

import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.commands.arguments.ArgumentConverterImpl;
import org.dockbox.hartshorn.commands.definition.ArgumentConverter;
import org.dockbox.hartshorn.core.annotations.stereotype.Service;

@Service
public final class ArgumentConverterService {

    public static final ArgumentConverter<String> GREETER = ArgumentConverterImpl.builder(String.class, "greeting")
            .withConverter(input -> Exceptional.of("Hello %s".formatted(input)))
            .build();

    private ArgumentConverterService() {}
}
