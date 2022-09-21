package me.lynx.parkourmaker.migration.config;

import me.lynx.parkourmaker.ParkourMakerPlugin;
import me.lynx.parkourmaker.migration.FileMigration;
import me.lynx.parkourmaker.migration.MigrationService;
import me.lynx.parkourmaker.migration.dataholder.MigrationData;
import org.bukkit.configuration.ConfigurationSection;

import java.io.*;
import java.nio.file.Files;
import java.util.List;
import java.util.function.Predicate;

public class ConfigMigration extends FileMigration {

    @Override
    public void migrate() {
        long start = System.currentTimeMillis();
        updateFile();
        plugin.getLogger().info("Config migration took " + (System.currentTimeMillis() - start) + "ms!");
    }

    public void updateFile() {
        loadPluginProperties();
        loadLocalPropertyValues();
        if (migrationData.isEmpty()) return;

        File file = ParkourMakerPlugin.instance().getFileManager().getConfigFile();
        List<String> newLocalConfig;

        try {
            Files.delete(file.getAbsoluteFile().toPath());
            plugin.getFileManager().loadFile("config");

            newLocalConfig = Files.readAllLines(file.toPath());

            for (int i = 0; i < newLocalConfig.size(); i++) {
                String line = newLocalConfig.get(i);
                if (line.startsWith("#") || line.isEmpty())
                    newLocalConfig.set(i, line.replaceAll("\\\\", "|"));
                else if (line.startsWith("config-version"))
                    newLocalConfig.set(i, "config-version: " + MigrationService.CURRENT_CONFIG_VERSION);
                else {
                    MigrationData data = migrationData.stream()
                        .filter(property -> equalsKey(line, property.getPropertyKey()))
                        .findFirst().orElse(null);
                    if (data == null || data.getPropertyValue() == null) continue;

                    String key = data.getPropertyKey() + ": ";
                    if (data.isStringValue()) newLocalConfig.set(i, key + "'" + data.getPropertyValue() + "'");
                    else newLocalConfig.set(i, key + data.getPropertyValue());
                }
            }
            Files.write(file.toPath(), newLocalConfig);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        plugin.getFileManager().reloadConfig();
    }

    /**
     * Loads local values from plugin directory to replace default one after migration.
     */
    @Override
    protected void loadLocalPropertyValues() {
        ConfigurationSection config = plugin.getFileManager().getConfig().getConfigurationSection("");
        if (config == null) {
            plugin.getLogger().warning("Config file not found config migration aborted!");
            return;
        }

        migrationData.forEach(property -> {
            if (property.hasChildren()) {
                ConfigurationSection section = config.getConfigurationSection(property.getPropertyKey());
                if (section == null) return;
                section.getKeys(false).forEach(key -> {
                    MigrationData data = new MigrationData(key);

                    if (property.getPropertyKey().equalsIgnoreCase("token-booster")) {
                        ConfigurationSection boosterSection = section.getConfigurationSection(key);
                        if (boosterSection == null) return;

                        boosterSection.getKeys(false).forEach(boosterKey -> {
                            MigrationData boosterData;
                            if (boosterKey.equalsIgnoreCase("boost-type")) {
                                boosterData = new MigrationData("booster-type");
                                boosterData.setLegacyKey(boosterKey);
                            } else boosterData = new MigrationData(boosterKey);

                            boosterData.setPropertyValue(boosterSection.getString(boosterKey));

                            if (boosterData.getPropertyKey().equalsIgnoreCase("name") ||
                                boosterData.getPropertyKey().equalsIgnoreCase("booster-type")) {
                                boosterData.setStringValue(true);
                            }
                            data.addChildData(boosterData);
                        });
                    } else {
                        if (property.getPropertyKey().equalsIgnoreCase("commands"))
                            data.setStringValue(true);

                        data.setPropertyValue(section.getString(key));
                    }
                    property.addChildData(data);
                });
                return;
            }
            String value = config.getString(property.getPropertyKey());
            property.setPropertyValue(value);
        });
    }

    /**
     * Loads plugin properties from internal plugin resource, setting the property name
     * and if value should be string or not.
     */
    @Override
    protected void loadPluginProperties() {
        try (InputStream input = plugin.getResourceAsStream("config.yml");
             BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {

            reader.lines()
                .filter(line -> !line.startsWith("#"))
                .filter(Predicate.not(String::isEmpty))
                .forEach(line -> {
                    String[] split = line.split(":");

                    MigrationData property = new MigrationData(split[0]);
                    if (split.length < 2) property.setStringValue(false);
                    else property.setStringValue(split[1].contains("'"));

                    migrationData.add(property);
                });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}