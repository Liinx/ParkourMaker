package me.lynx.parkourmaker.io.file.load;

import me.lynx.parkourmaker.ParkourMakerPlugin;
import me.lynx.parkourmaker.io.file.ProcessedConfigValue;
import org.bukkit.configuration.file.YamlConfiguration;

public enum ConfigValues {

    NOTIFY_OPERATOR_NEW_VERSION("notify-operator-new-version"),
    MESSAGE_PREFIX("message-prefix"),
    CONFIG_VERSION("config-version");

    public static void setupValues() {
        YamlConfiguration config = ParkourMakerPlugin.instance().getFileManager().getConfig();

        for (ConfigValues value : ConfigValues.values()) {
            value.setValue(config.getString(value.getMapping()));
        }

        ProcessedConfigValue.of().loadDynamicVales();
    }

    private String value;
    private final String mapping;

    ConfigValues(String mapping) {
        this.mapping = mapping;
    }

    private void setValue(String value) {
        this.value = value;
    }

    public String getMapping() {
        return mapping;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return mapping;
    }

}