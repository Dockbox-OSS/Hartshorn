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

package org.dockbox.hartshorn.persistence.service;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.domain.TypedOwner;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.di.context.element.TypeContext;
import org.dockbox.hartshorn.di.services.ComponentContainer;
import org.dockbox.hartshorn.persistence.FileManager;
import org.dockbox.hartshorn.persistence.FileType;
import org.dockbox.hartshorn.persistence.annotations.UsePersistence;
import org.dockbox.hartshorn.persistence.context.PersistenceAnnotationContext;
import org.dockbox.hartshorn.persistence.context.SerialisationContext;
import org.dockbox.hartshorn.persistence.mapping.ObjectMapper;
import org.dockbox.hartshorn.proxy.handle.ProxyFunction;
import org.dockbox.hartshorn.proxy.service.MethodProxyContext;
import org.dockbox.hartshorn.proxy.service.ServiceAnnotatedMethodModifier;

import java.lang.annotation.Annotation;
import java.nio.file.Path;

public abstract class AbstractPersistenceServiceModifier<M extends Annotation, C extends SerialisationContext> extends ServiceAnnotatedMethodModifier<M, UsePersistence> {

    @Override
    public Class<UsePersistence> activator() {
        return UsePersistence.class;
    }

    @Override
    public <T, R> ProxyFunction<T, R> process(final ApplicationContext context, final MethodProxyContext<T> methodContext) {
        final Exceptional<C> serialisationContext = methodContext.first(this.contextType());
        if (serialisationContext.absent()) throw new IllegalStateException("Expected additional context to be present");

        final C ctx = serialisationContext.get();
        return switch (ctx.target()) {
            case ANNOTATED_PATH -> this.processAnnotatedPath(context, methodContext, ctx);
            case PARAMETER_PATH -> this.processParameterPath(context, methodContext, ctx);
            case STRING -> this.processString(context, methodContext, ctx);
            default -> throw new IllegalArgumentException("Unsupported serialisation target: " + ctx.target());
        };
    }

    protected abstract Class<C> contextType();

    protected abstract <T, R> ProxyFunction<T, R> processAnnotatedPath(ApplicationContext context, MethodProxyContext<T> methodContext, C serialisationContext);

    protected abstract <T, R> ProxyFunction<T, R> processParameterPath(ApplicationContext context, MethodProxyContext<T> methodContext, C serialisationContext);

    protected abstract <T, R> ProxyFunction<T, R> processString(ApplicationContext context, MethodProxyContext<T> methodContext, C serialisationContext);

    protected ObjectMapper mapper(final ApplicationContext context, final C serialisationContext) {
        final ObjectMapper objectMapper = context.get(ObjectMapper.class);
        final FileType fileType = serialisationContext.fileType();
        objectMapper.fileType(fileType);
        return objectMapper;
    }

    protected Path determineAnnotationPath(final ApplicationContext context, final MethodProxyContext<?> methodContext, final PersistenceAnnotationContext annotationContext) {
        TypeContext<?> owner = TypeContext.of(annotationContext.file().owner());

        if (owner.isVoid()) {
            final Exceptional<ComponentContainer> container = context.locator().container(methodContext.method().parent());
            if (container.present()) {
                owner = container.get().owner();
            }
        }

        final TypedOwner lookup = context.meta().lookup(owner);
        final FileManager fileManager = context.get(FileManager.class);

        if ("".equals(annotationContext.file().value())) return fileManager.dataFile(lookup);
        else return fileManager.dataFile(lookup, annotationContext.file().value());
    }
}
