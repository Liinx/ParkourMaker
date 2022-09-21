package me.lynx.parkourmaker;

import me.lynx.parkourmaker.io.db.AccessService;
import me.lynx.parkourmaker.io.db.access.AccessProvider;
import me.lynx.parkourmaker.io.db.access.AccessProviderType;
import me.lynx.parkourmaker.io.file.load.FileManager;
import me.lynx.parkourmaker.migration.MigrationService;
import me.lynx.parkourmaker.model.map.MapHandler;
import me.lynx.parkourmaker.model.runner.RunnerHandler;

import java.io.File;
import java.io.InputStream;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParkourMakerPlugin {

    private static ParkourMakerPlugin INSTANCE;
    private final ParkourMakerAdapter adapter;
    private final MigrationService migration;
    private final FileManager fileManager;
    private final AccessService storage;
    private final MapHandler mapHandler;
    private final RunnerHandler runnerHandler;
    private final Pattern serverVersion = Pattern.compile("(?<=(\\(MC: 1)\\.)([0-9]+)");

    protected ParkourMakerPlugin(ParkourMakerAdapter adapter) {
        INSTANCE = this;
        this.adapter = adapter;

        migration = new MigrationService();
        fileManager = new FileManager(getPluginDir());
        storage = new AccessService();
        mapHandler = new MapHandler();
        runnerHandler = new RunnerHandler();

        getServerVersion();
    }

    public static ParkourMakerPlugin instance() {
        return INSTANCE;
    }

    public ParkourMakerAdapter getAdapter() {
        return adapter;
    }

    public Logger getLogger() {
        return adapter.getLogger();
    }

    public File getPluginDir() {
        return adapter.getDataFolder();
    }

    public InputStream getResourceAsStream(String fileName) {
        return adapter.getResource(fileName);
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public MapHandler getMapHandler() {
        return mapHandler;
    }

    public RunnerHandler getRunnerHandler() {
        return runnerHandler;
    }

    public AccessProvider getStorage() {
        return storage.getStorage();
    }

    public MigrationService getMigration() {
        return migration;
    }

    public AccessProviderType getStorageType() {
        return storage.getStorageType();
    }

    void startStorage() {
        storage.start();
    }

    public boolean doesPAPIExist() {
        return adapter.doesPAPIExist();
    }

    public int getServerVersion() {
        String message = adapter.getServer().getVersion();

        Matcher matcher = serverVersion.matcher(message);
        matcher.find();
        String version = message.substring(matcher.start(), matcher.end());

        return Integer.parseInt(version);
    }

}