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

import org.dockbox.selene.core.annotations.Getter;
import org.dockbox.selene.core.annotations.Provided;
import org.dockbox.selene.core.annotations.SkipIf;
import org.dockbox.selene.core.annotations.UnwrapOrSkip;
import org.dockbox.selene.core.annotations.WrapSafe;
import org.dockbox.selene.core.exceptions.SkipEventException;
import org.dockbox.selene.core.objects.events.Event;
import org.dockbox.selene.core.objects.optional.Exceptional;
import org.dockbox.selene.core.server.Selene;
import org.dockbox.selene.core.util.SeleneUtils;
import org.dockbox.selene.core.util.events.AbstractEventParamProcessor;
import org.dockbox.selene.core.util.events.EventStage;
import org.dockbox.selene.core.util.events.IWrapper;
import org.dockbox.selene.core.util.extension.Extension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public enum DefaultParamProcessors {
    GETTER(Getter.class, EventStage.EARLY, (object, annotation, event, parameter, wrapper) -> {
        if (null != object && !annotation.overrideExisting()) return object;

        AtomicReference<Object> arg = new AtomicReference<>(null);
        SeleneUtils.getMethodValue(event, annotation.value(), parameter.getType())
                .ifPresent(arg::set);
        return arg.get();
    }),

    PROVIDED(Provided.class, EventStage.EARLY, (object, annotation, event, parameter, wrapper) -> {
        if (null != object && !annotation.overrideExisting()) return object;

        Class<?> extensionClass = parameter.getType();
        if (Void.class != annotation.value() && annotation.value().isAnnotationPresent(Extension.class)) {
            extensionClass = annotation.value();
        } else if (wrapper.getListener().getClass().isAnnotationPresent(Extension.class)) {
            extensionClass = wrapper.getListener().getClass();
        }
        return Selene.getInstance(parameter.getType(), extensionClass);
    }),

    SKIP_IF(SkipIf.class, EventStage.LATE, (object, annotation, event, parameter, wrapper) -> {
        switch (annotation.value()) {
            case NULL:
                if (null == object) throw new SkipEventException();
                break;
            case EMPTY:
                if (SeleneUtils.isEmpty(object)) throw new SkipEventException();
                break;
            case ZERO:
                if (object instanceof Number && 0 == ((Number) object).floatValue())
                    throw new SkipEventException();
                break;
        }
        return object;
    }),

    WRAP_SAFE(WrapSafe.class, EventStage.LATE, (object, annotation, event, parameter, wrapper) -> {
        if (parameter.getType().isAssignableFrom(event.getClass())) {
            Selene.log().warn("Event parameter cannot be wrapped");
            return object;
        }
        if (object instanceof Exceptional<?>) return object;
        if (object instanceof Optional<?>) return Exceptional.ofOptional((Optional<?>) object);

        return Exceptional.ofNullable(object);
    }),

    UNWRAP_OR_SKIP(UnwrapOrSkip.class, EventStage.LATE, (object, annotation, event, parameter, wrapper) -> {
        if (object instanceof Exceptional<?>) {
            if (((Exceptional<?>) object).isPresent()) return ((Exceptional<?>) object).get();
            else throw new SkipEventException();

        } else if (object instanceof Optional<?>) {
            if (((Optional<?>) object).isPresent()) return ((Optional<?>) object).get();
            else throw new SkipEventException();

        } else return object; // Already unwrapped
    });

    private final EventStage stage;
    private final Supplier<AbstractEventParamProcessor<?>> processorSupplier;

    <A extends Annotation> DefaultParamProcessors(Class<A> annotationClass, AbstractEnumEventParamProcessor<A> processor) {
        this(annotationClass, EventStage.NORMAL, processor);
    }

    <A extends Annotation> DefaultParamProcessors(Class<A> annotationClass, EventStage stage, AbstractEnumEventParamProcessor<A> processor) {
        this.stage = stage;
        this.processorSupplier = () -> new AbstractEventParamProcessor<A>() {
            @Override
            public @NotNull Class<A> getAnnotationClass() {
                return annotationClass;
            }

            @Override
            public @NotNull EventStage targetStage() {
                return null == stage ? super.targetStage() : stage;
            }

            @Override
            public @Nullable Object process(Object object, A annotation, Event event, Parameter parameter, IWrapper wrapper) throws SkipEventException {
                return processor.process(object, annotation, event, parameter, wrapper);
            }
        };
    }

    public EventStage getStage() {
        return this.stage;
    }

    public AbstractEventParamProcessor<?> getProcessor() {
        return this.processorSupplier.get();
    }

    @FunctionalInterface
    private interface AbstractEnumEventParamProcessor<A extends Annotation> {
        Object process(Object object, A annotation, Event event, Parameter parameter, IWrapper wrapper) throws SkipEventException;
    }
}
