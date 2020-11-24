package org.dockbox.selene.integrated.data.pipeline.exceptions;

public class IllegalPipelineTypeException extends IllegalArgumentException {

    public IllegalPipelineTypeException(String errorMessage, Throwable throwable) {
        super(errorMessage, throwable);
    }

    public IllegalPipelineTypeException(String errorMessage) {
        super(errorMessage);
    }
}
