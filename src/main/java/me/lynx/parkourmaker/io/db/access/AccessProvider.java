package me.lynx.parkourmaker.io.db.access;

import me.lynx.parkourmaker.model.map.*;
import me.lynx.parkourmaker.model.runner.Cooldown;
import me.lynx.parkourmaker.model.runner.Runner;
import org.bukkit.Location;

public interface AccessProvider {

    void setLobbyLocation(double x, double y, double z, float yaw, float pitch, String worldName);

    Location getLobbyLocation();

    void createNewMap(String name, String creator);

    void setStartLocation(String name, Location location);

    void setFinishLocation(String name, Selection selection);

    void setFinishTeleport(String name, Location location);

    void addCheckpoint(String name, Checkpoint checkpoint);

    void addFallzone(String name, Fallzone fallzone);

    void addReward(String name, Reward reward);

    void setStartMessage(String name, String message);

    void setFinishMessage(String name, String message);

    void addSignText(String name, int line, String message);

    void setActivity(String name, boolean activity);

    void setDisplayName(String name, String displayName);

    void setRewardCooldown(String name, long amount);

    void setJoinCooldown(String name, long amount);

    void setRewardType(String name, RewardType type);

    ParkourMap constructMapFromStorage(String fileName);

    void insertRunner(String name);

    void addCooldown(String name, Cooldown cooldown);

    void addEnteredMap(String name, String mapName);

    void updateCheckpoint(String name, int checkpoint);

    Runner constructRunnerFromStorage(String fileName);

    void onReload();

}