package com.fullhousedev.multifind.socket;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;

/**
 * This is a description of the class.
 *
 * @author Austin Bolstridge (WolfyTheFur).
 */
public class ProxyServerManager {

    ArrayList<ProxyServer> servers = new ArrayList<>();

    public ProxyServerManager() {

    }

    public boolean registerServer(ProxyServer server) {
        if(servers.contains(server)) {
            return false;
        }
        servers.add(server);
        return true;
    }

    public void deregisterServer(ProxyServer server) {
        servers.remove(server);
    }

    public boolean isServerRegistered(ProxyServer server) {
        return servers.contains(server);
    }

    public boolean isServerRegistered(InetAddress address) {
        for(ProxyServer server : servers) {
            if(server.getAddress().equals(address)) {
                return true;
            }
        }
        return false;
    }

    public void closeAllProxies() throws IOException {
        for(ProxyServer server : servers) {
            server.closeResource();
        }
    }

    public void sendToAll(String data) {
        for(ProxyServer server : servers) {
            server.sendData(data);
        }
    }
}
