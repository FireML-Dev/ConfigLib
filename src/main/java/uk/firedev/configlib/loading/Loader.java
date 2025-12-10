package uk.firedev.configlib.loading;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class Loader {

    public static void createFile(@NotNull File file, @Nullable InputStream resource) throws IOException {
        if (file.exists()) {
            return;
        }
        file.getParentFile().mkdirs();
        if (resource == null) {
            file.createNewFile();
        } else {
            Files.copy(resource, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

}
