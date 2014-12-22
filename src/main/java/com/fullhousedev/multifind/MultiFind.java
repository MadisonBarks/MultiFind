package com.fullhousedev.multifind;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * This is the main class.
 *
 * @author Austin Bolstridge (WolfyTheFur).
 */
public class MultiFind extends Plugin{

    private JedisPool redisPool;

    @Override
    public void onEnable() {
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

        String redisHostname = configuration.getString("redis.hostname", "localhost");
        int port = configuration.getInt("redis.port", 6379);

        redisPool = new JedisPool(new JedisPoolConfig(), redisHostname);

        this.getProxy().getPluginManager().registerListener(this, new PostLoginListener(this));
        this.getProxy().getPluginManager().registerListener(this, new ServerChangeListener(this));
        this.getProxy().getPluginManager().registerListener(this, new ServerDisconnectListener(this));
        this.getProxy().getPluginManager().registerCommand(this, new FindUserCommand(this));
    }

    public Jedis getRedisConnection() {
        return redisPool.getResource();
    }
}
