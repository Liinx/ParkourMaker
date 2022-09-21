package me.lynx.parkourmaker.migration.messages;

import me.lynx.parkourmaker.ParkourMakerPlugin;
import me.lynx.parkourmaker.migration.FileMigration;
import me.lynx.parkourmaker.migration.dataholder.MigrationData;
import org.bukkit.configuration.ConfigurationSection;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static me.lynx.parkourmaker.util.Utils.withCounter;

public class MessageMigration extends FileMigration {

    @Override
    public void migrate() {
        long start = System.currentTimeMillis();
        loadPluginProperties();
        loadLocalPropertyValues();
        updateMessages();

        plugin.getLogger().info("Language migration took " + (System.currentTimeMillis() - start) + "ms!");
    }

    private void updateMessages() {
        if (migrationData.isEmpty()) return;

        File file = ParkourMakerPlugin.instance().getFileManager().getMessageFile();
        List<String> newLocalMessages;

        try {
            Files.delete(file.getAbsoluteFile().toPath());
            plugin.getFileManager().loadFile("messages");

            newLocalMessages = Files.readAllLines(file.toPath());
            List<MigrationData> propertyWithChildren = migrationData.stream()
                .filter(MigrationData::hasChildren)
                .collect(Collectors.toList());

            for (int i = 0; i < newLocalMessages.size(); i++) {
                String line = newLocalMessages.get(i);

                if (line.startsWith("#") || line.isEmpty())
                    newLocalMessages.set(i, line.replaceAll("\\\\", "|"));
                else if (propertyWithChildren.stream().anyMatch(property -> equalsKey(line, property.getPropertyKey())))
                    handleChildProperties(newLocalMessages, i);
                else {
                    if (line.contains("warning") || line.contains("message")) continue;
                    MigrationData data = migrationData.stream()
                        .filter(property -> equalsKey(line, property.getPropertyKey()))
                        .findFirst().orElse(null);
                    if (data == null || data.getPropertyValue() == null) continue;

                    String key = data.getPropertyKey() + ": ";
                    if (data.isStringValue()) newLocalMessages.set(i, key + "'" + data.getPropertyValue() + "'");
                    else newLocalMessages.set(i, key + data.getPropertyValue());
                }
            }
            Files.write(file.toPath(), newLocalMessages);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        plugin.getFileManager().reloadMessages();
    }

    /**
     * Replaces config properties with child values and bumps the rest
     * of the text file accordingly.
     * @param lines all lines of the file
     * @param currentLine current position
     */
    private void handleChildProperties(List<String> lines, int currentLine) {
        MigrationData data = migrationData.stream()
            .filter(property -> equalsKey(lines.get(currentLine), property.getPropertyKey()))
            .findFirst().orElse(null);
        if (data == null) return;

        AtomicInteger counter = new AtomicInteger(1);
        List<String> toAppend = new ArrayList<>();
        List<String> finalAddition = new ArrayList<>();

        data.getChildData().forEach(child -> {
            if (child.getPropertyValue() == null) return;

            if (!equalsKey(lines.get(currentLine + counter.get()), child.getPropertyKey().trim()))
                toAppend.add(lines.get(currentLine + counter.get()));

            if (child.isStringValue())
                lines.set(currentLine + counter.get(), "  " + child.getPropertyKey().trim() + ": '"
                    + child.getPropertyValue() + "'");
            else lines.set(currentLine + counter.get(), "  " + child.getPropertyKey().trim() + ": "
                    + child.getPropertyValue());

            counter.getAndIncrement();
        });

        /* adds lines that should be added after appending was handled. */
        for (int j = currentLine + counter.get(); j < lines.size(); j++) {
            finalAddition.add(lines.get(j));
        }

        int tempCount = 0;
        /* appends lines that wore lost while child values wore being added. */
        for (int j = currentLine + counter.get(); j < toAppend.size() + currentLine + counter.get(); j++) {
            if (j > lines.size() - 1) lines.add(toAppend.get(tempCount));
            else lines.set(j, toAppend.get(tempCount));
            tempCount++;
        }
        tempCount = 0;

        /* appends remaining lines that wore modified by other actions. */
        for (int j = currentLine + counter.get() + toAppend.size(); j <
                currentLine + counter.get() + toAppend.size() + finalAddition.size(); j++) {
            if (j > lines.size() - 1) lines.add(finalAddition.get(tempCount));
            else lines.set(j, finalAddition.get(tempCount));
            tempCount++;
        }
    }

    @Override
    protected void loadLocalPropertyValues() {
        ConfigurationSection messages = plugin.getFileManager().getLang().getConfigurationSection("");
        if (messages == null) {
            plugin.getLogger().warning("Messages file not found message migration aborted!");
            return;
        }

        migrationData.forEach(withCounter((i, property) -> {
            if (property.hasChildren()) {
                ConfigurationSection section = messages.getConfigurationSection(property.getPropertyKey());
                if (section == null) return;
                section.getKeys(false).forEach(key -> {
                    MigrationData data = new MigrationData(key);

                    if (property.getPropertyKey().equalsIgnoreCase("color-scheme"))
                        data.setStringValue(true);
                    else data.setStringValue(!key.equalsIgnoreCase("warning"));

                    data.setPropertyValue(section.getString(key));
                    property.addChildData(data);
                });
                return;
            }

            String value = messages.getString(property.getPropertyKey());
            property.setPropertyValue(value);
        }));
    }

    @Override
    protected void loadPluginProperties() {
        try (InputStream input = plugin.getResourceAsStream("messages.yml");
             BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {

            reader.lines()
                .filter(line -> !line.startsWith("#"))
                .filter(Predicate.not(String::isEmpty))
                .forEach(withCounter((i, line) -> {
                    String[] split = line.split(":");

                    MigrationData property = new MigrationData(split[0]);
                    if (split.length < 2) property.setStringValue(false);
                    else property.setStringValue(split[1].contains("'"));

                    property.setHasChildren(countSpacesBeforeChar(line) < 1);
                    if (i != 0 && !isChild(line)) migrationData.get(i - 1).setHasChildren(false);
                    migrationData.add(property);

                }));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}