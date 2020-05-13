package com.darwinreforged.server.modules.extensions.chat.dave;

import java.util.List;

/**
 The type Dave trigger.
 */
public class DaveTrigger {

    private List<String> trigger;
    private boolean important;
    private List<Response> responses;

    /**
     Instantiates a new Dave trigger.
     */
    public DaveTrigger() {
    }

    /**
     Instantiates a new Dave trigger.

     @param trigger
     the trigger
     @param important
     the important
     @param responses
     the responses
     */
    public DaveTrigger(List<String> trigger, boolean important, List<Response> responses) {
        this.trigger = trigger;
        this.important = important;
        this.responses = responses;
    }

    /**
     Gets trigger.

     @return the trigger
     */
    public List<String> getTrigger() {
        return trigger;
    }

    /**
     Is important boolean.

     @return the boolean
     */
    public boolean isImportant() {
        return important;
    }

    /**
     Gets responses.

     @return the responses
     */
    public List<Response> getResponses() {
        return responses;
    }

    /**
     The type Response.
     */
    public static class Response {
        private String message;
        private String type;

        /**
         Instantiates a new Response.
         */
        public Response() {
        }

        /**
         Instantiates a new Response.

         @param message
         the message
         @param type
         the type
         */
        public Response(String message, String type) {
            this.message = message;
            this.type = type;
        }

        /**
         Gets message.

         @return the message
         */
        public String getMessage() {
            return message;
        }

        /**
         Gets type.

         @return the type
         */
        public String getType() {
            return type;
        }
    }

}
