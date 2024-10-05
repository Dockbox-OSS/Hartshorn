package org.dockbox.hartshorn.inject;

import java.lang.reflect.InvocationTargetException;

import org.dockbox.hartshorn.util.ApplicationRuntimeException;

public class ReflectionObjectFactory implements ObjectFactory {

    @Override
    public <T> T create(Class<T> type) {
        try {
            return type.getConstructor().newInstance();
        }
        catch(InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException |
              NoSuchMethodException | SecurityException e) {
            throw new ApplicationRuntimeException(e);
        }
    }
}
