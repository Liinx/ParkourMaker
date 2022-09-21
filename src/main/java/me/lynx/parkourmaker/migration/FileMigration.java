package me.lynx.parkourmaker.migration;

import me.lynx.parkourmaker.ParkourMakerPlugin;
import me.lynx.parkourmaker.migration.dataholder.MigrationData;

import java.util.ArrayList;
import java.util.List;

public abstract class FileMigration {

    protected ParkourMakerPlugin plugin = ParkourMakerPlugin.instance();
    protected final List<MigrationData> migrationData;

    public FileMigration() {
        migrationData = new ArrayList<>();
    }

    public abstract void migrate();

    protected abstract void loadLocalPropertyValues();

    protected abstract void loadPluginProperties();

    protected boolean isChild(String line) {
        return countSpacesBeforeChar(line) > 1;
    }

    protected int countSpacesBeforeChar(String line) {
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) != ' ') return i;
        }
        return 0;
    }

    protected boolean equalsKey(String line, String key) {
        String lineKey = line.split(":")[0].trim();

        if (key.length() != lineKey.length()) return false;
        return lineKey.equalsIgnoreCase(key);
    }

}