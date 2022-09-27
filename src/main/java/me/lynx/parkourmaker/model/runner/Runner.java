package me.lynx.parkourmaker.model.runner;

import me.lynx.parkourmaker.ParkourMakerPlugin;
import me.lynx.parkourmaker.io.message.MessageManager;
import me.lynx.parkourmaker.model.map.ParkourMap;
import me.lynx.parkourmaker.util.TitleManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class Runner {

    private final String name;
    private ParkourMap map;
    private int currentCheckpoint;
    private int attempts;
    private final List<Cooldown> cooldowns;
    private RunTime runTime;

    protected Runner(String name) {
        this.name = name;
        map = null;
        currentCheckpoint = 0;
        attempts = 0;
        cooldowns = new ArrayList<>();
        runTime = new RunTime(name);
    }

    public Runner(String name, String enteredMap, int currentCheckpoint, int attempts, List<Cooldown> cooldowns, RunTime runTime) {
        this.name = name;
        map = ParkourMakerPlugin.instance().getMapHandler().getByName(enteredMap);
        this.currentCheckpoint = currentCheckpoint;
        this.attempts = attempts;
        this.cooldowns = cooldowns;
        this.runTime = runTime;
    }

    public void joinMap(ParkourMap map) {
        this.map = map;
        ParkourMakerPlugin.instance().getStorage().addEnteredMap(name, map.getName());
        teleportToStart();
        runTime.start();
        addCooldown(map.getName(), CooldownType.JOIN, map.getJoinCooldown());
        setAttempts(map.getAttempts());

        if (map.getStartMessage() != null) {
            TitleManager.displayTitle(getPlayer(),
                MessageManager.instance().newInternalMessage(map.getStartMessage())
                .removePrefix()
                .parkourName(map.getDisplayName())
                .playerName(name));
        }
    }

    public void addCooldown(String mapName, CooldownType type, long amount) {
        Cooldown foundCooldown = getCooldown(mapName, type);
        if (foundCooldown == null) {
            foundCooldown = new Cooldown(name, mapName, type);
            cooldowns.add(foundCooldown);
        }
        foundCooldown.start();
    }

    public Cooldown getCooldown(String mapName, CooldownType type) {
        Supplier<Stream<Cooldown>> supplier = () -> cooldowns.stream()
            .filter(cd -> cd.getType() == type)
            .filter(cd -> cd.getMapName().equalsIgnoreCase(mapName));

        if (supplier.get().findAny().isEmpty()) return null;
        return supplier.get().findFirst().get();
    }

    public void setCurrentCheckpoint(int currentCheckpoint) {
        this.currentCheckpoint = currentCheckpoint;
        ParkourMakerPlugin.instance().getStorage().updateCheckpoint(name, currentCheckpoint);
    }

    /**
     * Returns current checkpoint position only.
     */
    public int getCurrentCheckpoint() {
        return currentCheckpoint;
    }

    public RunTime getRunTime() {
        return runTime;
    }

    public int getAttempts() {
        return attempts;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
        ParkourMakerPlugin.instance().getStorage().setRunnerAttempts(name, attempts);
    }

    public void teleportToStart() {
        getPlayer().teleport(map.getStartLocation());
    }

    /**
     * Only use for cases where you expect the player to be online.
     */
    public Player getPlayer() {
        List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
        Supplier<Stream<Player>> supplier = () -> onlinePlayers.stream()
            .filter(player -> player.getName().equals(name));

        if (supplier.get().findAny().isEmpty()) return null;
        return supplier.get().findFirst().get();
    }

    public String getName() {
        return name;
    }

    public ParkourMap getMap() {
        return map;
    }

    public List<Cooldown> getAllCooldowns() {
        return cooldowns;
    }

    public void quitMap() {
        map = null;
        setCurrentCheckpoint(0);
        setAttempts(0);
        runTime.clear();
        ParkourMakerPlugin.instance().getStorage().addEnteredMap(name, null);
    }

}