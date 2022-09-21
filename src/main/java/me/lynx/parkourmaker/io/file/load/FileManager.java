package me.lynx.parkourmaker.io.file.load;

import me.lynx.parkourmaker.ParkourMakerPlugin;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class FileManager {

    private final ParkourMakerPlugin PLUGIN = ParkourMakerPlugin.instance();
    private final File pluginDir;
    private File config, lang;
    private YamlConfiguration confLoaded, langLoaded;
    private final Function<File,YamlConfiguration> loadMethod = YamlConfiguration::loadConfiguration;

    private final String CONFIG = "config";
    private final String MESSAGES = "messages";

    public FileManager(File pluginDir) {
        this.pluginDir = pluginDir;
    }

    public void startupAsync(Runnable after) {
        CompletableFuture.runAsync(this::startup).thenRun(after).join();
    }

    public void startup() {
        loadFile(CONFIG);
        loadFile(MESSAGES);
        PLUGIN.getLogger().info("All files have been loaded.");
        PLUGIN.getMigration().readConfigVersion();
    }

    public void loadFile(String fileName) {
        if (!pluginDir.exists()) pluginDir.mkdir();

        File file = new File(pluginDir, fileName + ".yml");
        if (!file.exists()) {
            try (InputStream inputStream = PLUGIN.getResourceAsStream(fileName + ".yml")) {
                Files.copy(inputStream, file.getAbsoluteFile().toPath());
            } catch (Exception e) {
                e.printStackTrace();
                PLUGIN.getLogger().severe("Could not create " + fileName + ".yml!");
            }
        }

        if (fileName.equalsIgnoreCase(CONFIG)) {
            config = file;
            reloadConfig();
        }
        else if (fileName.equalsIgnoreCase(MESSAGES)) {
            langLoaded = loadMethod.apply(file);
            lang = file;
        }
    }

    public void reloadConfig() {
        confLoaded = loadMethod.apply(config);
    }

    public void reloadMessages() {
        langLoaded = loadMethod.apply(lang);
    }

    public YamlConfiguration getConfig() {
        return confLoaded;
    }

    public YamlConfiguration getLang() {
        return langLoaded;
    }

    public File getConfigFile() {
        return config;
    }

    public File getMessageFile() {
        return lang;
    }

}