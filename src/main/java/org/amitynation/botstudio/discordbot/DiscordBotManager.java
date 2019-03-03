package org.amitynation.botstudio.discordbot;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class DiscordBotManager {

    private static DiscordBotManager instance;
    public MainDiscordBot mainBot = null;
    public Map<String, DiscordBot> discordBots = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    public DiscordBotManager() {
    }

    public static DiscordBotManager getInstance() {
        if (instance == null) {
            instance = new DiscordBotManager();
        }
        return instance;
    }

    /**
     * Gets an unmodifiable list of DiscordBots.
     */
    public Map<String, DiscordBot> getDiscordBots() {
        return Collections.unmodifiableMap(discordBots);
    }

    /**
     * Gets a bot by name.
     *
     * @return Returns bot with given name. Returns null if no bot was found.
     */
    @Nullable
    public DiscordBot getDiscordBot(@NotNull String botName) {
        if (discordBots.containsKey(botName)) {
            return discordBots.get(botName);
        }
        return null;
    }

    /**
     * Registers a discord bot into the plugin for any part of the plugin to use.
     * Also turns on the bot and set's it's status to online.
     *
     * @param discordBot DiscordBot to register
     */
    public void registerDiscordBot(DiscordBot discordBot) {
        if (discordBots.containsKey(discordBot.getName())) return;
        discordBots.put(discordBot.getName(), discordBot);
        discordBot.startupBot();
    }

    /**
     * Unregisters a discord bot and shuts it down to reclaim memory and bandwidth.
     * @param discordBot DiscordBot to unregister
     */
    public void unregisterDiscordBot(DiscordBot discordBot) {
        if (discordBot.isUnmanaged()) return;

        discordBots.remove(discordBot.getName());
        shutdownBot(discordBot);
    }

    /**
     * Should only be called when the plugin is being disabled. Shuts down all bots to avoid problems on plugin reload.
     */
    public void onDisable() {
        for (DiscordBot discordBot : discordBots.values()) {
            shutdownBot(discordBot);
        }
        discordBots.clear();
    }

    /**
     * Simple method to try to shutdown the bot. Currently locks current thread.
     */
    private void shutdownBot(DiscordBot discordBot) {
        try {
            discordBot.shutdownBot();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
