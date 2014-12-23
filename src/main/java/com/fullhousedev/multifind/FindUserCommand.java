package com.fullhousedev.multifind;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import redis.clients.jedis.Jedis;

/**
 * This is a description of the class.
 *
 * @author Austin Bolstridge (WolfyTheFur).
 */
public class FindUserCommand extends Command {
    private MultiFind plugin;

    public FindUserCommand(MultiFind plugin) {
        super("gofind", "multifind.find");
        this.plugin = plugin;
    }

    @Override
    public void execute(final CommandSender commandSender, final String[] args) {
        if(args.length < 1) {
            commandSender.sendMessage(ChatConstants.NO_USERNAME);
            return;
        }
        plugin.getProxy().getScheduler().runAsync(plugin, new Runnable() {
            @Override
            public void run() {
                if(plugin.runningOnRedis()) {
                    try (Jedis redis = plugin.getRedisConnection()) {
                        if (redis.exists(args[0].toLowerCase())) {
                            String server = redis.get(args[0].toLowerCase());
                            commandSender.sendMessage(ChatConstants.userFound(args[0], server));
                        } else {
                            commandSender.sendMessage(ChatConstants.userNotFound(args[0]));
                        }
                    }
                }
                else {
                    if(plugin.getPlayerManager().isPlayerLoggedIn(args[0].toLowerCase())) {
                        String server = plugin.getPlayerManager().getServer(args[0].toLowerCase());
                        commandSender.sendMessage(ChatConstants.userFound(args[0], server));
                    }
                    else {
                        commandSender.sendMessage(ChatConstants.userNotFound(args[0]));
                    }
                }
            }
        });
    }
}
