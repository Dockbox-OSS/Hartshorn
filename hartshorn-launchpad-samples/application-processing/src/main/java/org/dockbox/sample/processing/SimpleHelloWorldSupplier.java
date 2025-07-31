package org.dockbox.sample.processing;

public class SimpleHelloWorldSupplier implements HelloWorldSupplier {

    private String message;

    public String message() {
        return this.message;
    }

    public void message(String message) {
        this.message = message;
    }

    @Override
    public String getHelloWorldMessage() {
        return this.message != null ? this.message : "Oops, didn't set a message!";
    }
}
