package org.dockbox.selene.integrated.data.pipeline.exceptions;

public class IllegalPipeException extends IllegalArgumentException {

    public IllegalPipeException(String errorMessage, Throwable throwable) {
        super(errorMessage, throwable);
    }

    public IllegalPipeException(String errorMessage) {
        super(errorMessage);
    }
}
