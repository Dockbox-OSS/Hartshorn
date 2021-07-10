/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.hartshorn.di.types.bean;

import org.dockbox.hartshorn.di.annotations.inject.Bean;
import org.dockbox.hartshorn.di.annotations.inject.Named;
import org.dockbox.hartshorn.di.annotations.service.Service;
import org.dockbox.hartshorn.di.annotations.inject.Wired;
import org.dockbox.hartshorn.di.types.SampleField;

import javax.inject.Singleton;

@Service
public class SampleBeanService {

    @Bean
    public BeanInterface get() {
        return () -> "Bean";
    }

    @Bean("named")
    public BeanInterface getNamed() {
        return () -> "NamedBean";
    }

    @Bean("field")
    public BeanInterface getWithField(SampleField field) {
        return () -> "FieldBean";
    }

    @Bean("namedField")
    public BeanInterface getWithNamedField(@Named("named") SampleField field) {
        return () -> "NamedFieldBean";
    }

    @Singleton
    @Bean("singleton")
    public BeanInterface getSingleton() {
        return () -> "SingletonBean";
    }

    @Bean("wired")
    @Wired
    public BeanInterface getManualWired(String name) {
        return () -> name;
    }

}
