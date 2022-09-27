package me.lynx.parkourmaker.io.message;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.lynx.parkourmaker.ParkourMakerPlugin;
import me.lynx.parkourmaker.command.commands.Time;
import me.lynx.parkourmaker.io.file.ProcessedConfigValue;
import me.lynx.parkourmaker.model.map.ParkourMap;
import me.lynx.parkourmaker.model.runner.CooldownType;
import me.lynx.parkourmaker.model.runner.Runner;
import me.lynx.parkourmaker.util.Utils;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class PAPIExpansion extends PlaceholderExpansion {

    private final ParkourMakerPlugin plugin = ParkourMakerPlugin.instance();

    public PAPIExpansion() {}

    @Override
    public boolean persist(){
        return true;
    }

    @Override
    public boolean canRegister(){
        return true;
    }

    @Override
    public @NotNull String getAuthor(){
        return plugin.getAdapter().getDescription().getAuthors().toString();
    }

    @Override
    public @NotNull String getIdentifier(){
        return "parkourmaker";
    }

    @Override
    public @NotNull String getVersion(){
        return plugin.getAdapter().getDescription().getVersion();
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String identifier) {
        Runner runner = ParkourMakerPlugin.instance().getRunnerHandler().getRunnerFromPlayer(player.getName());

        if (identifier.equals("player_name")) {
            return runner.getName();
        }

        ParkourMap map = runner.getMap();
        if (map != null) {
            if (identifier.equals("parkour_name")) {
                return runner.getMap().getDisplayName();
            }

            if (identifier.equals("current_checkpoint")) {
                return map.getCheckpoint(runner.getCurrentCheckpoint()).getName();
            }

            if (identifier.equals("current_checkpoint_position")) {
                return runner.getCurrentCheckpoint() + "";
            }

            if (identifier.equals("join_cooldown")) {
                return runner.getCooldown(map.getName(), CooldownType.JOIN).getTimeLeft() + "";
            }

            if (identifier.equals("reward_cooldown")) {
                return runner.getCooldown(map.getName(), CooldownType.REWARD).getTimeLeft() + "";
            }

            if (identifier.equals("start_message")) {
                return map.getStartMessage();
            }

            if (identifier.equals("finish_message")) {
                return map.getFinishMessage();
            }

            if (identifier.equals("map_join_cooldown")) {
                return map.getJoinCooldown() + "";
            }

            if (identifier.equals("map_reward_cooldown")) {
                return map.getRewardCooldown() + "";
            }

        }

        if (identifier.equals("current_checkpoint_position")) {
            return 0 + "";
        }

        AtomicReference<String> leaderboardReturn = new AtomicReference<>(null);
        ParkourMakerPlugin.instance().getMapHandler().getAllMapNames().forEach(mapName -> {
            if (identifier.startsWith("leaderboard_" + mapName)) {
                try {
                    int position = Integer.parseInt(identifier.split("\\.")[1]);

                    Map<String,String> leaderboard = plugin.getStorage().getEveryoneBestTimes(mapName);
                    Map<String,Long> sortedBoard = Time.setPlacesInOrder(leaderboard);
                    Map.Entry<String,Long> v = sortedBoard.entrySet().stream().skip(position - 1).findFirst().orElse(null);
                    if (v != null) leaderboardReturn.set(
                        MessageManager.instance().newInternalMessage(
                            ProcessedConfigValue.of().papiLeaderboardFormat())
                                .number(position + "")
                                .playerName(v.getKey())
                                .runTime(Utils.toReadableTime(v.getValue(), true))
                                .removePrefix()
                                .colorScheme(false)
                                .getFormattedText());

                } catch (NumberFormatException e) { } /* Ignored */
            }

            if (identifier.startsWith("best_time_" + mapName)) {
                Runner foundRunner = ParkourMakerPlugin.instance().getRunnerHandler()
                    .getRunnerFromPlayer(identifier.split("\\.")[1]);
                if (foundRunner != null) {
                    String time = ParkourMakerPlugin.instance().getStorage().getBestTime(foundRunner.getName(), mapName);
                    if (time != null) leaderboardReturn.set(time);
                }
            }

        });

        if  (identifier.startsWith("leaderboard_") || identifier.startsWith("best_time_")) {
            if (leaderboardReturn.get() != null) return leaderboardReturn.get();
            else return "";
        }

        return null;
    }

}