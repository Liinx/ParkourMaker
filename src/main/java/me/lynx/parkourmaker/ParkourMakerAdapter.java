package me.lynx.parkourmaker;

import me.lynx.parkourmaker.command.MainCommand;
import me.lynx.parkourmaker.command.MainCommandTabComplete;
import me.lynx.parkourmaker.io.file.ProcessedConfigValue;
import me.lynx.parkourmaker.io.file.load.ConfigValues;
import me.lynx.parkourmaker.io.message.MessageManager;
import me.lynx.parkourmaker.io.message.PAPIExpansion;
import me.lynx.parkourmaker.model.sign.SignHandler;
import me.lynx.parkourmaker.util.update.UpdateChecker;
import me.lynx.parkourmaker.util.update.UpdateNotifier;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public class ParkourMakerAdapter extends JavaPlugin {

    private final ParkourMakerPlugin plugin;
    private boolean PAPIExists;

    public ParkourMakerAdapter() {
        plugin = new ParkourMakerPlugin(this);
    }

    @Override
    public void onEnable() {
        /* Core Files and Config Properties */
        plugin.getFileManager().startup();
        plugin.getMigration().doConfigMigration();
        plugin.getMigration().doMessageMigration();
        ConfigValues.setupValues();

        /* Messaging */
        registerPAPI();
        MessageManager.instance().prepare();

        /* World Edit check */
        if (!doesWorldEditExist()) {
            getLogger().info("Plugin will now disable.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        };

        plugin.startStorage();
        ProcessedConfigValue.of().loadAfterStorage();

        /* Events */
        Bukkit.getPluginManager().registerEvents(plugin.getRunnerHandler(), this);
        Bukkit.getPluginManager().registerEvents(new SignHandler(), this);
        Bukkit.getPluginManager().registerEvents(new UpdateNotifier(), this);

        /* Commands and TabCompletion */
        MainCommand mainCommand = new MainCommand();
        getCommand(mainCommand.getName()).setExecutor(mainCommand);
        getCommand(mainCommand.getName()).setAliases(new ArrayList<>(mainCommand.getAliases()));
        getCommand(mainCommand.getName()).setTabCompleter(new MainCommandTabComplete(mainCommand.getAllCommands()));

        /* Update checker and Analytics*/
        UpdateChecker.getInstance().check(true, null);
        enableBStats();

        /* Continuing runs for all the player that stayed on during reload */
        plugin.getRunnerHandler().continueRunsOnEnable();

        getLogger().info("Parkour Maker has been enabled successfully!");
    }

    @Override
    public void onDisable() {
        plugin.getRunnerHandler().pauseRunsOnDisable();

        getLogger().info("Parkour Maker has been disabled!");
    }

    private boolean doesWorldEditExist() {
        if (Bukkit.getPluginManager().getPlugin("WorldEdit") == null) {
            getLogger().severe("WorldEdit was not found on this server!.");
            getLogger().severe("Please add World Edit in order to use Parkour Maker!.");
            return false;
        }
        return true;
    }

    private void registerPAPI() {
        if(getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            PAPIExists = true;
            new PAPIExpansion().register();
            getLogger().info("Registered PlaceholderAPI expansion.");
        } else PAPIExists = false;
    }

    public boolean doesPAPIExist() {
        return PAPIExists;
    }

    private void enableBStats() {
        int pluginId = 2373;
        Metrics metrics = new Metrics(this, pluginId);
    }

}