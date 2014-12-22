package com.fullhousedev.multifind;

import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import redis.clients.jedis.Jedis;

/**
 * This is a description of the class.
 *
 * @author Austin Bolstridge (WolfyTheFur).
 */
public class ServerChangeListener implements Listener {
    private MultiFind plugin;

    public ServerChangeListener(MultiFind plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onServerChange(ServerSwitchEvent event) {
        final String newServer = event.getPlayer().getServer().getInfo().getName();
        final String username = event.getPlayer().getName();

        plugin.getProxy().getScheduler().runAsync(plugin, new Runnable() {
            @Override
            public void run() {
                try(Jedis redis = plugin.getRedisConnection()) {
                    redis.set(username, newServer);
                }
            }
        });
    }
}
