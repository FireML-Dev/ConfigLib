package uk.firedev.configlib;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.firedev.configlib.exception.ConfigException;
import uk.firedev.configlib.util.Loader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigFile {

    private static final Logger logger = Logger.getLogger(ConfigFile.class.getName());

    private final YamlConfiguration config = new YamlConfiguration();
    private final @NotNull File file;
    private @Nullable Plugin plugin;
    private @Nullable String resourcePath;

    // Constructors

    public ConfigFile(@NotNull File file, @Nullable InputStream resource) throws ConfigException {
        Loader.createFile(file, resource);
        this.file = file;

        try {
            this.config.load(file);
        } catch (IOException | InvalidConfigurationException exception) {
            throw new ConfigException(exception);
        }
    }

    public ConfigFile(@NotNull File file) throws ConfigException {
        this(file, null);
    }

    public ConfigFile(@NotNull Plugin plugin, @NotNull String filePath) throws ConfigException {
        this(
            new File(plugin.getDataFolder(), filePath)
        );
        this.plugin = plugin;
    }

    public ConfigFile(@NotNull Plugin plugin, @NotNull String filePath, @NotNull String resourcePath) throws ConfigException {
        this(
            new File(plugin.getDataFolder(), filePath),
            fetchResource(plugin, resourcePath)
        );
        this.plugin = plugin;
        this.resourcePath = resourcePath;
    }

    private static @Nullable InputStream fetchResource(@Nullable Plugin plugin, @Nullable String resourcePath) {
        if (plugin == null || resourcePath == null) {
            return null;
        }
        return plugin.getResource(resourcePath);
    }

    // Config Things

    public @NotNull YamlConfiguration getConfig() {
        return this.config;
    }

    public void reload() {
        try {
            // In case the file is deleted.
            Loader.createFile(this.file, fetchResource(this.plugin, this.resourcePath));
            this.config.load(this.file);
        } catch (ConfigException | IOException | InvalidConfigurationException exception) {
            logger.log(
                Level.SEVERE,
                "Failed to reload " + this.file.getName(),
                exception
            );
        }
    }

    public void save() {
        try {
            this.config.save(this.file);
        } catch (IOException exception) {
            logger.log(
                Level.SEVERE,
                "Failed to save " + this.file.getName(),
                exception
            );
        }
    }

}
