package me.lynx.parkourmaker.model.map;

import org.bukkit.Location;

public class Checkpoint extends Selection {

    private int position;
    private String name;
    private Location teleportLocation;
    private ParkourMap owningMap;

    public Checkpoint(ParkourMap owningMap) {
        this.owningMap = owningMap;
    }

    public Checkpoint() {}

    public void setTeleportLocation(Location teleportLocation) {
        this.teleportLocation = teleportLocation;
    }

    public Location getTeleportLocation() {
        if (type == SelectionType.MULTI) return teleportLocation;
        else return startPoint;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getPosition() {
        return position;
    }

    public ParkourMap getOwningMap() {
        return owningMap;
    }

    public void setOwningMap(ParkourMap owningMap) {
        this.owningMap = owningMap;
    }

}
