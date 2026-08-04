package org.dockbox.hartshorn.web.error;

import org.dockbox.hartshorn.util.option.Option;

public interface ErrorHandlerRegistry {

    <E extends Throwable> void register(
            Class<E> exceptionType,
            RequestErrorHandler<E> handler
    );

    Option<RequestErrorHandler<? extends Throwable>> getHandler(
            Class<? extends Throwable> exceptionType
    );
}
