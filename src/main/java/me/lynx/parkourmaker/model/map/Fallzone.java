package me.lynx.parkourmaker.model.map;

import org.bukkit.Location;

public class Fallzone extends Selection {

    private String name;

    public Fallzone(String name, Location startPoint, Location endPoint) {
        super(startPoint, endPoint);
        this.name = name;
    }

    public String getName() {
        return name;
    }

}