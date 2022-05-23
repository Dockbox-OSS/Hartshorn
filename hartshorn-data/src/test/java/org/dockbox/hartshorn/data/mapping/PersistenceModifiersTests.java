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

package org.dockbox.hartshorn.data.mapping;

import org.dockbox.hartshorn.util.StringUtilities;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.util.Result;
import org.dockbox.hartshorn.data.annotations.UsePersistence;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import javax.inject.Inject;

@HartshornTest
@UsePersistence
public class PersistenceModifiersTests {

    @Inject
    private ApplicationContext applicationContext;

    @Test
    void testSkipEmptyKeepsNonEmpty() {
        final ObjectMapper mapper = this.applicationContext.get(ObjectMapper.class).skipBehavior(JsonInclusionRule.SKIP_EMPTY);
        final ModifierElement element = new ModifierElement(List.of("sample", "other"));
        final Result<String> out = mapper.write(element);

        Assertions.assertTrue(out.present());
        Assertions.assertEquals("{\"names\":[\"sample\",\"other\"]}", StringUtilities.strip(out.get()));
    }

    @Test
    void testSkipEmptySkipsEmpty() {
        final ObjectMapper mapper = this.applicationContext.get(ObjectMapper.class).skipBehavior(JsonInclusionRule.SKIP_EMPTY);
        final ModifierElement element = new ModifierElement(List.of());
        final Result<String> out = mapper.write(element);

        Assertions.assertTrue(out.present());
        Assertions.assertEquals("{}", StringUtilities.strip(out.get()));
    }
}
