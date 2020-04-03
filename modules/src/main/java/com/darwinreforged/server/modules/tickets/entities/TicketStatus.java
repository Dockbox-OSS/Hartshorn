package com.darwinreforged.server.modules.tickets.entities;


public enum TicketStatus {
    Open("Open"), Claimed("Claimed"), Held("Held"), Closed("Closed");

    private String status;

    TicketStatus(String stat) {
        this.status = stat;
    }

    @Override
    public String toString() {
        return status;
    }
}
