package com.fullhousedev.multifind;

import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import redis.clients.jedis.Jedis;

/**
 * This is a description of the class.
 *
 * @author Austin Bolstridge (WolfyTheFur).
 */
public class PostLoginListener implements Listener {
    private MultiFind plugin;

    public PostLoginListener(MultiFind plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        final String username = event.getPlayer().getName().toLowerCase();
        final String server = event.getPlayer().getServer().getInfo().getName();

        plugin.getProxy().getScheduler().runAsync(plugin, new Runnable() {
            @Override
            public void run() {
                try(Jedis redis = plugin.getRedisConnection()) {
                    redis.set(username, server);
                }
            }
        });
    }
}
