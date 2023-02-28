package me.beeland.dunmoore.archaeology;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class PluginConfiguration {

    private JavaPlugin plugin;
    private File file;
    private FileConfiguration config;

    public PluginConfiguration(Archaeology plugin, File directory, String filename, boolean copy) {

        this.plugin = plugin;
        this.file = new File(directory, filename);

        if(!directory.exists()) {
            directory.mkdir();
        }

        if(!file.exists()) {

            if(copy) {

                plugin.saveResource(filename, false);

            } else {

                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        reloadConfig();
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public void reloadConfig() {
        this.plugin.getLogger().info("New configuration loaded: [" + file.getName() + "]");
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public void saveConfig() {
        try {
            this.config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ConfigurationSection getConfigSection(String path) {
        return config.getConfigurationSection(path);
    }

    public String getString(String path) {
        return config.getString(path);
    }

    public int getInteger(String path) {
        return config.getInt(path);
    }

    public double getDouble(String path) {
        return config.getDouble(path);
    }

    public boolean getBoolean(String path) {
        return config.getBoolean(path);
    }

}