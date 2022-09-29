package me.lynx.parkourmaker.io.db.access;

import me.lynx.parkourmaker.model.map.*;
import me.lynx.parkourmaker.model.runner.Cooldown;
import me.lynx.parkourmaker.model.runner.RunTime;
import me.lynx.parkourmaker.model.runner.Runner;
import org.bukkit.Location;

import java.util.Map;

public interface AccessProvider {

    void setLobbyLocation(double x, double y, double z, float yaw, float pitch, String worldName);

    Location getLobbyLocation();

    void createNewMap(String name, String creator);

    void deleteMap(String name);

    void setStartLocation(String name, Location location);

    void setFinishLocation(String name, Selection selection);

    void setFinishTeleport(String name, Location location);

    void addCheckpoint(String name, Checkpoint checkpoint);

    void deleteCheckpoint(String name, int position);

    void addFallzone(String name, Fallzone fallzone);

    void deleteFallzone(String name, String zoneName);

    void addReward(String name, Reward reward);

    void deleteReward(String name, int id);

    void setStartMessage(String name, String message);

    void setFinishMessage(String name, String message);

    void addSignText(String name, int line, String message);

    void setActivity(String name, boolean activity);

    void setDisplayName(String name, String displayName);

    void setRewardCooldown(String name, long amount);

    void setJoinCooldown(String name, long amount);

    void deleteCooldownForAll(String name);

    void setRewardType(String name, RewardType type);

    void setAttempts(String name, int amount);

    ParkourMap constructMapFromStorage(String fileName);

    void insertRunner(String name);

    void addCooldown(String name, Cooldown cooldown);

    void addEnteredMap(String name, String mapName);

    void updateCheckpoint(String name, int checkpoint);

    void setRunnerAttempts(String name, int attempts);

    void setRunTimestamps(String name, RunTime runTime);

    void saveBestRunTime(String name, String mapName, String time);

    Runner constructRunnerFromStorage(String fileName);

    void onReload();

    String getBestTime(String playerName, String mapName);

    Map<String,String> getEveryoneBestTimes(String mapName);

    Map<String,String> getAllBestTimes(String playerName);

}