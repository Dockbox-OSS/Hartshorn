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

package org.dockbox.hartshorn.api;

import com.google.common.collect.Multimap;

import org.dockbox.hartshorn.di.InjectConfiguration;
import org.dockbox.hartshorn.di.Modifier;
import org.dockbox.hartshorn.di.annotations.InjectPhase;

import java.lang.annotation.Annotation;
import java.util.List;

public class SampleBootstrap extends HartshornBootstrap {

    @Override
    public void create(String prefix, Class<?> activationSource, List<Annotation> activators, Multimap<InjectPhase, InjectConfiguration> configs, Modifier... modifiers) {
        // This is only used for Application testing, do not actually create bootstrap instance
    }
}
