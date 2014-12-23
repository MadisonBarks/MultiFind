package com.fullhousedev.multifind.socket;

import com.fullhousedev.multifind.MultiFind;
import net.md_5.bungee.api.plugin.PluginLogger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * This is a description of the class.
 *
 * @author Austin Bolstridge (WolfyTheFur).
 */
public class SocketServerController implements Runnable{

    private MultiFind plugin;

    private int port;

    private ProxyServer[] servers;

    public SocketServerController(MultiFind plugin, int port, ProxyServer... servers) {
        this.plugin = plugin;
        this.port = port;

        int i = 0;
        this.servers = servers;
    }

    public void handleConnections() throws IOException {
        ServerSocket socket = new ServerSocket(port);

        while(true) {
            Socket connection = socket.accept();
            if(!isAddressAllowed(connection.getInetAddress())) {
                PluginLogger.getLogger("MultiFind").warning("Host located at address " + connection.getInetAddress().toString()
                    + " attempted to connect, but was rejected due to it not being a registered proxy.");
                connection.close();
                continue;
            }
            //At this point, we now have an idea that it's authenticated and allowed.

            //first thing we really need to do is check if we're already connected to these peeps.
            if(plugin.getProxyManager().isServerRegistered(connection.getInetAddress())) {
                //We're already connected to this server.
                //For now, we will close the connection, and forget all about it.
                //In the future, we may have a quick conversation with the server to see who should connect.
                connection.close();
                continue;
            }

            //So, if we get here, then the connection is fine.
            for(ProxyServer server : servers) {
                if(server.getAddress().equals(connection.getInetAddress())) {
                    server.setSocket(connection);
                    plugin.getProxyManager().registerServer(server);
                    plugin.getProxy().getScheduler().runAsync(plugin, new ProxyRunnable(server, plugin));
                    break;
                }
            }

            //Well, that's pretty much done.
            //I think.
        }
    }

    private boolean isAddressAllowed(InetAddress address) {
        for(ProxyServer server : servers) {
            if(server.getAddress().equals(address)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void run() {
        try {
            handleConnections();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
