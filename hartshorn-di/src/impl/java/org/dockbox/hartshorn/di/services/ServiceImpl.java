package org.dockbox.hartshorn.di.services;

import org.dockbox.hartshorn.di.annotations.service.Service;

import java.lang.annotation.Annotation;

public class ServiceImpl implements Service {
    @Override
    public String id() {
        return "";
    }

    @Override
    public String name() {
        return "";
    }

    @Override
    public boolean enabled() {
        return true;
    }

    @Override
    public Class<?> owner() {
        return Service.class;
    }

    @Override
    public boolean singleton() {
        return false;
    }

    @Override
    public Class<? extends Annotation>[] activators() {
        return new Class[0];
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return Service.class;
    }
}
