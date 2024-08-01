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

package org.dockbox.hartshorn.testsuite;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.dockbox.hartshorn.context.ContextView;
import org.dockbox.hartshorn.util.introspect.annotations.Extends;
import org.junit.jupiter.api.Test;

import org.dockbox.hartshorn.inject.Inject;

/**
 * Acts as a composite between {@link Inject} and {@link Test}. This allows test method parameters to be injected
 * into the test method. Note that this will inject on provision-basis and will not inject {@link ContextView} types.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Test
@Extends(Inject.class)
public @interface InjectTest {
}
