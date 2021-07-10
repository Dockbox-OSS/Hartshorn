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
import org.dockbox.hartshorn.di.services.ServiceContainer;
import org.dockbox.hartshorn.persistence.FileManager;
import org.dockbox.hartshorn.persistence.FileType;
import org.dockbox.hartshorn.persistence.annotations.UsePersistence;
import org.dockbox.hartshorn.persistence.mapping.ObjectMapper;
import org.dockbox.hartshorn.proxy.handle.ProxyFunction;
import org.dockbox.hartshorn.proxy.service.MethodProxyContext;
import org.dockbox.hartshorn.proxy.service.ServiceAnnotatedMethodModifier;
import org.dockbox.hartshorn.util.Reflect;

import java.lang.annotation.Annotation;
import java.nio.file.Path;

public abstract class AbstractPersistenceServiceModifier<M extends Annotation, C extends SerialisationContext> extends ServiceAnnotatedMethodModifier<M, UsePersistence> {

    @Override
    public Class<UsePersistence> activator() {
        return UsePersistence.class;
    }

    @Override
    public <T, R> ProxyFunction<T, R> process(ApplicationContext context, MethodProxyContext<T> methodContext) {
        final Exceptional<C> serialisationContext = methodContext.first(this.getContextType());
        if (serialisationContext.absent()) throw new IllegalStateException("Expected additional context to be present");

        final C ctx = serialisationContext.get();
        return switch (ctx.getTarget()) {
            case ANNOTATED_PATH -> this.processAnnotatedPath(context, methodContext, ctx);
            case PARAMETER_PATH -> this.processParameterPath(context, methodContext, ctx);
            case STRING -> this.processString(context, methodContext, ctx);
            default -> throw new IllegalArgumentException("Unsupported serialisation target: " + ctx.getTarget());
        };
    }

    protected abstract  <T, R> ProxyFunction<T, R> processAnnotatedPath(ApplicationContext context, MethodProxyContext<T> methodContext, C serialisationContext);

    protected abstract <T, R> ProxyFunction<T, R> processParameterPath(ApplicationContext context, MethodProxyContext<T> methodContext, C serialisationContext);

    protected abstract <T, R> ProxyFunction<T, R> processString(ApplicationContext context, MethodProxyContext<T> methodContext, C serialisationContext);

    protected abstract Class<C> getContextType();

    protected ObjectMapper getObjectMapper(ApplicationContext context, C serialisationContext) {
        final ObjectMapper objectMapper = context.get(ObjectMapper.class);
        final FileType fileType = serialisationContext.getFileType();
        objectMapper.setFileType(fileType);
        return objectMapper;
    }

    protected Path determineAnnotationPath(ApplicationContext context, MethodProxyContext<?> methodContext, PersistenceAnnotationContext annotationContext) {
        Class<?> owner = annotationContext.getFile().owner();

        if (!Reflect.notVoid(owner)) {
            final Exceptional<ServiceContainer> container = context.locator().container(methodContext.getMethod().getDeclaringClass());
            if (container.present()) {
                owner = container.get().owner();
            }
        }

        final TypedOwner lookup = context.meta().lookup(owner);
        final FileManager fileManager = context.get(FileManager.class);

        if ("".equals(annotationContext.getFile().value())) return fileManager.getDataFile(lookup);
        else return fileManager.getDataFile(lookup, annotationContext.getFile().value());
    }
}
