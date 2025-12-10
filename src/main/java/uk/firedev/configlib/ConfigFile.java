package uk.firedev.configlib;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.firedev.configlib.exception.ConfigException;
import uk.firedev.configlib.loading.ConfigUpdater;
import uk.firedev.configlib.loading.Loader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Level;

public abstract class ConfigFile {

    private static final Logger logger = LoggerFactory.getLogger(ConfigFile.class);

    private final YamlConfiguration config = new YamlConfiguration();
    private final YamlConfiguration defaults = new YamlConfiguration();
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

        try {
            if (resource != null) {
                InputStreamReader reader = new InputStreamReader(resource);
                this.defaults.load(reader);
            }
        } catch (IOException | InvalidConfigurationException exception) {
            logger.warn("Failed to load default config.", exception);
        }

        performManualUpdates();
        save();
    }

    public ConfigFile(@NotNull File file) throws ConfigException {
        this(file, null);
    }

    public ConfigFile(@NotNull String filePath, @NotNull Plugin plugin) throws ConfigException {
        this(
            new File(plugin.getDataFolder(), filePath)
        );
        this.plugin = plugin;
    }

    public ConfigFile(@NotNull String filePath, @NotNull String resourcePath, @NotNull Plugin plugin) throws ConfigException {
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

    public @NotNull YamlConfiguration getDefaults() {
        return this.defaults;
    }

    /**
     * Updates the configuration to include any new defaults.
     */
    public void pullDefaults() {
        new ConfigUpdater(this).update();
        save();
    }

    /**
     * Updates the configuration to include any manual edits.
     */
    public abstract void performManualUpdates();

    /**
     * Moves the value of a specified key to the destination.
     * <p>
     * If the destination had a value, it will be overwritten.
     */
    public void move(@NotNull String sourceKey, @NotNull String destinationKey) {
        if (!config.contains(sourceKey)) {
            return;
        }
        Object value = config.get(sourceKey);
        config.set(destinationKey, value);
        config.set(sourceKey, null);
    }

    public void reload() {
        try {
            // In case the file is deleted.
            Loader.createFile(this.file, fetchResource(this.plugin, this.resourcePath));
            this.config.load(this.file);
        } catch (ConfigException | IOException | InvalidConfigurationException exception) {
            logger.error(
                "Failed to reload " + this.file.getName(),
                exception
            );
        }
    }

    public void save() {
        try {
            this.config.save(this.file);
        } catch (IOException exception) {
            logger.error(
                "Failed to save " + this.file.getName(),
                exception
            );
        }
    }

}
