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

package org.dockbox.selene.core.events.processing;

import org.dockbox.selene.core.exceptions.SkipEventException;
import org.dockbox.selene.core.events.parents.Event;
import org.dockbox.selene.core.events.handling.EventStage;
import org.dockbox.selene.core.events.handling.IWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;

/**
 A low level type used to process a given {@link Annotation} type. Typically this is only used by {@link IWrapper}
 implementations to populate, filter, or skip parameter arguments.

 @param <A>
 The annotation type the processor applies to.
 */
public abstract class AbstractEventParamProcessor<A extends Annotation> {

    /**
     Gets the annotation {@link Class} instance, which can be used to identify the processor for a given
     {@link Annotation} type.

     @return the annotation class
     */

    @NotNull
    public abstract Class<A> getAnnotationClass();

    /**
     Gets the target {@link EventStage} in which this processor should be applied.
     Defaults to {@link EventStage#PROCESS}.

     @return The target event stage
     */
    @NotNull
    public EventStage targetStage() {
        return EventStage.PROCESS;
    }

    /**
     Processes the given object based on available information in the given {@link A Annotation}, {@link Event},
     {@link Parameter}} and/or {@link IWrapper}.
     If the parameter does not meet expectations, or should not be used in a given listener method,
     {@link SkipEventException} is thrown.

     @param object
     The object to process.
     @param annotation
     The annotation containing the definition of how the processor should act.
     @param event
     The event for which the listener can be invoked.
     @param parameter
     The parameter on which the annotation is applied.
     @param wrapper
     The {@link IWrapper} which contains the invoking definition.

     @return The processed object

     @throws SkipEventException
     Indicates the event listener should be skipped.
     */
    @Nullable
    public abstract Object process(@Nullable Object object, A annotation, Event event, Parameter parameter, IWrapper wrapper) throws SkipEventException;

}
