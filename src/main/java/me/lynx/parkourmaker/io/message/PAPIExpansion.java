package me.lynx.parkourmaker.io.message;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.lynx.parkourmaker.ParkourMakerPlugin;
import me.lynx.parkourmaker.model.map.ParkourMap;
import me.lynx.parkourmaker.model.runner.CooldownType;
import me.lynx.parkourmaker.model.runner.Runner;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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
        return "parkour_maker";
    }

    @Override
    public @NotNull String getVersion(){
        return plugin.getAdapter().getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (player == null) return null;
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

        return null;
    }

}