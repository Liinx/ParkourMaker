package me.lynx.parkourmaker.io.db;

import me.lynx.parkourmaker.ParkourMakerPlugin;
import me.lynx.parkourmaker.io.db.access.AccessProvider;
import me.lynx.parkourmaker.io.db.access.AccessProviderType;
import me.lynx.parkourmaker.io.db.file.yaml.YamlProvider;

public class AccessService {

    private AccessProvider dataProvider;
    private final AccessProviderType storageType = AccessProviderType.YAML;
    private final ParkourMakerPlugin plugin = ParkourMakerPlugin.instance();

    public void start() {
        plugin.getLogger().info("Loading " + storageType.name() + " storage...");
        dataProvider = new YamlProvider();
    }

    public AccessProviderType getStorageType() {
        return storageType;
    }

    public AccessProvider getStorage() {
        return dataProvider;
    }

}