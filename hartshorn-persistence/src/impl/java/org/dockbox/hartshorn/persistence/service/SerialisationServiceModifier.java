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

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.di.services.ComponentContainer;
import org.dockbox.hartshorn.persistence.PersistenceType;
import org.dockbox.hartshorn.persistence.annotations.Serialise;
import org.dockbox.hartshorn.persistence.mapping.ObjectMapper;
import org.dockbox.hartshorn.proxy.handle.ProxyFunction;
import org.dockbox.hartshorn.proxy.service.MethodProxyContext;
import org.dockbox.hartshorn.util.Reflect;

import java.io.File;
import java.lang.reflect.Type;
import java.nio.file.Path;

public class SerialisationServiceModifier extends AbstractPersistenceServiceModifier<Serialise, SerialisationContext> {

    @Override
    protected <T, R> ProxyFunction<T, R> processAnnotatedPath(ApplicationContext context, MethodProxyContext<T> methodContext, SerialisationContext serialisationContext) {
        return (instance, args, proxyContext) -> {
            final Path target = serialisationContext.predeterminedPath();
            final Object content = args[0];
            final ObjectMapper objectMapper = this.mapper(context, serialisationContext);
            final Exceptional<Boolean> result = objectMapper.write(target, content);
            return this.wrapBooleanResult(result, methodContext);
        };
    }

    @Override
    protected <T, R> ProxyFunction<T, R> processParameterPath(ApplicationContext context, MethodProxyContext<T> methodContext, SerialisationContext serialisationContext) {
        return (instance, args, proxyContext) -> {
            Path target = null;
            Object content = null;
            for (Object arg : args) {
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
    @SuppressWarnings("unchecked")
    protected <T, R> ProxyFunction<T, R> processString(ApplicationContext context, MethodProxyContext<T> methodContext, SerialisationContext serialisationContext) {
        return (instance, args, proxyContext) -> {
            final Object content = args[0];
            final ObjectMapper objectMapper = this.mapper(context, serialisationContext);

            final Exceptional<String> result = objectMapper.write(content);
            if (Reflect.assigns(String.class, methodContext.returnType())) {
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

    @SuppressWarnings("unchecked")
    private <R> R wrapBooleanResult(Exceptional<Boolean> result, MethodProxyContext<?> methodContext) {
        if (Reflect.assigns(Boolean.class, methodContext.returnType())) {
            return (R) result.or(false);
        }
        else {
            return (R) result;
        }
    }

    @Override
    public <T> boolean preconditions(ApplicationContext context, MethodProxyContext<T> methodContext) {
        if (methodContext.method().getParameterCount() < 1) return false;

        final Serialise annotation = methodContext.annotation(Serialise.class);
        if (!annotation.filetype().type().equals(PersistenceType.RAW)) return false;

        boolean hasPath = false;
        for (Class<?> parameter : methodContext.method().getParameterTypes()) {
            if (Reflect.assigns(Path.class, parameter) || Reflect.assigns(File.class, parameter)) {
                hasPath = true;
                break;
            }
        }

        SerialisationContext serialisationContext = new SerialisationContext();
        serialisationContext.fileType(annotation.filetype());
        methodContext.add(serialisationContext);

        if (hasPath) {
            serialisationContext.target(SerialisationTarget.PARAMETER_PATH);
            return methodContext.method().getParameterCount() == 2;
        }
        else {
            if (Reflect.notVoid(this.owner(annotation.value().owner(), methodContext))) {
                serialisationContext.target(SerialisationTarget.ANNOTATED_PATH);
                serialisationContext.predeterminedPath(this.determineAnnotationPath(
                        context,
                        methodContext,
                        new PersistenceAnnotationContext(annotation))
                );
                return methodContext.method().getParameterCount() == 1;
            }
            else {
                serialisationContext.target(SerialisationTarget.STRING);
                return this.stringTargetPreconditions(methodContext);
            }
        }
    }

    private Class<?> owner(Class<?> annotationOwner, MethodProxyContext<?> context) {
        if (Reflect.notVoid(annotationOwner)) return annotationOwner;
        return Hartshorn.context().locator().container(context.method().getDeclaringClass()).map(ComponentContainer::owner).orNull();
    }

    private boolean argumentPathTargetPreconditions(MethodProxyContext<?> context) {
        // One argument should be the content, one should be a Path/File subtype. The presence of the latter
        // has already been verified before.
        if (context.method().getParameterCount() != 2) return false;

        final Class<?> returnType = context.returnType();
        if (Reflect.assigns(boolean.class, returnType)) return true;

        else if (Reflect.assigns(Exceptional.class, returnType)) {
            final Exceptional<Type> type = Reflect.typeParameter(returnType, 0);

            if (type.present()) {
                final Type generic = type.get();

                if (generic instanceof Class) {
                    return Reflect.assigns(Boolean.class, (Class<?>) generic);
                }
            }
        }

        return false;
    }

    private boolean stringTargetPreconditions(MethodProxyContext<?> context) {
        final Class<?> returnType = context.returnType();

        if (Reflect.assigns(String.class, returnType)) return true;

        else if (Reflect.assigns(Exceptional.class, returnType)) {
            final Exceptional<Type> type = Reflect.typeParameter(returnType, 0);

            if (type.present()) {
                final Type generic = type.get();

                if (generic instanceof Class) {
                    return Reflect.assigns(String.class, (Class<?>) generic);
                }
            }
        }

        return false;
    }

    @Override
    public Class<Serialise> annotation() {
        return Serialise.class;
    }
}
