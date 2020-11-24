package org.dockbox.selene.integrated.data.pipeline.exceptions;

public class IllegalPipelineConverterException extends Exception {

    public IllegalPipelineConverterException(String errorMessage, Throwable throwable) {
        super(errorMessage, throwable);
    }

    public IllegalPipelineConverterException(String errorMessage) {
        super(errorMessage);
    }
}
