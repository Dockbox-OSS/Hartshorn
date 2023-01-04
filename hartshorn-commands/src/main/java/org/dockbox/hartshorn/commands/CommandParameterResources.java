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

package org.dockbox.hartshorn.commands;

import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.i18n.annotations.InjectTranslation;
import org.dockbox.hartshorn.i18n.Message;

@Service
public interface CommandParameterResources {

    @InjectTranslation(value = "Not enough arguments.", key = "missing_args")
    Message notEnoughArgs();

    @InjectTranslation(value = "Pattern has to be formatted as #type[arg1][arg2][etc.]", key = "hashtag.wrong_format")
    Message wrongHashtagPatternFormat();

    @InjectTranslation(value = "Parameter of type {0} has no register converter", key = "missing_converter")
    Message missingConverter(String type);

}
