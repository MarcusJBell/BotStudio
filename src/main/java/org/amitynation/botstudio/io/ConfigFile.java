package org.amitynation.botstudio.io;

import org.amitynation.botstudio.BotStudio;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

public class ConfigFile {

    private Plugin plugin;
    private final File configFile;
    private final String fileName;
    private final File configFolder;

    private FileConfiguration config;

    public ConfigFile(String fileName) {
        this(fileName, BotStudio.getInstance().getDataFolder(), true);
        this.plugin = BotStudio.getInstance();
    }

    public ConfigFile(String fileName, File configFolder, Boolean extractFromJar) {
        this.plugin = BotStudio.getInstance();
        this.fileName = fileName;
        this.configFolder = configFolder;
        configFile = new File(this.configFolder, fileName);

        if (extractFromJar) {
            saveDefault(false);
        }
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public void reload() {
        config = YamlConfiguration.loadConfiguration(configFile);

        InputStream defaults = plugin.getResource(fileName);
        if (defaults != null) {
            config.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defaults)));
        }
    }

    public void save() {
        try {
            config.save(configFile);
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save config to " + configFile.toString(), e);
        }
    }

    public void saveDefault(Boolean overrideExisting) {
        saveDefault(fileName, overrideExisting);
    }

    public void saveDefault(String resourcePath, Boolean overrideExisting) {
        if (!overrideExisting && configFile.exists()) return;

        InputStream inputStream = plugin.getResource(fileName);
        if (inputStream != null) {
            plugin.saveResource(resourcePath, overrideExisting);
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
//
//    @Override
//    public String toString() {
//        return config.toString();
//    }
}
