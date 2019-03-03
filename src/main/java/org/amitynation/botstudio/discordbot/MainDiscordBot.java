package org.amitynation.botstudio.discordbot;

import org.amitynation.botstudio.BotStudio;
import org.amitynation.botstudio.discordbot.listener.DiscordCommandListener;
import org.bukkit.Bukkit;

public class MainDiscordBot extends DiscordBot {

    private static MainDiscordBot instance;

    /**
     * Main discord bot that handles all events. There should only be one main bot otherwise the plugin will shutdown.
     */
    public MainDiscordBot(String botName, String token) {
        super(botName, token, true);
        if (instance != null) {
            BotStudio.getInstance().getLogger().severe("More than one MainDiscordBot was attempted to be registered.");
            BotStudio.getInstance().getLogger().severe("Bot with name '" + instance.getName() + "' already exists but bot with name '" + botName + "' was attempted to be registered");
            Bukkit.getServer().getPluginManager().disablePlugin(BotStudio.getInstance());
            return;
        }
        instance = this;
    }

    @Override
    void startupBot() {
        super.startupBot();

        jda.addEventListener(
                new DiscordCommandListener()
        );
    }

    @Override
    void shutdownBot() {
        super.shutdownBot();
        instance = null;
    }
}
