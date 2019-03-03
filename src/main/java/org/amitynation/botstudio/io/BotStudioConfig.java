package org.amitynation.botstudio.io;

import org.amitynation.botstudio.BotStudio;
import org.amitynation.botstudio.command.DiscordCommand;
import org.amitynation.botstudio.command.DiscordCommandProcessor;
import org.amitynation.botstudio.discordbot.DiscordBot;
import org.amitynation.botstudio.discordbot.DiscordBotManager;
import org.amitynation.botstudio.discordbot.MainDiscordBot;
import org.amitynation.wordfilter.api.WordFilterAPI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class BotStudioConfig extends ConfigFile {

    private static Random wordFilterRandom = new Random();

    public WorldFitlerConfig worldFitlerConfig;
    public boolean hasErrors = false;

    public String defaultBotName = null;
    public String defaultBotToken = null;
    public long debugChannelId = -1L;
    public List<Long> chatFilterIgnoredChannels = new ArrayList<>();
    public String[] prefixes;

    public List<ConfigBot> otherBots = new ArrayList<>();

    public BotStudioConfig() {
        super("config.yml");
    }

    @Override
    public void reload() {
        super.reload();

        FileConfiguration config = getConfig();
        defaultBotName = null;
        defaultBotToken = null;
        chatFilterIgnoredChannels.clear();
        otherBots.clear();

        try {
            reloadBotConfig(config);
            reloadDelegateCommands(config);
        } catch (ConfigLoadException e) {
            shutdownError(e.message);
        }
        if (defaultBotName == null || defaultBotToken == null) {
            shutdownError("Missing main discord bot.");
            return;
        }

        debugChannelId = getConfig().getLong("debugChannel", -1L);
        if (debugChannelId == -1L) {
            shutdownError("Missing debugChannel from config.yml");
            return;
        }

        List<String> prefixesList = getConfig().getStringList("prefixes");
        if (prefixesList.isEmpty()) {
            prefixesList.add("!");
        }
        prefixes = prefixesList.toArray(new String[0]);
        chatFilterIgnoredChannels = getConfig().getLongList("chatFilter.ignoredChannels");
        reloadWordFilterConfig(config);
    }

    private void reloadWordFilterConfig(FileConfiguration config) {
        worldFitlerConfig = new WorldFitlerConfig();
        ConfigurationSection filterSection = config.getConfigurationSection("chatFilter");
        if (filterSection == null) {
            return;
        }

        worldFitlerConfig.botName = filterSection.getString("bot", null);
        worldFitlerConfig.addRandomResponse(filterSection.getStringList("randomResponses"));

        ConfigurationSection wordBasedSection = filterSection.getConfigurationSection("wordBasedResponses");
        if (wordBasedSection != null) {
            for (String word : wordBasedSection.getKeys(false)) {
                worldFitlerConfig.addWordBasedResponse(word, wordBasedSection.getStringList(word));
            }
        }
    }

    private void reloadBotConfig(FileConfiguration config) throws ConfigLoadException {
        ConfigurationSection botSection = config.getConfigurationSection("bots");
        if (botSection == null) {
            throw new ConfigLoadException("Missing config section named 'bots'");
        }
        for (String key : botSection.getKeys(false)) {
            String botPath = "bots." + key + ".";
            String name = config.getString(botPath + "name");
            String token = config.getString(botPath + "token");
            boolean main = config.getBoolean(botPath + "main");
            if (name == null) {
                throw new ConfigLoadException("Bot with key '" + key + "' error. Name cannot be null");
            }
            if (token == null) {
                throw new ConfigLoadException("Bot with key '" + key + "' error. Token cannot be null");
            }
            if (main) {
                if (defaultBotName != null) {
                    throw new ConfigLoadException("Cannot register multiple bots as main bot.");
                }
                defaultBotName = name;
                defaultBotToken = token;
                continue;
            }

            otherBots.add(new ConfigBot(name, token));
        }
    }

    private void reloadDelegateCommands(FileConfiguration config) throws ConfigLoadException {
        for (DiscordCommand registeredCommand : DiscordCommandProcessor.getInstance().getRegisteredCommands()) {
            registeredCommand.setDelegate(null);
        }
        
        ConfigurationSection delegateSection = config.getConfigurationSection("delegateCommands");
        if (delegateSection == null) {
            return;
        }
        for (String key : delegateSection.getKeys(false)) {
            String commandName = key;
            String delegateBot = config.getString("delegateCommands." + key);
            if (delegateBot == null) {
                throw new ConfigLoadException("Delegate command '" + key + "' cannot have blank name.");
            }
            DiscordCommand command = DiscordCommandProcessor.getInstance().getCommandByName(commandName);
            if (command == null) {
                throw new ConfigLoadException("Delegate command '" + key + "' isn't a registered command.");
            }
            command.setDelegate(delegateBot);
        }
    }

    private void shutdownError(String message) {
        BotStudio.getInstance().getLogger().severe(message);
        Bukkit.getServer().getPluginManager().disablePlugin(BotStudio.getInstance());
        hasErrors = true;
    }

    public void reloadBots() {
        // Add main bot from config.
        DiscordBotManager.getInstance().onDisable();

        MainDiscordBot bot = new MainDiscordBot(defaultBotName, defaultBotToken);
        DiscordBotManager.getInstance().registerDiscordBot(bot);
        DiscordBotManager.getInstance().mainBot = bot;

        configLoop:
        for (BotStudioConfig.ConfigBot otherBot : otherBots) {
            if (DiscordBotManager.getInstance().getDiscordBot(otherBot.name) != null) {
                BotStudio.getInstance().getLogger().warning("Attempted to register multiple bots with name '" + otherBot.name + "'. Ignoring.");
                continue;
            }
            for (DiscordBot discordBot : DiscordBotManager.getInstance().discordBots.values()) {
                if (discordBot.getToken().equals(otherBot.token)) {
                    BotStudio.getInstance().getLogger().warning("Attempted to register multiple bots with  same token. Ignoring bot with name '" + otherBot.name + "'.");
                    continue configLoop;
                }
            }
            DiscordBotManager.getInstance().registerDiscordBot(new DiscordBot(otherBot.name, otherBot.token, true));
        }

        WordFilterAPI wordFilterAPI = BotStudio.getInstance().getWordFilterApi();
        if (wordFilterAPI != null) {
            BotStudio.getInstance().startWordFilter();
        }
    }

    @Override
    public void save() {
        getConfig().set("defaultBot.name", defaultBotName);
        getConfig().set("defaultBot.token", defaultBotToken);
        getConfig().set("debugChannel", debugChannelId);
        getConfig().set("chatFilter.ignoredChannels", chatFilterIgnoredChannels);

        super.save();
    }

    public class ConfigBot {
        public final String name;
        public final String token;

        public ConfigBot(String name, String token) {
            this.name = name;
            this.token = token;
        }
    }

    private class ConfigLoadException extends Exception {
        public String message;

        ConfigLoadException(String message) {
            this.message = message;
        }
    }

    public class WorldFitlerConfig {
        public String botName;
        private List<String> randomResponses = new ArrayList<>();
        private Map<String, List<String>> wordBasedResponses = new HashMap<>();

        public void addRandomResponse(String... message) {
            randomResponses.addAll(Arrays.asList(message));
        }

        public void addRandomResponse(List<String> message) {
            randomResponses.addAll(message);
        }

        public void addWordBasedResponse(String word, List<String> message) {
            if (!wordBasedResponses.containsKey(word.toLowerCase())) {
                wordBasedResponses.put(word.toLowerCase(), new ArrayList<>());
            }
            wordBasedResponses.get(word.toLowerCase()).addAll(message);
        }

        public void addWordBasedResponse(String word, String... message) {
            addWordBasedResponse(word, Arrays.asList(message));
        }

        @NotNull
        public String getRandomResponse() {
            if (randomResponses.isEmpty()) {
                return "Beep boop much like a human I am not perfect. If you feel this was a mistake please contact a staff member!";
            }
            return randomResponses.get(wordFilterRandom.nextInt(randomResponses.size()));
        }

        @Nullable
        public String getRandomWordBasedResponse(String word) {
            if (!wordBasedResponses.containsKey(word.toLowerCase())) {
                return null;
            }
            List<String> responses = wordBasedResponses.get(word.toLowerCase());
            if (responses.isEmpty()) {
                return null;
            }
            return responses.get(wordFilterRandom.nextInt(responses.size()));
        }
    }
}
