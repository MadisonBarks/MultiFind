package com.fullhousedev.multifind.socket;

import com.fullhousedev.multifind.MultiFind;
import com.fullhousedev.multifind.events.RemoteProxyPlayerDisconnectEvent;
import com.fullhousedev.multifind.events.RemoteProxyPlayerLoginEvent;
import com.fullhousedev.multifind.events.RemoteProxyServerSwitchEvent;
import net.md_5.bungee.api.plugin.PluginLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

/**
 * This is a description of the class.
 *
 * @author Austin Bolstridge (WolfyTheFur).
 */
public class SocketController implements Runnable {

    private int delay;
    private ProxyServer[] servers;
    private MultiFind plugin;

    public SocketController(int delay, MultiFind plugin, ProxyServer... servers) {
        this.delay = delay;
        this.servers = servers;
        this.plugin = plugin;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(delay * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        servers = this.checkServers(servers);

        for(final ProxyServer server : servers) {
            if(server == null) {
                continue;
            }
            plugin.getProxyManager().registerServer(server);
            if(!server.setupSocket()) {
                plugin.getProxyManager().deregisterServer(server);
                PluginLogger.getLogger("MultiFind").info("Proxy server at " + server.getAddress().toString() +
                        " is not responding. No longer trying to communicate with it.");
                continue;
            }
            plugin.getProxy().getScheduler().runAsync(plugin, new ProxyRunnable(server, plugin));
        }

    }

    private ProxyServer[] checkServers(ProxyServer[] servers) {
        for(int i = 0; i < servers.length; i++) {
            if(plugin.getProxyManager().isServerRegistered(servers[i])) {
                servers[i] = null;
            }
        }

        for(int i = 1; i < servers.length; i++) {
            if(servers[i] != null && servers[i - 1] == null) {
                servers[i - 1] = servers[i];
            }
        }

        int size = 0;
        for(int i = 0; i < servers.length; i++) {
            if(servers[i] == null) {
                size = i + 1;
                break;
            }
        }

        if(size == 0) {
            return servers;
        }
        ProxyServer[] newServerArray = new ProxyServer[size];
        for(int i = 0; i < newServerArray.length; i++) {
            newServerArray[i] = servers[i];
        }

        return newServerArray;
    }
}


