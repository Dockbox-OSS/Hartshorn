package org.dockbox.hartshorn.web.route.response;

public abstract class AbstractResponseMessageConverter<T> implements ResponseMessageConverter<T> {

    private final Class<T> supportedType;

    protected AbstractResponseMessageConverter(Class<T> supportedType) {
        this.supportedType = supportedType;
    }

    @Override
    public boolean supports(Class<?> type) {
        return this.supportedType.isAssignableFrom(type);
    }
}
