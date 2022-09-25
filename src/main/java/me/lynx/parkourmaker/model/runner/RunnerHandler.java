package me.lynx.parkourmaker.model.runner;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import me.lynx.parkourmaker.ParkourMakerPlugin;
import me.lynx.parkourmaker.io.message.Message;
import me.lynx.parkourmaker.io.message.MessageManager;
import me.lynx.parkourmaker.model.map.*;
import me.lynx.parkourmaker.util.TitleManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RunnerHandler implements Listener {

    private final Set<Runner> runners;

    public RunnerHandler() {
        runners = new HashSet<>();
    }

    public Runner addRunner(Player player) {
        Supplier<Stream<Runner>> supplier = () -> runners.stream()
            .filter(runner -> runner.getName().equalsIgnoreCase(player.getName()));

        Runner runner;
        if (supplier.get().findAny().isEmpty()) {
            runner = new Runner(player.getName());
            runners.add(runner);
            ParkourMakerPlugin.instance().getStorage().insertRunner(runner.getName());
        } else runner = supplier.get().findFirst().get();

        return runner;
    }

    public void addFromStorageRunner(Runner runner) {
        runners.add(runner);
    }

    public Runner getRunnerFromPlayer(String playerName) {
        Supplier<Stream<Runner>> supplier = () -> runners.stream()
            .filter(runner -> runner.getName().equalsIgnoreCase(playerName));
        if (supplier.get().findAny().isEmpty()) return null;
        return supplier.get().findFirst().get();
    }

    public boolean isInMap(String playerName) {
        Runner runner = getRunnerFromPlayer(playerName);
        if (runner == null) return false;
        return runner.getMap() != null;
    }

    @EventHandler
    private void onFinishLine(PlayerMoveEvent e) {
        if (!isInMap(e.getPlayer().getName())) return;
        if (coordinateEquals(e.getFrom(), e.getTo())) return;
        Runner runner = getRunnerFromPlayer(e.getPlayer().getName());
        ParkourMap map = runner.getMap();
        Selection finishSelection = map.getFinishLocation();

        if (selectionReached(finishSelection, e.getTo())) {
            Location lobbyLoc = ParkourMakerPlugin.instance().getStorage().getLobbyLocation();
            Location finishTeleport = map.getFinishTeleportLocation();
            if (finishTeleport != null) e.getPlayer().teleport(finishTeleport);
            else e.getPlayer().teleport(lobbyLoc);

            Cooldown cooldown = runner.getCooldown(map.getName(), CooldownType.REWARD);
            if (!e.getPlayer().hasPermission("parkour-maker.ignore-cooldown.reward") &&
                (cooldown != null && !cooldown.cooldownExpired())) {
                MessageManager.instance().newMessage("reward-cooldown")
                    .cooldown(cooldown.getTimeLeft() + "")
                    .parkourName(map.getDisplayName())
                    .send(e.getPlayer());
            } else {
                if (map.getRewardType() == RewardType.ALL) {
                    map.getAllRewards().forEach(rew ->
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                            MessageManager.instance().newInternalMessage(rew.getCommand())
                                .playerName(e.getPlayer().getName())
                                .removePrefix()
                                .colorScheme(false)
                                .getFormattedText()));
                } else if (map.getRewardType() == RewardType.RANDOM) {
                    Set<Integer> ids = map.getAllRewards().stream().map(Reward::getId)
                        .collect(Collectors.toSet());
                    int rand = ThreadLocalRandom.current().nextInt(ids.size());
                    int option = ids.toArray(Integer[]::new)[rand];
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                        MessageManager.instance().newInternalMessage(map.getReward(option).getCommand())
                            .playerName(e.getPlayer().getName())
                            .removePrefix()
                            .colorScheme(false)
                            .getFormattedText());
                }
                runner.addCooldown(map.getName(), CooldownType.REWARD, map.getRewardCooldown());
            }

            if (map.getFinishMessage() != null) {
                TitleManager.displayTitle(e.getPlayer(),
                    MessageManager.instance().newInternalMessage(map.getFinishMessage())
                    .removePrefix()
                    .parkourName(map.getDisplayName())
                    .playerName(runner.getName()));
            }
            MessageManager.instance().newMessage("finished-map")
                .parkourName(runner.getMap().getDisplayName()).send(e.getPlayer());
            runner.quitMap();
        }
    }

    @EventHandler
    private void onCheckpoint(PlayerMoveEvent e) {
        if (!isInMap(e.getPlayer().getName())) return;
        if (coordinateEquals(e.getFrom(), e.getTo())) return;
        Runner runner = getRunnerFromPlayer(e.getPlayer().getName());

        List<Checkpoint> checkpoints = runner.getMap().getAllCheckpoints();
        checkpoints.forEach(checkpoint -> {
            if (checkpoint.getPosition() <= runner.getCurrentCheckpoint()) return;

            if (selectionReached(checkpoint, e.getTo())) {
                runner.setCurrentCheckpoint(checkpoint.getPosition());
                MessageManager.instance().newMessage("checkpoint-reached")
                    .checkpointName(checkpoint.getName())
                    .checkpointPosition(checkpoint.getPosition() + "")
                    .parkourName(runner.getMap().getDisplayName())
                    .send(e.getPlayer());
            }
        });
    }

    @EventHandler
    private void onFallzone(PlayerMoveEvent e) {
        if (!isInMap(e.getPlayer().getName())) return;
        if (coordinateEquals(e.getFrom(), e.getTo())) return;
        Runner runner = getRunnerFromPlayer(e.getPlayer().getName());

        List<Fallzone> fallzones = runner.getMap().getAllFallzones();
        fallzones.forEach(fallzone -> {
            if (selectionReached(fallzone, e.getTo())) {
                Message message = MessageManager.instance().newMessage("fall-in-fallzone")
                    .fallzoneName(fallzone.getName());

                if (runner.getMap().getAttempts() > 0) {
                    int attemptsLeft = runner.getAttempts();
                    if (attemptsLeft > 0) {
                        runner.setAttempts(attemptsLeft - 1);
                        MessageManager.instance().newMessage("lost-attempt")
                            .playerName(e.getPlayer().getName())
                            .parkourName(runner.getMap().getDisplayName())
                            .amount(runner.getAttempts() + "")
                            .send(e.getPlayer());
                    } else {
                        Location lobbyLoc = ParkourMakerPlugin.instance().getStorage().getLobbyLocation();
                        e.getPlayer().teleport(lobbyLoc);
                        MessageManager.instance().newMessage("no-attempts-left")
                            .playerName(e.getPlayer().getName())
                            .parkourName(runner.getMap().getDisplayName())
                            .amount(runner.getAttempts() + "")
                            .send(e.getPlayer());
                        runner.quitMap();
                        return;
                    }
                }

                if (runner.getCurrentCheckpoint() == 0) {
                    e.getPlayer().teleport(runner.getMap().getStartLocation());
                    message.checkpointName("Start").checkpointPosition(0 + "");
                } else {
                    Checkpoint checkpoint = runner.getMap().getCheckpoint(runner.getCurrentCheckpoint());
                    e.getPlayer().teleport(checkpoint.getTeleportLocation());
                    message.checkpointName(checkpoint.getName()).checkpointPosition(checkpoint.getPosition() + "");
                }

                message.parkourName(runner.getMap().getDisplayName()).send(e.getPlayer());
            }
        });
    }

    public boolean selectionReached(Selection selection, Location currentLocation) {
        if (selection.getType() == SelectionType.SINGLE) {
            return coordinateEquals(selection.getStartPoint(), currentLocation);
        } else if (selection.getType() == SelectionType.MULTI) {
            BlockVector3 start = BukkitAdapter.asBlockVector(selection.getStartPoint());
            BlockVector3 end = BukkitAdapter.asBlockVector(selection.getEndPoint());

            Region region = new CuboidRegion(BukkitAdapter.adapt(selection.getStartPoint().getWorld()), start, end);
            return region.contains(BukkitAdapter.asBlockVector(currentLocation));
        }
        return false;
    }

    public boolean coordinateEquals(Location loc1, Location loc2) {
        int x1 = loc1.getBlockX();
        int y1 = loc1.getBlockY();
        int z1 = loc1.getBlockZ();
        String world1 = loc1.getWorld().getName();

        int x2 = loc2.getBlockX();
        int y2 = loc2.getBlockY();
        int z2 = loc2.getBlockZ();
        String world2 = loc2.getWorld().getName();

        return x1 == x2 && y1 == y2 && z1 == z2 && world1.equalsIgnoreCase(world2);
    }

}