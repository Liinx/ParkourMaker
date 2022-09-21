package me.lynx.parkourmaker.migration;

import me.lynx.parkourmaker.ParkourMakerPlugin;
import me.lynx.parkourmaker.migration.config.ConfigMigration;
import me.lynx.parkourmaker.migration.messages.MessageMigration;

public class MigrationService {

    private static Integer actualConfigVersion;
    public static final int CURRENT_CONFIG_VERSION = 1;
    /* Current config version has to be changed in config file as well in resources. */

    private final ConfigMigration configMigration;
    private final MessageMigration messageMigration;

    public MigrationService() {
        configMigration = new ConfigMigration();
        messageMigration = new MessageMigration();
    }

    public ConfigMigration getConfigMigration() {
        return configMigration;
    }

    public MessageMigration getMessageMigration() {
        return messageMigration;
    }

    public void readConfigVersion() {
        try {
            actualConfigVersion = ParkourMakerPlugin.instance().getFileManager().getConfig()
                    .getInt("config-version");
        } catch (Exception e) {
            actualConfigVersion = null;
        }
    }

    public void doConfigMigration() {
        if (actualConfigVersion != null && actualConfigVersion >= CURRENT_CONFIG_VERSION) return;

        ParkourMakerPlugin.instance().getLogger().info("Config migration initialized...");
        configMigration.migrate();
    }

    public void doMessageMigration() {
        if (actualConfigVersion != null && actualConfigVersion >= CURRENT_CONFIG_VERSION) return;

        ParkourMakerPlugin.instance().getLogger().info("Language migration initialized...");
        messageMigration.migrate();
    }

}