package uk.firedev.configlib.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.firedev.configlib.exception.ConfigException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class Loader {

    public static void createFile(@NotNull File file, @Nullable InputStream resource) throws ConfigException {
        if (file.exists()) {
            return;
        }
        try  {
            file.getParentFile().mkdirs();
            if (resource == null) {
                file.createNewFile();
            } else {
                Files.copy(resource, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException exception) {
            throw new ConfigException(exception);
        }
    }

}
