package org.amitynation.botstudio;

import org.amitynation.botstudio.discordbot.DiscordBot;
import org.amitynation.botstudio.discordbot.DiscordBotManager;
import org.amitynation.botstudio.discordbot.listener.DiscordMessageFilterListener;
import org.amitynation.botstudio.io.BotStudioConfig;
import org.amitynation.botstudio.screenplay.Screenplay;
import org.amitynation.botstudio.screenplay.command.ScreenplayCommandProcessor;
import org.amitynation.wordfilter.WordFilter;
import org.amitynation.wordfilter.api.WordFilterAPI;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BotStudio extends JavaPlugin {

    @Nullable
    public Screenplay currentScreenplay = null;
    private static BotStudio instance;
    // Plugin's config file.
    private BotStudioConfig botStudioConfig;

    public File playFolder;
    private WordFilterAPI wordFilterApi = null;
    private BotStudioHttpServer server;

    public static BotStudio getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        if (Bukkit.getPluginManager().isPluginEnabled("WordFilter")) {
            wordFilterApi = new WordFilterAPI(WordFilter.instance.filterSettings);
        } else {
            getLogger().warning("Missing WordFilter plugin.");
        }

        try {
            initFolders();
        } catch (IOException e) {
            e.printStackTrace();
        }
        instance = this;

        Bukkit.getServer().getPluginCommand("botstudioshutdown").setExecutor(new ShutdownCommand());

        botStudioConfig = new BotStudioConfig();
        getBotStudioConfig().reload();
        if (getBotStudioConfig().hasErrors) {
            return;
        }
        new DiscordBotManager();
        new ScreenplayCommandProcessor();

        getBotStudioConfig().reloadBots();


        try {
            server = new BotStudioHttpServer();
            server.start(3000, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDisable() {
        // Unload all bots before shutting down.
        List<DiscordBot> oldBots = new ArrayList<>(DiscordBotManager.getInstance().discordBots.values());

        if (DiscordBotManager.getInstance() != null)
            DiscordBotManager.getInstance().onDisable();

        try {
            Thread.sleep(5000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (server != null) {
            server.stop();
        }
    }

    public void startWordFilter() {
        String filterBotName = botStudioConfig.worldFitlerConfig.botName;
        DiscordBot filterBot;
        if (filterBotName == null) {
            filterBot = DiscordBotManager.getInstance().mainBot;
        } else {
            filterBot = DiscordBotManager.getInstance().getDiscordBot(filterBotName);
            if (filterBot == null) {
                getLogger().warning(String.format("Not bot with name %s found for word filter. Defaulting to main bot.", filterBotName));
                filterBot = DiscordBotManager.getInstance().mainBot;
            }
        }
        filterBot.getJda().addEventListener(new DiscordMessageFilterListener(filterBot));
    }

    /**
     * Creates data folder if it doesn't exist.
     */
    private void initFolders() throws IOException {
        File dataFolder = getDataFolder();
        if (!dataFolder.exists()) dataFolder.mkdir();

        playFolder = new File(dataFolder, "screenplays");
        if (!playFolder.exists()) playFolder.mkdir();

        FileUtils.copyInputStreamToFile(getResource("debug.html"), new File(getDataFolder(), "debug.html"));
    }

    public BotStudioConfig getBotStudioConfig() {
        return botStudioConfig;
    }

    public WordFilterAPI getWordFilterApi() {
        return wordFilterApi;
    }
}
