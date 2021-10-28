/*
 *  Copyright (C) 2020 Guus Lieben
 *
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.hartshorn.web;

import org.dockbox.hartshorn.core.HartshornUtils;
import org.dockbox.hartshorn.web.processing.BodyRequestArgumentProcessor;
import org.dockbox.hartshorn.web.processing.HeaderRequestArgumentProcessor;
import org.dockbox.hartshorn.web.processing.RequestArgumentProcessor;

import java.util.Set;

import lombok.Getter;

public abstract class DefaultHttpWebServer implements HttpWebServer {

    @Getter
    private final Set<RequestArgumentProcessor<?>> processors = HartshornUtils.emptyConcurrentSet();

    protected DefaultHttpWebServer() {
        this.add(new BodyRequestArgumentProcessor());
        this.add(new HeaderRequestArgumentProcessor());
    }

    @Override
    public void add(final RequestArgumentProcessor<?> processor) {
        this.processors().add(processor);
    }
}
