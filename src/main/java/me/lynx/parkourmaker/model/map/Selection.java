package me.lynx.parkourmaker.model.map;

import org.bukkit.Location;

public class Selection {

    protected SelectionType type;
    protected Location startPoint;
    protected Location endPoint;

    public Selection() {
        type = SelectionType.SINGLE;
    }

    public Selection(Location startPoint, Location endPoint) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        type = SelectionType.MULTI;
    }

    public Selection(Location startPoint) {
        this.startPoint = startPoint;
        this.endPoint = null;
        type = SelectionType.SINGLE;
    }

    public SelectionType getType() {
        return type;
    }

    public Location getStartPoint() {
        return startPoint;
    }

    public Location getEndPoint() {
        return endPoint;
    }

    public void setType(SelectionType type) {
        this.type = type;
    }

    public void setStartPoint(Location startPoint) {
        this.startPoint = startPoint;
    }

    public void setEndPoint(Location endPoint) {
        type = SelectionType.MULTI;
        this.endPoint = endPoint;
    }

}