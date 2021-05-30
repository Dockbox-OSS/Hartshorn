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
import org.dockbox.selene.persistence.PersistenceType;
import org.dockbox.selene.persistence.annotations.Serialise;
import org.dockbox.selene.persistence.annotations.UsePersistence;
import org.dockbox.selene.persistence.mapping.ObjectMapper;
import org.dockbox.selene.proxy.handle.ProxyFunction;
import org.dockbox.selene.proxy.service.MethodProxyContext;
import org.dockbox.selene.proxy.service.ServiceAnnotatedMethodModifier;
import org.dockbox.selene.util.Reflect;

import java.io.File;
import java.lang.reflect.Type;
import java.nio.file.Path;

public class SerialisationServiceModifier extends ServiceAnnotatedMethodModifier<Serialise, UsePersistence> {

    @Override
    public Class<UsePersistence> activator() {
        return UsePersistence.class;
    }

    @Override
    public <T, R> ProxyFunction<T, R> process(ApplicationContext context, MethodProxyContext<T> methodContext) {
        final Exceptional<SerialisationContext> serialisationContext = methodContext.first(SerialisationContext.class);
        if (serialisationContext.absent()) throw new IllegalStateException("Expected additional context to be present");

        final SerialisationContext ctx = serialisationContext.get();
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

    private <T, R> ProxyFunction<T, R> processAnnotatedPath(ApplicationContext context, MethodProxyContext<T> methodContext, SerialisationContext serialisationContext) {
        return (instance, args, proxyContext) -> {
            final Path target = serialisationContext.getPredeterminedPath();
            final Object content = args[0];
            final ObjectMapper objectMapper = this.getObjectMapper(context, serialisationContext);
            final Exceptional<Boolean> result = objectMapper.write(target, content);
            return this.wrapBooleanResult(result, methodContext);
        };
    }

    private <T, R> ProxyFunction<T, R> processParameterPath(ApplicationContext context, MethodProxyContext<T> methodContext, SerialisationContext serialisationContext) {
        return (instance, args, proxyContext) -> {
            Path target = null;
            Object content = null;
            for (Object arg : args) {
                if (arg instanceof Path) target = (Path) arg;
                else if (arg instanceof File) target = ((File) arg).toPath();
                else content = arg;
            }

            if (target == null || content == null) throw new IllegalArgumentException("Expected one argument to be a subtype of File or Path, expected one argument to be a content type");

            final ObjectMapper objectMapper = this.getObjectMapper(context, serialisationContext);
            final Exceptional<Boolean> result = objectMapper.write(target, content);
            return this.wrapBooleanResult(result, methodContext);
        };
    }

    @SuppressWarnings("unchecked")
    private <R> R wrapBooleanResult(Exceptional<Boolean> result, MethodProxyContext<?> methodContext) {
        if (Reflect.assignableFrom(Boolean.class, methodContext.getReturnType())) {
            return (R) result.or(false);
        } else {
            return (R) result;
        }
    }

    @SuppressWarnings("unchecked")
    private <T, R> ProxyFunction<T, R> processString(ApplicationContext context, MethodProxyContext<T> methodContext, SerialisationContext serialisationContext) {
        return (instance, args, proxyContext) -> {
            final Object content = args[0];
            final ObjectMapper objectMapper = this.getObjectMapper(context, serialisationContext);

            final Exceptional<String> result = objectMapper.write(content);
            if (Reflect.assignableFrom(String.class, methodContext.getReturnType())) {
                return (R) result.orNull();
            } else {
                return (R) result;
            }
        };
    }

    private ObjectMapper getObjectMapper(ApplicationContext context, SerialisationContext serialisationContext) {
        final ObjectMapper objectMapper = context.get(ObjectMapper.class);
        final FileType fileType = serialisationContext.getFileType();
        objectMapper.setFileType(fileType);
        return objectMapper;
    }

    @Override
    public <T> boolean preconditions(ApplicationContext context, MethodProxyContext<T> methodContext) {
        if (methodContext.getMethod().getParameterCount() < 1) return false;

        final Serialise annotation = methodContext.getAnnotation(Serialise.class);
        if (!annotation.filetype().getType().equals(PersistenceType.RAW)) return false;

        boolean hasPath = false;
        for (Class<?> parameter : methodContext.getMethod().getParameterTypes()) {
            if (Reflect.assignableFrom(Path.class, parameter) || Reflect.assignableFrom(File.class, parameter)) {
                hasPath = true;
                break;
            }
        }

        SerialisationContext serialisationContext = new SerialisationContext();
        serialisationContext.setFileType(annotation.filetype());
        methodContext.add(serialisationContext);

        if (hasPath) {
            serialisationContext.setTarget(SerialisationTarget.ANNOTATED_PATH);
            serialisationContext.setPredeterminedPath(this.determineAnnotationPath(context, methodContext));
            return this.argumentPathTargetPreconditions(methodContext);

        } else {
            if (Reflect.isNotVoid(annotation.value().owner())) {
                serialisationContext.setTarget(SerialisationTarget.PARAMETER_PATH);
                return methodContext.getMethod().getParameterCount() == 1;

            } else {
                serialisationContext.setTarget(SerialisationTarget.STRING);
                return this.stringTargetPreconditions(methodContext);
            }
        }
    }

    private Path determineAnnotationPath(ApplicationContext context, MethodProxyContext<?> methodContext) {
        final Serialise annotation = methodContext.getAnnotation(Serialise.class);
        final org.dockbox.selene.persistence.annotations.File file = annotation.value();
        Class<?> owner = file.owner();

        if (!Reflect.isNotVoid(owner)) {
            final Service service = methodContext.getMethod().getDeclaringClass().getAnnotation(Service.class);
            owner = service.owner();
        }

        final TypedOwner lookup = context.get(OwnerLookup.class).lookup(owner);
        final FileManager fileManager = context.get(FileManager.class);

        if ("".equals(file.file())) return fileManager.getDataFile(lookup);
        else return fileManager.getDataFile(lookup, file.file());
    }

    private boolean argumentPathTargetPreconditions(MethodProxyContext<?> context) {
        // One argument should be the content, one should be a Path/File subtype. The presence of the latter
        // has already been verified before.
        if (context.getMethod().getParameterCount() != 2) return false;

        final Class<?> returnType = context.getReturnType();
        if (Reflect.assignableFrom(boolean.class, returnType)) return true;

        else if (Reflect.assignableFrom(Exceptional.class, returnType)) {
            final Exceptional<Type> type = Reflect.genericTypeParameter(returnType, 0);

            if (type.present()) {
                final Type generic = type.get();

                if (generic instanceof Class) {
                    return Reflect.assignableFrom(Boolean.class, (Class<?>) generic);
                }
            }
        }

        return false;
    }

    private boolean stringTargetPreconditions(MethodProxyContext<?> context) {
        final Class<?> returnType = context.getReturnType();

        if (Reflect.assignableFrom(String.class, returnType)) return true;

        else if (Reflect.assignableFrom(Exceptional.class, returnType)) {
            final Exceptional<Type> type = Reflect.genericTypeParameter(returnType, 0);

            if (type.present()) {
                final Type generic = type.get();

                if (generic instanceof Class) {
                    return Reflect.assignableFrom(String.class, (Class<?>) generic);
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
