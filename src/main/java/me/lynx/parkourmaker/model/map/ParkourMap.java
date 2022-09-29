package me.lynx.parkourmaker.model.map;

import me.lynx.parkourmaker.ParkourMakerPlugin;
import me.lynx.parkourmaker.model.sign.SignText;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class ParkourMap {

    private boolean operational;
    private boolean enabled;
    private final String name;
    private String displayName;
    private final String creator;
    private final Set<String> editors;
    private Location startLocation;
    private Selection finishLocation;
    private Location finishTeleportLocation;
    private String startMessage;
    private String finishMessage;
    private SignText signText;
    private long joinCooldown;
    private long rewardCooldown;
    private RewardType rewardType;
    private int attempts;

    private final List<Checkpoint> checkpoints;
    private final List<Fallzone> fallzones;
    private final List<Reward> rewards;

    protected ParkourMap(String name, String creator) {
        this.name = name;
        this.creator = creator;

        editors = new HashSet<>();
        checkpoints = new ArrayList<>();
        fallzones = new ArrayList<>();
        rewards = new ArrayList<>();

        editors.add(creator);

        startLocation = null;
        finishLocation = null;
        finishTeleportLocation = null;
        signText = null;
        startMessage = null;
        finishMessage = null;
        displayName = null;
        joinCooldown = 0;
        rewardCooldown = 0;
        attempts = 0;
        rewardType = RewardType.ALL;

        operational = false;
        enabled = false;
    }

    /**
     * Constructor for making map from storage!
     */
    public ParkourMap(String name, String creator, List<Checkpoint> checkpoints, List<Fallzone> fallzones,
            List<Reward> rewards, Location startLocation, Selection finishLocation, Location finishTeleportLocation,
             SignText signText, String startMessage, String finishMessage, boolean enabled, String displayName,
              long joinCooldown, long rewardCooldown, RewardType rewardType, int attempts) {
        this.name = name;
        this.creator = creator;
        this.checkpoints = checkpoints;
        this.fallzones = fallzones;
        this.rewards = rewards;
        this.startLocation = startLocation;
        this.finishLocation = finishLocation;
        this.finishTeleportLocation = finishTeleportLocation;
        this.signText = signText;
        this.startMessage = startMessage;
        this.finishMessage = finishMessage;
        this.displayName = displayName;
        this.joinCooldown = joinCooldown;
        this.rewardCooldown = rewardCooldown;
        this.rewardType = rewardType;
        this.attempts = attempts;

        editors = new HashSet<>();
        checkIfOperational();
        this.enabled = operational && enabled;
    }

    public int getAttempts() {
        return attempts;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
        ParkourMakerPlugin.instance().getStorage().setAttempts(name, attempts);
    }

    public void setRewardType(RewardType rewardType) {
        this.rewardType = rewardType;
        ParkourMakerPlugin.instance().getStorage().setRewardType(name, rewardType);
    }

    public RewardType getRewardType() {
        return rewardType;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        ParkourMakerPlugin.instance().getStorage().setDisplayName(name, displayName);
    }

    public String getDisplayName() {
        if (displayName != null) return displayName;
        else return name;
    }

    public String getCreator() {
        return creator;
    }

    public void setJoinCooldown(long joinCooldown) {
        this.joinCooldown = joinCooldown;
        ParkourMakerPlugin.instance().getStorage().setJoinCooldown(name, joinCooldown);
    }

    public void setRewardCooldown(long rewardCooldown) {
        this.rewardCooldown = rewardCooldown;
        ParkourMakerPlugin.instance().getStorage().setRewardCooldown(name, rewardCooldown);
    }

    public long getJoinCooldown() {
        return joinCooldown;
    }

    public long getRewardCooldown() {
        return rewardCooldown;
    }

    public SignText getSignText() {
        if (signText == null) signText = new SignText(this);
        return signText;
    }

    public String getStartMessage() {
        return startMessage;
    }

    public String getFinishMessage() {
        return finishMessage;
    }

    public String getName() {
        return name;
    }

    public Set<String> getEditors() {
        return editors;
    }

    public void addEditor(String editor) {
        editors.add(editor);
    }

    public void removeEditor(String editor) {
        editors.remove(editor);
    }

    public void setStartLocation(Location startLocation) {
        this.startLocation = startLocation;
        ParkourMakerPlugin.instance().getStorage().setStartLocation(name, startLocation);
        checkIfOperational();
    }

    public void setFinishLocation(Selection finishLocation) {
        this.finishLocation = finishLocation;
        ParkourMakerPlugin.instance().getStorage().setFinishLocation(name, finishLocation);
        checkIfOperational();
    }

    public void setStartMessage(String startMessage) {
        if (startMessage.isEmpty()) startMessage = null;
        this.startMessage = startMessage;
        ParkourMakerPlugin.instance().getStorage().setStartMessage(name, startMessage);
    }

    public void setFinishMessage(String finishMessage) {
        if (finishMessage.isEmpty()) finishMessage = null;
        this.finishMessage = finishMessage;
        ParkourMakerPlugin.instance().getStorage().setFinishMessage(name, finishMessage);
    }

    public void removeCheckpoint(int position) {
        checkpoints.removeIf(cp -> cp.getPosition() == position);
        ParkourMakerPlugin.instance().getStorage().deleteCheckpoint(name, position);
    }

    public void addCheckpoint(Checkpoint checkpoint) {
        checkpoints.removeIf(cp -> cp.getPosition() == checkpoint.getPosition());
        checkpoints.add(checkpoint);
        ParkourMakerPlugin.instance().getStorage().addCheckpoint(name, checkpoint);
    }

    public List<Checkpoint> getAllCheckpoints() {
        return checkpoints;
    }

    public Checkpoint getCheckpoint(int position) {
        Supplier<Stream<Checkpoint>> supplier = () -> checkpoints.stream()
                .filter(cp -> cp.getPosition() == position);
        if (supplier.get().findAny().isEmpty()) return null;
        return supplier.get().findFirst().get();
    }

    public void removeFallzone(String zoneName) {
        fallzones.removeIf(fz -> fz.getName().equalsIgnoreCase(zoneName));
        ParkourMakerPlugin.instance().getStorage().deleteFallzone(name, zoneName);
    }

    public void addFallzone(Fallzone fallzone) {
        fallzones.removeIf(fz -> fz.getName().equalsIgnoreCase(fallzone.getName()));
        fallzones.add(fallzone);
        ParkourMakerPlugin.instance().getStorage().addFallzone(name, fallzone);
    }

    public Fallzone getFallzone(String zoneName) {
        Supplier<Stream<Fallzone>> supplier = () -> fallzones.stream()
            .filter(fz -> fz.getName().equals(zoneName));
        if (supplier.get().findAny().isEmpty()) return null;
        return supplier.get().findFirst().get();
    }

    public List<Fallzone> getAllFallzones() {
        return fallzones;
    }

    public void removeReward(int id) {
        rewards.removeIf(reward -> reward.getId() == id);
        ParkourMakerPlugin.instance().getStorage().deleteReward(name, id);
    }

    public void addReward(String command) {
        Reward reward = new Reward(this, command);
        rewards.add(reward);
        ParkourMakerPlugin.instance().getStorage().addReward(name, reward);
    }

    public List<Reward> getAllRewards() {
        return rewards;
    }

    public Reward getReward(int id) {
        Supplier<Stream<Reward>> supplier = () -> rewards.stream()
            .filter(rew -> rew.getId() == id);
        if (supplier.get().findAny().isEmpty()) return null;
        return supplier.get().findFirst().get();
    }

    public void setFinishTeleportLocation(Location finishTeleportLocation) {
        this.finishTeleportLocation = finishTeleportLocation;
        ParkourMakerPlugin.instance().getStorage().setFinishTeleport(name, finishTeleportLocation);
    }

    public Location getFinishTeleportLocation() {
        return finishTeleportLocation;
    }

    public void checkIfOperational() {
        operational = startLocation != null && finishLocation != null;
    }

    public boolean enable() {
        if (operational) {
            enabled = true;
            ParkourMakerPlugin.instance().getStorage().setActivity(name, enabled);
            return true;
        }
        return false;
    }

    public void disable() {
        enabled = false;
        ParkourMakerPlugin.instance().getStorage().setActivity(name, enabled);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Selection getFinishLocation() {
        return finishLocation;
    }

    public Location getStartLocation() {
        return startLocation;
    }

}