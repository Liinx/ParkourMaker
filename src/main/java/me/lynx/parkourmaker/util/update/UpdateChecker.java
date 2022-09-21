package me.lynx.parkourmaker.util.update;

import me.lynx.parkourmaker.ParkourMakerPlugin;
import me.lynx.parkourmaker.io.file.ProcessedConfigValue;
import me.lynx.parkourmaker.io.message.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class UpdateChecker {

    private int resourceId;
    private URL resourceURL;
    private String currentVersionString;
    private String latestVersionString;
    private UpdateCheckResult updateCheckResult;

    public static final UpdateChecker instance = new UpdateChecker();
    public static UpdateChecker getInstance() {
        return instance;
    }

    private UpdateChecker() {}

    public void check(boolean isStartup, Player owner) {
        try {
            this.resourceId = Integer.parseInt("21424");
            this.resourceURL = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + resourceId);
        } catch (Exception e) {
            ParkourMakerPlugin.instance().getLogger().warning("Update checker is not ready yet!");
        }

        Bukkit.getScheduler().runTaskAsynchronously(ParkourMakerPlugin.instance().getAdapter(), () -> {
            currentVersionString = ParkourMakerPlugin.instance().getAdapter().getDescription().getVersion();
            latestVersionString = getLatestVersion();

            if (latestVersionString == null) {
                updateCheckResult = UpdateCheckResult.NO_RESULT;
            } else updateCheckResult = checkVersions();

            Bukkit.getScheduler().runTask(ParkourMakerPlugin.instance().getAdapter(), () -> {
                if (isStartup) onEnable();
                else onOwnerJoin(owner);
            });
        });
    }

    private UpdateCheckResult checkVersions() {
        String[] splitLocal = currentVersionString.replace("v", "").split("\\.");
        String[] splitLatest = latestVersionString.replace("v", "").split("\\.");

        for (int i = 0; i < splitLocal.length; i++) {
            int toIntLocal = Integer.parseInt(splitLocal[i]);
            int toIntLatest = Integer.parseInt(splitLatest[i]);

            if (toIntLatest > toIntLocal) {
                return UpdateCheckResult.OUT_DATED;
            } else if (toIntLatest == toIntLocal) {
                if (i == (splitLocal.length - 1)) {
                    return UpdateCheckResult.UP_TO_DATE;
                }
            } else {
                return UpdateCheckResult.UNRELEASED;
            }
        }
        return UpdateCheckResult.NO_RESULT;
    }

    public void onEnable() {
        switch (updateCheckResult) {
            case OUT_DATED:
                Bukkit.getConsoleSender().sendMessage(MessageManager.instance()
                    .translate("&4-----------------------------------------------------------"));
                Bukkit.getConsoleSender().sendMessage(MessageManager.instance()
                    .translate("       &eThere is a new version of &aParkour Maker&e(&av" + getLatestVersionString() + "&e)!"));
                Bukkit.getConsoleSender().sendMessage(MessageManager.instance()
                    .translate("           &eYou are running &cv" + getCurrentVersionString() + " &eof &aParkour Maker&e."));
                Bukkit.getConsoleSender().sendMessage(MessageManager.instance()
                    .translate("        &eMake sure to download the latest version at:"));
                Bukkit.getConsoleSender().sendMessage(MessageManager.instance()
                    .translate("          &bhttps://www.spigotmc.org/resources/" + resourceId));
                Bukkit.getConsoleSender().sendMessage(MessageManager.instance()
                    .translate("&4-----------------------------------------------------------"));
                break;
            case UP_TO_DATE:
                Bukkit.getConsoleSender().sendMessage(MessageManager.instance()
                    .translate(ProcessedConfigValue.of().messagePrefix() + "&eYou are running the latest version(" +
                        "&av." + getCurrentVersionString() + "&e) of &aParkour Maker&e, no update is needed."));
                break;
            case UNRELEASED:
                Bukkit.getConsoleSender().sendMessage(MessageManager.instance()
                    .translate(ProcessedConfigValue.of().messagePrefix() + "&eCurrent version(" +
                        "&av." + getCurrentVersionString() + "&e) of &aParkour Maker " +
                        "&eis not released yet, no support will be provided!"));
                break;
            case NO_RESULT:
                Bukkit.getConsoleSender().sendMessage(MessageManager.instance()
                    .translate("&4--------------------------------------------------------------------------"));
                Bukkit.getConsoleSender().sendMessage(MessageManager.instance()
                    .translate("&cCould not check if your version(" + "&av." + getCurrentVersionString() +
                        "&c) of &aParkour Maker " + "&cis up to date."));
                Bukkit.getConsoleSender().sendMessage(MessageManager.instance()
                    .translate("&cMake sure that you have access to the internet and that spigot is working."));
                Bukkit.getConsoleSender().sendMessage(MessageManager.instance()
                    .translate("&4--------------------------------------------------------------------------"));
                break;
            default:
        }
    }

    public void onOwnerJoin(Player owner) {
        if (updateCheckResult == UpdateCheckResult.OUT_DATED) {
            owner.sendMessage(MessageManager.instance().translate("&4-----------------------------------------------------"));
            owner.sendMessage(MessageManager.instance().translate("           &eThere is a new version of &aParkour Maker" +
                "&e(&av" + getLatestVersionString() + "&e)!"));
            owner.sendMessage(MessageManager.instance().translate("               &eYou are running &cv" +
                    getCurrentVersionString() + " &eof &aParkour Maker&e."));
            owner.sendMessage(MessageManager.instance().translate("            &eMake sure to download the latest version at:"));
            owner.sendMessage(MessageManager.instance().translate("             &bhttps://www.spigotmc.org/resources/" + resourceId));
            owner.sendMessage(MessageManager.instance().translate("&4-----------------------------------------------------"));
        }
    }

    public enum UpdateCheckResult {
        NO_RESULT, OUT_DATED, UP_TO_DATE, UNRELEASED,
    }

    public int getResourceId() {
        return resourceId;
    }

    public String getResourceURL() {
        return "https://www.spigotmc.org/resources/" + resourceId;
    }

    public String getCurrentVersionString() {
        return currentVersionString;
    }

    public String getLatestVersionString() {
        return latestVersionString;
    }

    public UpdateCheckResult getUpdateCheckResult() {
        return updateCheckResult;
    }

    public String getLatestVersion() {
        try {
            URLConnection urlConnection = resourceURL.openConnection();
            return new BufferedReader(new InputStreamReader(urlConnection.getInputStream())).readLine();
        } catch (Exception exception) {
            return null;
        }
    }

}