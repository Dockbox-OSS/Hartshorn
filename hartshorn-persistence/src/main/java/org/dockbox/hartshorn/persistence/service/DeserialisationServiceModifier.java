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
import org.dockbox.hartshorn.di.context.element.TypeContext;
import org.dockbox.hartshorn.persistence.annotations.Deserialise;
import org.dockbox.hartshorn.persistence.context.DeserialisationContext;
import org.dockbox.hartshorn.persistence.context.PersistenceAnnotationContext;
import org.dockbox.hartshorn.persistence.context.SerialisationTarget;
import org.dockbox.hartshorn.persistence.mapping.ObjectMapper;
import org.dockbox.hartshorn.proxy.handle.ProxyFunction;
import org.dockbox.hartshorn.proxy.service.MethodProxyContext;

import java.io.File;
import java.nio.file.Path;

public class DeserialisationServiceModifier extends AbstractPersistenceServiceModifier<Deserialise, DeserialisationContext> {

    @Override
    protected <T, R> ProxyFunction<T, R> processAnnotatedPath(final ApplicationContext context, final MethodProxyContext<T> methodContext, final DeserialisationContext serialisationContext) {
        return (instance, args, proxyContext) -> {
            final Path path = serialisationContext.predeterminedPath();
            final ObjectMapper objectMapper = this.mapper(context, serialisationContext);

            final Exceptional<?> result = objectMapper.read(path, serialisationContext.type());
            return this.wrapResult(result, methodContext);
        };
    }

    @Override
    protected <T, R> ProxyFunction<T, R> processParameterPath(final ApplicationContext context, final MethodProxyContext<T> methodContext, final DeserialisationContext serialisationContext) {
        return (instance, args, proxyContext) -> {
            final Path path;
            if (args[0] instanceof Path) path = (Path) args[0];
            else if (args[0] instanceof File) path = ((File) args[0]).toPath();
            else throw new IllegalArgumentException("Expected one argument to be a subtype of File or Path");

            final ObjectMapper objectMapper = this.mapper(context, serialisationContext);

            final Exceptional<?> result = objectMapper.read(path, serialisationContext.type());
            return this.wrapResult(result, methodContext);
        };
    }

    @Override
    protected <T, R> ProxyFunction<T, R> processString(final ApplicationContext context, final MethodProxyContext<T> methodContext, final DeserialisationContext serialisationContext) {
        return (instance, args, proxyContext) -> {
            final String raw = (String) args[0];
            final ObjectMapper objectMapper = this.mapper(context, serialisationContext);

            final Exceptional<?> result = objectMapper.read(raw, serialisationContext.type());
            return this.wrapResult(result, methodContext);
        };
    }

    @Override
    protected Class<DeserialisationContext> contextType() {
        return DeserialisationContext.class;
    }

    @SuppressWarnings("unchecked")
    private <R> R wrapResult(final Exceptional<?> result, final MethodProxyContext<?> methodContext) {
        if (methodContext.method().returnType().childOf(Exceptional.class)) return (R) result;
        else return (R) result.orNull();
    }

    @Override
    public <T> boolean preconditions(final ApplicationContext context, final MethodProxyContext<T> methodContext) {
        if (methodContext.method().parameterCount() > 1) return false;
        if (methodContext.method().returnType().isVoid()) return false;

        final Deserialise annotation = methodContext.annotation(Deserialise.class);

        final TypeContext<?> outputType = this.outputType(methodContext);
        if (outputType == null) return false;

        final DeserialisationContext deserialisationContext = new DeserialisationContext(outputType);
        deserialisationContext.fileType(annotation.filetype());
        methodContext.add(deserialisationContext);

        if (methodContext.method().parameterCount() == 0) {
            deserialisationContext.target(SerialisationTarget.ANNOTATED_PATH);
            deserialisationContext.predeterminedPath(this.determineAnnotationPath(
                    context,
                    methodContext,
                    new PersistenceAnnotationContext(annotation))
            );
            return true;
        }
        else if (methodContext.method().parameterCount() == 1) {
            final TypeContext<?> parameterType = methodContext.method().parameterTypes().get(0);

            if (parameterType.childOf(String.class)) {
                deserialisationContext.target(SerialisationTarget.STRING);
                return true;
            }
            else if (parameterType.childOf(Path.class) || parameterType.childOf(File.class)) {
                deserialisationContext.target(SerialisationTarget.PARAMETER_PATH);
                return true;
            }
        }
        return false;
    }

    private TypeContext<?> outputType(final MethodProxyContext<?> context) {
        final TypeContext<?> returnType = context.method().returnType();
        if (returnType.childOf(Exceptional.class)) {
            return returnType.typeParameters().get(0);
        }
        else {
            return returnType;
        }
    }

    @Override
    public Class<Deserialise> annotation() {
        return Deserialise.class;
    }
}
