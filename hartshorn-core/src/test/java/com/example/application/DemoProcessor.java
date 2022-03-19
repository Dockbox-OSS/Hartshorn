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

package com.example.application;

import org.dockbox.hartshorn.inject.Key;
import org.dockbox.hartshorn.component.processing.AutomaticActivation;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.processing.ComponentPreProcessor;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@AutomaticActivation
public class DemoProcessor implements ComponentPreProcessor<UseDemo> {

    @Inject
    private DemoService demoService;

    @Inject
    private Demo demo;

    public DemoService demoService() {
        return this.demoService;
    }

    public Demo demo() {
        return this.demo;
    }

    @Override
    public Class<UseDemo> activator() {
        return UseDemo.class;
    }

    @Override
    public boolean modifies(final ApplicationContext context, final Key<?> key) {
        return false;
    }

    @Override
    public <T> void process(final ApplicationContext context, final Key<T> key) {
    }
}
