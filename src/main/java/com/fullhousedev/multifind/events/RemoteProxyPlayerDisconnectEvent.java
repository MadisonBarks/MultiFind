package com.fullhousedev.multifind.events;

import net.md_5.bungee.api.plugin.Event;

/**
 * This is a description of the class.
 *
 * @author Austin Bolstridge (WolfyTheFur).
 */
public class RemoteProxyPlayerDisconnectEvent extends Event {

    private String username;

    public RemoteProxyPlayerDisconnectEvent(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

}
