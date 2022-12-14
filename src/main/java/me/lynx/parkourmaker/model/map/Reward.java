package me.lynx.parkourmaker.model.map;

public class Reward {

    private ParkourMap owningMap;
    private String command;
    private int id;

    protected Reward(ParkourMap owningMap, String command) {
        this.command = command;
        this.owningMap = owningMap;
        id = generateID();
    }

    public Reward(int id, String command) {
        this.id = id;
        this.command = command;
    }

    public ParkourMap getOwningMap() {
        return owningMap;
    }

    public void setOwningMap(ParkourMap owningMap) {
        this.owningMap = owningMap;
    }

    public String getCommand() {
        return command;
    }

    public int getId() {
        return id;
    }

    private int generateID() {
        if (owningMap.getAllRewards().size() < 1) return 1;
        return owningMap.getAllRewards().stream().map(Reward::getId).mapToInt(v -> v).max().getAsInt() + 1;
    }

}