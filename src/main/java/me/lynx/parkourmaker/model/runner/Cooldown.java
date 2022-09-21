package me.lynx.parkourmaker.model.runner;

import me.lynx.parkourmaker.ParkourMakerPlugin;
import me.lynx.parkourmaker.model.map.ParkourMap;

public class Cooldown {

    private final String mapName;
    private String owningRunner;
    private final CooldownType type;
    private long startTime;

    public Cooldown(String owningRunner, String mapName, CooldownType type) {
        this.owningRunner = owningRunner;
        this.mapName = mapName;
        this.type = type;
    }

    public Cooldown(String mapName, CooldownType type, long startTime) {
        this.mapName = mapName;
        this.type = type;
        this.startTime = startTime;
    }

    public void setOwningRunner(String owningRunner) {
        this.owningRunner = owningRunner;
    }

    public long getStartTime() {
        return startTime;
    }

    public void start() {
        startTime = System.currentTimeMillis();
        ParkourMakerPlugin.instance().getStorage().addCooldown(owningRunner, this);
    }

    public long getTimeLeft() {
        return getCooldownDuration() - getTimePassed();
    }

    public long getTimePassed() {
        return (System.currentTimeMillis() - startTime) / 1000;
    }

    public boolean cooldownExpired() {
        return getTimePassed() > getCooldownDuration();
    }

    public long getCooldownDuration() {
        ParkourMap map = ParkourMakerPlugin.instance().getMapHandler().getByName(mapName);
        if (type == CooldownType.JOIN) return map.getJoinCooldown();
        else return map.getRewardCooldown();
    }

    public CooldownType getType() {
        return type;
    }

    public String getMapName() {
        return mapName;
    }

}