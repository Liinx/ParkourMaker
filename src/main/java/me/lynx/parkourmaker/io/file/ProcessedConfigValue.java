package me.lynx.parkourmaker.io.file;

import me.lynx.parkourmaker.ParkourMakerPlugin;
import me.lynx.parkourmaker.exception.InvalidConfigException;
import me.lynx.parkourmaker.io.file.dataholder.MapPermissions;
import me.lynx.parkourmaker.io.file.load.ConfigValues;
import me.lynx.parkourmaker.io.message.MessageManager;

public class ProcessedConfigValue {

    private static final ProcessedConfigValue instance = new ProcessedConfigValue();
    private MapPermissions mapPermissions;

    private ProcessedConfigValue() {}

    public static ProcessedConfigValue of() {
        return instance;
    }

    public void loadDynamicVales() {
        mapPermissions = new MapPermissions();
    }

    public void loadAfterStorage() {
        mapPermissions.loadPermissions();
    }

    public MapPermissions mapPermissions() {
        return mapPermissions;
    }

    private int processInt(ConfigValues value) {
        try {
            return Integer.parseInt(value.getValue());
        } catch (NumberFormatException e) {
            throw new InvalidConfigException(value, e);
        }
    }

    private boolean processBool(ConfigValues value) {
        try {
            return Boolean.parseBoolean(value.getValue());
        } catch (NumberFormatException e) {
            throw new InvalidConfigException(value, e);
        }
    }

    public boolean notifyOperatorNewVersion() {
        return processBool(ConfigValues.NOTIFY_OPERATOR_NEW_VERSION);
    }

    /**
     * Returns plugin message prefix formatted.
     */
    public String messagePrefix() {
        String rawPrefix = ConfigValues.MESSAGE_PREFIX.getValue();
        rawPrefix = rawPrefix == null ? (ParkourMakerPlugin.instance().getServerVersion() >= 16 ?
            "#42473f[#75ff1fParkourMaker#42473f] &r" : "&8[&aParkourMaker&8] &r") : rawPrefix;
        return MessageManager.instance().translate(rawPrefix);
    }

    public int configVersion() {
        return processInt(ConfigValues.CONFIG_VERSION);
    }

}