package uk.firedev.configlib.loading;

import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.firedev.configlib.ConfigFile;

public record ConfigUpdater(@NotNull ConfigFile file) {

    private static final Logger logger = LoggerFactory.getLogger(ConfigUpdater.class);

    public void update() {
        YamlConfiguration config = file.getConfig();
        YamlConfiguration defaults = file.getDefaults();

        int ver = config.getInt("version");
        int defVer = defaults.getInt("version");

        if (ver >= defVer) {
            return;
        }

        for (String key : defaults.getKeys(true)) {
            if (config.isSet(key)) {
                continue;
            }
            if (defaults.isConfigurationSection(key)) {
                if (config.getConfigurationSection(key) == null) {
                    config.createSection(key);
                }
                continue;
            }
            config.set(key, defaults.get(key));
        }

        config.set("version", defVer);
    }

}
