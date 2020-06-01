package com.darwinreforged.server.modules.dave;

import java.util.List;

/**
 The type Dave trigger.
 */
public class DaveTrigger {

    private String id;
    private List<String> trigger;
    private boolean important;
    private List<Response> responses;
    private String permission = null;

    /**
     Instantiates a new Dave trigger.
     */
    public DaveTrigger() {
    }

    /**
     Instantiates a new Dave trigger.@param id
     @param trigger
     the trigger
     @param important
 the important
     @param responses
the responses


     */
    public DaveTrigger(String id, List<String> trigger, boolean important, List<Response> responses, String permission) {
        this.id = id;
        this.trigger = trigger;
        this.important = important;
        this.responses = responses;
        this.permission = permission;
    }

    /**
     Gets id.

     @return the id
     */
    public String getId() {
        return id;
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

    public String getPermission() {
        return permission;
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
