package com.fullhousedev.multifind.socket;

import com.fullhousedev.multifind.MultiFind;
import com.fullhousedev.multifind.events.RemoteProxyPlayerDisconnectEvent;
import com.fullhousedev.multifind.events.RemoteProxyPlayerLoginEvent;
import com.fullhousedev.multifind.events.RemoteProxyServerSwitchEvent;
import com.fullhousedev.multifind.socket.ProxyServer;
import net.md_5.bungee.api.plugin.PluginLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;

public class ProxyRunnable implements Runnable {

    private ProxyServer server;
    private MultiFind plugin;

    public ProxyRunnable(ProxyServer server, MultiFind plugin) {
        this.server = server;
        this.plugin = plugin;
    }

    @Override
    public void run() {
        Socket socket = server.getSocket();
        BufferedReader reader = server.getReader();

        while(true) {
            if(!socket.isClosed() || !socket.isConnected() || socket == null) {
                PluginLogger.getLogger("MultiFind").info("Proxy server at " + server.getAddress().toString() +
                        " has gone down. No longer trying to communicate with it.");
                break;
            }
            String message;
            try {
                message = reader.readLine();
            } catch (IOException e) {
                PluginLogger.getLogger("MultiFind").warning("Something happened while reading from a proxy." +
                        "Dropping this proxy from rotation.");
                try {
                    socket.close();
                } catch (IOException e1) {
                    PluginLogger.getLogger("MultiFind").warning("Something happened while closing the connection" +
                            " to a proxy. Abandoning the socket.");
                    break;
                }
                break;
            }

            String[] messageParts = message.split(":");
            switch(messageParts[0]) {
                case "PlayerLogin":
                    String username = messageParts[1];
                    String server = messageParts[2];

                    plugin.getProxy().getPluginManager().callEvent(new RemoteProxyPlayerLoginEvent(username, server));
                    break;
                case "PlayerSwitchServer":
                    //-_-
                    //Fuck you Java.
                    username = messageParts[1];
                    server = messageParts[2];

                    plugin.getProxy().getPluginManager().callEvent(new RemoteProxyServerSwitchEvent(username, server));
                    break;
                case "PlayerDisconnect":
                    username = messageParts[1];

                    plugin.getProxy().getPluginManager().callEvent(new RemoteProxyPlayerDisconnectEvent(username));
                    break;
            }
        }
    }
}