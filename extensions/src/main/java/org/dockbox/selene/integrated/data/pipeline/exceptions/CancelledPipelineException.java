package org.dockbox.selene.integrated.data.pipeline.exceptions;

public class CancelledPipelineException extends Exception {

    public CancelledPipelineException(String errorMessage) {
        super(errorMessage);
    }
}
