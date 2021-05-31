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

package org.dockbox.selene.persistence.service;

import org.dockbox.selene.api.domain.Exceptional;
import org.dockbox.selene.api.domain.OwnerLookup;
import org.dockbox.selene.api.domain.TypedOwner;
import org.dockbox.selene.di.annotations.Service;
import org.dockbox.selene.di.context.ApplicationContext;
import org.dockbox.selene.persistence.FileManager;
import org.dockbox.selene.persistence.FileType;
import org.dockbox.selene.persistence.annotations.UsePersistence;
import org.dockbox.selene.persistence.mapping.ObjectMapper;
import org.dockbox.selene.proxy.handle.ProxyFunction;
import org.dockbox.selene.proxy.service.MethodProxyContext;
import org.dockbox.selene.proxy.service.ServiceAnnotatedMethodModifier;
import org.dockbox.selene.util.Reflect;

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
        switch (ctx.getTarget()) {
            case ANNOTATED_PATH:
                return this.processAnnotatedPath(context, methodContext, ctx);
            case PARAMETER_PATH:
                return this.processParameterPath(context, methodContext, ctx);
            case STRING:
                return this.processString(context, methodContext, ctx);
            default:
                throw new IllegalArgumentException("Unsupported serialisation target: " + ctx.getTarget());
        }
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

        if (!Reflect.isNotVoid(owner)) {
            final Service service = methodContext.getMethod().getDeclaringClass().getAnnotation(Service.class);
            owner = service.owner();
        }

        final TypedOwner lookup = context.get(OwnerLookup.class).lookup(owner);
        final FileManager fileManager = context.get(FileManager.class);

        if ("".equals(annotationContext.getFile().value())) return fileManager.getDataFile(lookup);
        else return fileManager.getDataFile(lookup, annotationContext.getFile().value());
    }
}
