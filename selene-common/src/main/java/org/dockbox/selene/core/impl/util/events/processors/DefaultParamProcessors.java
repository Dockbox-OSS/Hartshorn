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

package org.dockbox.selene.core.impl.util.events.processors;

import org.dockbox.selene.core.util.events.AbstractEventParamProcessor;

import java.util.function.Supplier;

public enum DefaultParamProcessors {
    GETTER(GetterProcessor::new),
    PROVIDED(ProvidedProcessor::new),
    SKIP_IF(SkipIfProcessor::new),
    WRAP_SAFE(WrapSafeProcessor::new),
    UNWRAP_OR_SKIP(UnwrapOrSkipProcessor::new);

    private final Supplier<AbstractEventParamProcessor<?>> processorSupplier;

    DefaultParamProcessors(Supplier<AbstractEventParamProcessor<?>> processorSupplier) {
        this.processorSupplier = processorSupplier;
    }

    public AbstractEventParamProcessor<?> getProcessor() {
        return this.processorSupplier.get();
    }
}
