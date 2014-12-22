package com.fullhousedev.multifind;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;

/**
 * This is a description of the class.
 *
 * @author Austin Bolstridge (WolfyTheFur).
 */
public class ChatConstants {
    public static BaseComponent[] NO_USERNAME = new ComponentBuilder("No username specified! Please specify a username!").color(ChatColor.RED).create();

    public static BaseComponent[] userFound(String username, String serverName) {
        return new ComponentBuilder("User ").color(ChatColor.GOLD).append(username).color(ChatColor.AQUA)
                .append(" is located on ").color(ChatColor.GOLD).append(serverName).color(ChatColor.AQUA).create();
    }

    public static BaseComponent[] userNotFound(String username) {
        return new ComponentBuilder(username).color(ChatColor.AQUA).append(" was not found on any server.")
                .color(ChatColor.GOLD).create();
    }
}
