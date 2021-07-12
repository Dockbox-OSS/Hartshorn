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
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.persistence.annotations.Deserialise;
import org.dockbox.hartshorn.persistence.mapping.ObjectMapper;
import org.dockbox.hartshorn.proxy.handle.ProxyFunction;
import org.dockbox.hartshorn.proxy.service.MethodProxyContext;
import org.dockbox.hartshorn.util.Reflect;

import java.io.File;
import java.lang.reflect.Type;
import java.nio.file.Path;

public class DeserialisationServiceModifier extends AbstractPersistenceServiceModifier<Deserialise, DeserialisationContext> {

    @Override
    protected <T, R> ProxyFunction<T, R> processAnnotatedPath(ApplicationContext context, MethodProxyContext<T> methodContext, DeserialisationContext serialisationContext) {
        return (instance, args, proxyContext) -> {
            final Path path = serialisationContext.predeterminedPath();
            final ObjectMapper objectMapper = this.mapper(context, serialisationContext);

            final Exceptional<?> result = objectMapper.read(path, serialisationContext.type());
            return this.wrapResult(result, methodContext);
        };
    }

    @Override
    protected <T, R> ProxyFunction<T, R> processParameterPath(ApplicationContext context, MethodProxyContext<T> methodContext, DeserialisationContext serialisationContext) {
        return (instance, args, proxyContext) -> {
            Path path;
            if (args[0] instanceof Path) path = (Path) args[0];
            else if (args[0] instanceof File) path = ((File) args[0]).toPath();
            else throw new IllegalArgumentException("Expected one argument to be a subtype of File or Path");

            final ObjectMapper objectMapper = this.mapper(context, serialisationContext);

            final Exceptional<?> result = objectMapper.read(path, serialisationContext.type());
            return this.wrapResult(result, methodContext);
        };
    }

    @Override
    protected <T, R> ProxyFunction<T, R> processString(ApplicationContext context, MethodProxyContext<T> methodContext, DeserialisationContext serialisationContext) {
        return (instance, args, proxyContext) -> {
            final String raw = (String) args[0];
            final ObjectMapper objectMapper = this.mapper(context, serialisationContext);

            final Exceptional<?> result = objectMapper.read(raw, serialisationContext.type());
            return this.wrapResult(result, methodContext);
        };
    }

    @SuppressWarnings("unchecked")
    private <R> R wrapResult(Exceptional<?> result, MethodProxyContext<?> methodContext) {
        if (Reflect.assigns(Exceptional.class, methodContext.returnType())) return (R) result;
        else return (R) result.orNull();
    }

    @Override
    protected Class<DeserialisationContext> contextType() {
        return DeserialisationContext.class;
    }

    @Override
    public <T> boolean preconditions(ApplicationContext context, MethodProxyContext<T> methodContext) {
        if (methodContext.method().getParameterCount() > 1) return false;
        if (!Reflect.notVoid(methodContext.returnType())) return false;

        final Deserialise annotation = methodContext.annotation(Deserialise.class);

        final Class<?> outputType = this.outputType(methodContext);
        if (outputType == null) return false;

        DeserialisationContext deserialisationContext = new DeserialisationContext(outputType);
        deserialisationContext.fileType(annotation.filetype());
        methodContext.add(deserialisationContext);

        if (methodContext.method().getParameterCount() == 0) {
            deserialisationContext.target(SerialisationTarget.ANNOTATED_PATH);
            deserialisationContext.predeterminedPath(this.determineAnnotationPath(
                    context,
                    methodContext,
                    new PersistenceAnnotationContext(annotation))
            );
            return true;
        }
        else if (methodContext.method().getParameterCount() == 1) {
            final Class<?> parameterType = methodContext.method().getParameterTypes()[0];

            if (Reflect.assigns(String.class, parameterType)) {
                deserialisationContext.target(SerialisationTarget.STRING);
                return true;

            }
            else if (Reflect.assigns(Path.class, parameterType) || Reflect.assigns(File.class, parameterType)) {
                deserialisationContext.target(SerialisationTarget.PARAMETER_PATH);
                return true;
            }
        }
        return false;
    }

    private Class<?> outputType(MethodProxyContext<?> context) {
        final Class<?> returnType = context.returnType();
        if (Reflect.assigns(Exceptional.class, returnType)) {
            final Exceptional<Type> typeExceptional = Reflect.typeParameter(returnType, 0);
            if (typeExceptional.absent()) return null;
            else {
                final Type type = typeExceptional.get();
                if (type instanceof Class) return (Class<?>) type;
                else return null;
            }
        } else {
            return returnType;
        }
    }

    @Override
    public Class<Deserialise> annotation() {
        return Deserialise.class;
    }
}
