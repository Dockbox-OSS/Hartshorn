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

package org.dockbox.hartshorn.data.service;

import org.dockbox.hartshorn.core.annotations.service.AutomaticActivation;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.services.ComponentContainer;
import org.dockbox.hartshorn.data.DataStorageType;
import org.dockbox.hartshorn.data.annotations.Serialise;
import org.dockbox.hartshorn.data.context.PersistenceAnnotationContext;
import org.dockbox.hartshorn.data.context.SerialisationContext;
import org.dockbox.hartshorn.data.context.SerialisationTarget;
import org.dockbox.hartshorn.data.mapping.ObjectMapper;
import org.dockbox.hartshorn.core.proxy.ProxyFunction;
import org.dockbox.hartshorn.core.context.MethodProxyContext;

import java.io.File;
import java.nio.file.Path;

@AutomaticActivation
public class SerialisationServiceModifier extends AbstractPersistenceServiceModifier<Serialise, SerialisationContext> {

    @Override
    protected <T, R> ProxyFunction<T, R> processAnnotatedPath(final ApplicationContext context, final MethodProxyContext<T> methodContext, final SerialisationContext serialisationContext) {
        return (instance, args, proxyContext) -> {
            final Path target = serialisationContext.predeterminedPath();
            final Object content = args[0];
            final ObjectMapper objectMapper = this.mapper(context, serialisationContext);
            final Exceptional<Boolean> result = objectMapper.write(target, content);
            return this.wrapBooleanResult(result, methodContext);
        };
    }

    @Override
    protected <T, R> ProxyFunction<T, R> processParameterPath(final ApplicationContext context, final MethodProxyContext<T> methodContext, final SerialisationContext serialisationContext) {
        return (instance, args, proxyContext) -> {
            Path target = null;
            Object content = null;
            for (final Object arg : args) {
                if (arg instanceof Path) target = (Path) arg;
                else if (arg instanceof File) target = ((File) arg).toPath();
                else content = arg;
            }

            if (target == null || content == null) throw new IllegalArgumentException("Expected one argument to be a subtype of File or Path, expected one argument to be a content type");

            final ObjectMapper objectMapper = this.mapper(context, serialisationContext);
            final Exceptional<Boolean> result = objectMapper.write(target, content);
            return this.wrapBooleanResult(result, methodContext);
        };
    }

    @Override
    protected <T, R> ProxyFunction<T, R> processString(final ApplicationContext context, final MethodProxyContext<T> methodContext, final SerialisationContext serialisationContext) {
        return (instance, args, proxyContext) -> {
            final Object content = args[0];
            final ObjectMapper objectMapper = this.mapper(context, serialisationContext);

            final Exceptional<String> result = objectMapper.write(content);
            if (methodContext.method().returnType().childOf(String.class)) {
                return (R) result.orNull();
            }
            else {
                return (R) result;
            }
        };
    }

    @Override
    protected Class<SerialisationContext> contextType() {
        return SerialisationContext.class;
    }

    private <R> R wrapBooleanResult(final Exceptional<Boolean> result, final MethodProxyContext<?> methodContext) {
        if (methodContext.method().returnType().childOf(Boolean.class))
            return (R) result.or(false);
        else return (R) result;
    }

    @Override
    public <T> boolean preconditions(final ApplicationContext context, final MethodProxyContext<T> methodContext) {
        if (methodContext.method().parameterCount() < 1) return false;

        final Serialise annotation = methodContext.annotation(Serialise.class);
        if (!annotation.filetype().type().equals(DataStorageType.RAW)) return false;

        boolean hasPath = false;
        for (final TypeContext<?> parameter : methodContext.method().parameterTypes()) {
            if (parameter.childOf(Path.class) || parameter.childOf(File.class)) {
                hasPath = true;
                break;
            }
        }

        final SerialisationContext serialisationContext = new SerialisationContext();
        serialisationContext.fileFormat(annotation.filetype());
        methodContext.add(serialisationContext);

        if (hasPath) {
            serialisationContext.target(SerialisationTarget.PARAMETER_PATH);
            return methodContext.method().parameterCount() == 2;
        }
        else {
            final TypeContext<?> owner = this.owner(context, TypeContext.of(annotation.path().owner()), methodContext);
            if (!owner.isVoid()) {
                serialisationContext.target(SerialisationTarget.ANNOTATED_PATH);
                serialisationContext.predeterminedPath(this.determineAnnotationPath(
                        context,
                        methodContext,
                        new PersistenceAnnotationContext(annotation))
                );
                return methodContext.method().parameterCount() == 1;
            }
            else {
                serialisationContext.target(SerialisationTarget.STRING);
                return this.stringTargetPreconditions(methodContext);
            }
        }
    }

    private TypeContext<?> owner(final ApplicationContext context, final TypeContext<?> annotationOwner, final MethodProxyContext<?> methodContext) {
        if (!annotationOwner.isVoid()) return annotationOwner;
        return context.locator().container(methodContext.method().parent()).map(ComponentContainer::owner).orNull();
    }

    private boolean stringTargetPreconditions(final MethodProxyContext<?> context) {
        final TypeContext<?> returnType = context.method().returnType();

        if (returnType.childOf(String.class)) return true;

        else if (returnType.childOf(Exceptional.class)) {
            final TypeContext<?> type = returnType.typeParameters().get(0);

            if (!type.isVoid()) {
                return type.childOf(String.class);
            }
        }

        return false;
    }

    @Override
    public Class<Serialise> annotation() {
        return Serialise.class;
    }
}
