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
import org.dockbox.selene.di.context.ApplicationContext;
import org.dockbox.selene.persistence.annotations.Deserialise;
import org.dockbox.selene.persistence.mapping.ObjectMapper;
import org.dockbox.selene.proxy.handle.ProxyFunction;
import org.dockbox.selene.proxy.service.MethodProxyContext;
import org.dockbox.selene.util.Reflect;

import java.io.File;
import java.lang.reflect.Type;
import java.nio.file.Path;

public class DeserialisationServiceModifier extends AbstractPersistenceServiceModifier<Deserialise, DeserialisationContext> {

    @Override
    protected <T, R> ProxyFunction<T, R> processAnnotatedPath(ApplicationContext context, MethodProxyContext<T> methodContext, DeserialisationContext serialisationContext) {
        return (instance, args, proxyContext) -> {
            final Path path = serialisationContext.getPredeterminedPath();
            final ObjectMapper objectMapper = this.getObjectMapper(context, serialisationContext);

            final Exceptional<?> result = objectMapper.read(path, serialisationContext.getType());
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

            final ObjectMapper objectMapper = this.getObjectMapper(context, serialisationContext);

            final Exceptional<?> result = objectMapper.read(path, serialisationContext.getType());
            return this.wrapResult(result, methodContext);
        };
    }

    @Override
    protected <T, R> ProxyFunction<T, R> processString(ApplicationContext context, MethodProxyContext<T> methodContext, DeserialisationContext serialisationContext) {
        return (instance, args, proxyContext) -> {
            final String raw = (String) args[0];
            final ObjectMapper objectMapper = this.getObjectMapper(context, serialisationContext);

            final Exceptional<?> result = objectMapper.read(raw, serialisationContext.getType());
            return this.wrapResult(result, methodContext);
        };
    }

    @SuppressWarnings("unchecked")
    private <R> R wrapResult(Exceptional<?> result, MethodProxyContext<?> methodContext) {
        if (Reflect.assignableFrom(Exceptional.class, methodContext.getReturnType())) return (R) result;
        else return (R) result.orNull();
    }

    @Override
    protected Class<DeserialisationContext> getContextType() {
        return DeserialisationContext.class;
    }

    @Override
    public <T> boolean preconditions(ApplicationContext context, MethodProxyContext<T> methodContext) {
        if (methodContext.getMethod().getParameterCount() > 1) return false;
        if (!Reflect.isNotVoid(methodContext.getReturnType())) return false;

        final Deserialise annotation = methodContext.getAnnotation(Deserialise.class);

        final Class<?> outputType = this.getOutputType(methodContext);
        if (outputType == null) return false;

        DeserialisationContext deserialisationContext = new DeserialisationContext(outputType);
        deserialisationContext.setFileType(annotation.filetype());
        methodContext.add(deserialisationContext);

        if (methodContext.getMethod().getParameterCount() == 0) {
            deserialisationContext.setTarget(SerialisationTarget.ANNOTATED_PATH);
            deserialisationContext.setPredeterminedPath(this.determineAnnotationPath(
                    context,
                    methodContext,
                    new PersistenceAnnotationContext(annotation))
            );
            return true;
        }
        else if (methodContext.getMethod().getParameterCount() == 1) {
            final Class<?> parameterType = methodContext.getMethod().getParameterTypes()[0];

            if (Reflect.assignableFrom(String.class, parameterType)) {
                deserialisationContext.setTarget(SerialisationTarget.STRING);
                return true;

            }
            else if (Reflect.assignableFrom(Path.class, parameterType) || Reflect.assignableFrom(File.class, parameterType)) {
                deserialisationContext.setTarget(SerialisationTarget.PARAMETER_PATH);
                return true;
            }
        }
        return false;
    }

    private Class<?> getOutputType(MethodProxyContext<?> context) {
        final Class<?> returnType = context.getReturnType();
        if (Reflect.assignableFrom(Exceptional.class, returnType)) {
            final Exceptional<Type> typeExceptional = Reflect.genericTypeParameter(returnType, 0);
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
