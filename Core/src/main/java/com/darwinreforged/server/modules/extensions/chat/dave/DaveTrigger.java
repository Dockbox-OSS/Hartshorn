package com.darwinreforged.server.modules.extensions.chat.dave;

import java.util.List;

public class DaveTrigger {

    private List<String> trigger;
    private boolean important;
    private List<Response> responses;

    public DaveTrigger() {
    }

    public DaveTrigger(List<String> trigger, boolean important, List<Response> responses) {
        this.trigger = trigger;
        this.important = important;
        this.responses = responses;
    }

    public List<String> getTrigger() {
        return trigger;
    }

    public boolean isImportant() {
        return important;
    }

    public List<Response> getResponses() {
        return responses;
    }

    public static class Response {
        private String message;
        private String type;

        public Response() {
        }

        public Response(String message, String type) {
            this.message = message;
            this.type = type;
        }

        public String getMessage() {
            return message;
        }

        public String getType() {
            return type;
        }
    }

}
