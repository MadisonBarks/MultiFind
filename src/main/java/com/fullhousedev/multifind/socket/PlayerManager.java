package com.fullhousedev.multifind.socket;

import com.fullhousedev.multifind.MultiFind;
import com.fullhousedev.multifind.events.RemoteProxyPlayerDisconnectEvent;
import com.fullhousedev.multifind.events.RemoteProxyPlayerLoginEvent;
import com.fullhousedev.multifind.events.RemoteProxyServerSwitchEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.HashMap;

/**
 * This is a description of the class.
 *
 * @author Austin Bolstridge (WolfyTheFur).
 */
public class PlayerManager implements Listener {

    private HashMap<String, String> players = new HashMap<>();

    private MultiFind plugin;

    public PlayerManager(MultiFind plugin) {
        this.plugin = plugin;
    }

    public void onPlayerLogin(String username, String server) {
        players.put(username.toLowerCase(), server);
    }

    public void onPlayerSwitchServers(String username, String server) {
        players.put(username.toLowerCase(), server);
    }

    public void onPlayerDisconnect(String username) {
        players.remove(username.toLowerCase());
    }


    @EventHandler
    public void onRemoteProxyPlayerLogin(RemoteProxyPlayerLoginEvent event) {
        this.onPlayerLogin(event.getUsername(), event.getServer());
    }

    @EventHandler
    public void onRemoteProxyPlayerDisconnect(RemoteProxyPlayerDisconnectEvent event) {
        this.onPlayerDisconnect(event.getUsername());
    }

    @EventHandler
    public void onRemoteProxyServerSwitch(RemoteProxyServerSwitchEvent event) {
        this.onPlayerSwitchServers(event.getUsername(), event.getServer());
    }

    @EventHandler
    public void onLocalPlayerLogin(ServerConnectedEvent event) {
        this.onPlayerLogin(event.getPlayer().getName(), event.getServer().getInfo().getName());
        plugin.getProxyManager().sendToAll("PlayerLogin:" + event.getPlayer().getName() + ":" +
                event.getServer().getInfo().getName());
    }

    @EventHandler
    public void onLocalPlayerDisconnect(PlayerDisconnectEvent event) {
        this.onPlayerDisconnect(event.getPlayer().getName());
        plugin.getProxyManager().sendToAll("PlayerDisconnect:" + event.getPlayer().getName());
    }

    @EventHandler
    public void onLocalServerSwitch(ServerSwitchEvent event) {
        this.onPlayerSwitchServers(event.getPlayer().getName(), event.getPlayer().getServer().getInfo().getName());
        plugin.getProxyManager().sendToAll("PlayerSwitchServer:" + event.getPlayer().getName() + ":" +
                event.getPlayer().getServer().getInfo().getName());
    }

    public boolean isPlayerLoggedIn(String username) {
        return players.containsKey(username.toLowerCase());
    }

    public String getServer(String username) {
        return players.get(username.toLowerCase());
    }

}
