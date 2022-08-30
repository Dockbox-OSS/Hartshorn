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

package org.dockbox.hartshorn.beans;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.context.AutoCreating;
import org.dockbox.hartshorn.context.DefaultApplicationAwareContext;
import org.dockbox.hartshorn.util.reflect.TypeContext;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import jakarta.inject.Inject;

@AutoCreating
public class BeanContext extends DefaultApplicationAwareContext implements BeanCollector {

    private final List<BeanReference<?>> beans = new CopyOnWriteArrayList<>();

    @Inject
    public BeanContext(final ApplicationContext applicationContext) {
        super(applicationContext);
    }

    public BeanProvider provider() {
        return new ContextBeanProvider(this);
    }

    public List<BeanReference<?>> beans() {
        return this.beans;
    }

    @Override
    public <T> BeanReference<T> register(final T bean, final TypeContext<T> type, final String id) {
        final BeanReference<T> beanReference = new BeanReference<>(bean, type, id);
        this.beans.add(beanReference);
        return beanReference;
    }

    @Override
    public void unregister(final BeanReference<?> beanReference) {
        this.beans.remove(beanReference);
    }
}
