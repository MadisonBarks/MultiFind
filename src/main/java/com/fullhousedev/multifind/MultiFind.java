package com.fullhousedev.multifind;

import com.fullhousedev.multifind.socket.*;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginLogger;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import sun.awt.image.ImageWatched;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * This is the main class.
 *
 * @author Austin Bolstridge (WolfyTheFur).
 */
public class MultiFind extends Plugin {

    //Redis variables
    private JedisPool redisPool;

    //Socket variables
    private SocketController socketController;
    private SocketServerController serverController;
    private ProxyServerManager proxyManager;
    private PlayerManager playerManager;
    private int defaultPort;

    @Override
    public void onEnable() {

        PluginLogger.getLogger("MultiFind").info("Now parsing configuration");

        Configuration config = this.loadConfiguration();

        PluginLogger.getLogger("MultiFind").info("Parsing configuration done");

        if(config.getString("communication").equalsIgnoreCase("redis")) {
            redisPool = this.prepareJedis(config);

            this.getProxy().getPluginManager().registerListener(this, new PostLoginListener(this));
            this.getProxy().getPluginManager().registerListener(this, new ServerChangeListener(this));
            this.getProxy().getPluginManager().registerListener(this, new ServerDisconnectListener(this));
        }
        else if(config.getString("communication").equalsIgnoreCase("socket")) {
            defaultPort = config.getInt("socket-config.port");

            ProxyServer[] servers = prepareServers(config, defaultPort);

            this.prepareSocketSystem(config, defaultPort, servers);

            this.getProxy().getScheduler().runAsync(this, socketController);
            this.getProxy().getScheduler().runAsync(this, serverController);

            playerManager = new PlayerManager(this);

            this.getProxy().getPluginManager().registerListener(this, playerManager);
        }

        this.getProxy().getPluginManager().registerCommand(this, new FindUserCommand(this));
    }

    @Override
    public void onDisable() {
        try {
            this.proxyManager.closeAllProxies();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Jedis getRedisConnection() {
        return redisPool.getResource();
    }

    private JedisPool prepareJedis(Configuration config) {
        String redisHostname = config.getString("redis.hostname", "localhost");
        int port = config.getInt("redis.port", 6379);

        return new JedisPool(new JedisPoolConfig(), redisHostname);
    }

    private ProxyServer[] prepareServers(Configuration config, int defaultPort) {
        LinkedHashMap<Integer, LinkedHashMap<String, Object>> serversList =
                (LinkedHashMap<Integer, LinkedHashMap<String, Object>>) config.get("servers");

        if(serversList == null) {
            return new ProxyServer[1];
        }

        ProxyServer[] servers = new ProxyServer[serversList.size()];

        for(int i = 1; i <= serversList.size(); i++) {
            LinkedHashMap<String, Object> server = serversList.get(i);
            String address = (String) server.get("address");
            int port = defaultPort;
            if(server.containsKey("port")) {
                port = (Integer) server.get("port");
            }

            try {
                servers[i - 1] = new ProxyServer(address, port);
            } catch (UnknownHostException e) {
                PluginLogger.getLogger("MultiFind").warning("Could not find host " + address + ", ignoring.");
            }
        }

        return servers;
    }

    private void prepareSocketSystem(Configuration config, int defaultPort, ProxyServer... servers) {
        int delay = config.getInt("socket-config.connection-delay", 10) * config.getInt("socket-config.proxy-id", 1);
        this.socketController = new SocketController(delay, this, servers);
        this.serverController = new SocketServerController(this, defaultPort, servers);
        this.proxyManager = new ProxyServerManager();
    }

    private Configuration loadConfiguration() {
        if (!getDataFolder().exists())
            getDataFolder().mkdir();

        File file = new File(getDataFolder(), "config.yml");

        if (!file.exists()) {
            try {
                Files.copy(getResourceAsStream("config.yml"), file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Configuration configuration = null;
        try {
            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class)
                    .load(new File(getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return configuration;
    }

    public boolean runningOnRedis() {
        return redisPool != null;
    }

    public ProxyServerManager getProxyManager() {
        return proxyManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }
}
