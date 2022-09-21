package me.lynx.parkourmaker.io.message;

import me.lynx.parkourmaker.ParkourMakerPlugin;
import org.bukkit.configuration.file.YamlConfiguration;

public class ColorScheme {

    private final String primary;
    private final String secondary;
    private final String defaultText;
    private final String alternativeText;
    private final String parkourMap;
    private final String player;
    private final String warning;
    private final boolean useColorScheme;

    public ColorScheme() {
        YamlConfiguration lang = ParkourMakerPlugin.instance().getFileManager().getLang();

        useColorScheme = lang.getBoolean("use-color-scheme");
        primary = lang.getString("color-scheme.primary");
        secondary = lang.getString("color-scheme.secondary");
        defaultText = lang.getString("color-scheme.default-text");
        alternativeText = lang.getString("color-scheme.alternative-text");
        parkourMap = lang.getString("color-scheme.parkour-map");
        player = lang.getString("color-scheme.player");
        warning = lang.getString("color-scheme.warning");
    }

    public boolean useColorScheme() {
        return useColorScheme;
    }

    public String getWarning() {
        return warning;
    }

    public String getPrimary() {
        return primary;
    }

    public String getSecondary() {
        return secondary;
    }

    public String getDefaultText() {
        return defaultText;
    }

    public String getAlternativeText() {
        return alternativeText;
    }

    public String getParkourMap() {
        return parkourMap;
    }

    public String getPlayer() {
        return player;
    }

}