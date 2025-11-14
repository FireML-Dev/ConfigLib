package uk.firedev.configlib.exception;

import org.jetbrains.annotations.NotNull;

public class ConfigException extends Exception {

    public ConfigException(@NotNull String string) {
        super(string);
    }

    public ConfigException(@NotNull Throwable throwable) {
        super(throwable);
    }

}
