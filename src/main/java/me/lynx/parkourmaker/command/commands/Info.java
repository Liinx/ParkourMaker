package me.lynx.parkourmaker.command.commands;

import me.lynx.parkourmaker.ParkourMakerPlugin;
import me.lynx.parkourmaker.command.ChildCommandBase;
import me.lynx.parkourmaker.command.MainCommand;
import me.lynx.parkourmaker.command.tabcomplete.Argument;
import me.lynx.parkourmaker.io.message.MessageManager;
import me.lynx.parkourmaker.model.map.ParkourMap;
import org.bukkit.command.CommandSender;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Info extends ChildCommandBase {

    public Info(MainCommand parentCommand) {
        super("Info", parentCommand,
            "show information about a map",
            "/PM Info <MapName>",
            "parkour-maker.command.info");
    }

    @Override
    public Set<Argument> onTabComplete() {
        Set<Argument> toReturn = new HashSet<>();

        toReturn.add(new Argument(1, ParkourMakerPlugin.instance().getMapHandler().getAllMapNames()));
        return toReturn;
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (!hasPermission(sender, true)) return;
        if (!hasAllArgs(sender, (short) args.length, true)) return;

        ParkourMap parkourMap = ParkourMakerPlugin.instance().getMapHandler().getByName(args[1]);
        if (parkourMap == null) {
            MessageManager.instance().newMessage("map-not-found").parkourName(args[1]).send(sender);
            return;
        }

        MessageManager.instance().newInternalMessage("Info about %parkour-name%:")
            .colorScheme(true).parkourName(parkourMap.getDisplayName()).send(sender);
        sender.sendMessage("");

        MessageManager.instance().newInternalMessage(" - Name: %number%")
            .colorScheme(true).removePrefix().number(parkourMap.getName()).send(sender);
        MessageManager.instance().newInternalMessage(" - Display Name: %number%")
            .colorScheme(true).removePrefix().number(parkourMap.getDisplayName()).send(sender);
        MessageManager.instance().newInternalMessage(" - Creator: %number%")
            .colorScheme(true).removePrefix().number(parkourMap.getCreator()).send(sender);
        MessageManager.instance().newInternalMessage(" - Status: %number%")
            .colorScheme(true).removePrefix().number(parkourMap.isEnabled() ? "Active" : "Disabled" ).send(sender);
        MessageManager.instance().newInternalMessage(" - Attempts: %number%")
            .colorScheme(true).number(parkourMap.getAttempts() + "").removePrefix().send(sender);

        MessageManager.instance().newInternalMessage(" - Start Position: %number%")
            .colorScheme(true).removePrefix().number(parkourMap.getStartLocation() != null ? "Set" : "Not Set" ).send(sender);
        MessageManager.instance().newInternalMessage(" - Finish Position: %number%")
            .colorScheme(true).removePrefix().number(parkourMap.getFinishLocation() != null ? "Set (" +
            parkourMap.getFinishLocation().getType().name().toLowerCase() + ")" : "Not Set").send(sender);
        MessageManager.instance().newInternalMessage(" - Finish Teleport: %number%")
            .colorScheme(true).removePrefix().number(parkourMap.getFinishTeleportLocation() != null ? "Set" : "Not Set").send(sender);

        if (parkourMap.getAllCheckpoints().isEmpty()) {
            MessageManager.instance().newInternalMessage(" - Checkpoints: %number%")
                .colorScheme(true).removePrefix().number("None").send(sender);
        } else {
            MessageManager.instance().newInternalMessage(" - Checkpoints:").colorScheme(true).removePrefix().send(sender);
            parkourMap.getAllCheckpoints().forEach(checkpoint -> {
                MessageManager.instance().newInternalMessage("   - %number%. %checkpoint-name%")
                    .colorScheme(true).number(checkpoint.getPosition() + "")
                    .checkpointName(checkpoint.getName() + " (" + checkpoint.getType().name().toLowerCase() + ")")
                    .removePrefix().send(sender);
            });
        }

        if (parkourMap.getAllFallzones().isEmpty()) {
            MessageManager.instance().newInternalMessage(" - Fall zones: %number%")
                .colorScheme(true).removePrefix().number("None").send(sender);
        } else {
            MessageManager.instance().newInternalMessage(" - Fall zones:").colorScheme(true).removePrefix().send(sender);
            parkourMap.getAllFallzones().forEach(fallzone -> {
                MessageManager.instance().newInternalMessage("   - %fallzone-name%")
                    .fallzoneName(fallzone.getName() + " (" + fallzone.getType().name().toLowerCase() + ")")
                    .colorScheme(true).removePrefix().send(sender);
            });
        }

        if (parkourMap.getAllRewards().isEmpty()) {
            MessageManager.instance().newInternalMessage(" - Rewards: %number%")
                .colorScheme(true).removePrefix().number("None").send(sender);
        } else {
            MessageManager.instance().newInternalMessage(" - Rewards:").colorScheme(true).removePrefix().send(sender);
            parkourMap.getAllRewards().forEach(reward -> {
                MessageManager.instance().newInternalMessage("   - %number%. '%command%'")
                    .number(reward.getId() + "")
                    .command(reward.getCommand())
                    .colorScheme(true).removePrefix().send(sender);
            });
        }

        MessageManager.instance().newInternalMessage(" - Cooldowns:").colorScheme(true).removePrefix().send(sender);
        MessageManager.instance().newInternalMessage("   - Join: %number%")
            .number(parkourMap.getJoinCooldown() + "")
            .colorScheme(true).removePrefix().send(sender);
        MessageManager.instance().newInternalMessage("   - Reward: %number%")
            .number(parkourMap.getRewardCooldown() + "")
            .colorScheme(true).removePrefix().send(sender);

        MessageManager.instance().newInternalMessage(" - Tittles:").colorScheme(true).removePrefix().send(sender);
        if (parkourMap.getStartMessage() == null) {
            MessageManager.instance().newInternalMessage("   - Start Message: %number%")
                .colorScheme(true).number("Not Set").removePrefix().send(sender);
        } else {
            MessageManager.instance().newInternalMessage("   - Start Message: '%number%'")
                .colorScheme(true).number(parkourMap.getStartMessage()).removePrefix().send(sender);
        }
        if (parkourMap.getFinishMessage() == null) {
            MessageManager.instance().newInternalMessage("   - Finish Message: %number%")
                .colorScheme(true).number("Not Set").removePrefix().send(sender);
        } else {
            MessageManager.instance().newInternalMessage("   - Finish Message: '%number%'")
                .colorScheme(true).number(parkourMap.getFinishMessage()).removePrefix().send(sender);
        }

        MessageManager.instance().newInternalMessage(" - Join Sign:").colorScheme(true).removePrefix().send(sender);
        for (int i = 1; i < 5; i++) {
            if (parkourMap.getSignText().getLine(i) == null) {
                MessageManager.instance().newInternalMessage("   - " + i + ". %number%")
                    .colorScheme(true).number("Not Set").removePrefix().send(sender);
            } else {
                MessageManager.instance().newInternalMessage("   - " + i + ". '%number%'")
                    .colorScheme(true).number(parkourMap.getSignText().getLine(i)).removePrefix().send(sender);
            }
        }

        if (parkourMap.getEditors().isEmpty()) {
            MessageManager.instance().newInternalMessage(" - Current Editors: %number%")
                    .colorScheme(true).removePrefix().number("None").send(sender);
        } else {
            MessageManager.instance().newInternalMessage(" - Current Editors: %number%")
                    .playerName(parkourMap.getEditors().toString().replaceAll("\\[|]", ""))
                    .colorScheme(true).removePrefix().send(sender);
        }

        if (ParkourMakerPlugin.instance().getRunnerHandler().getAllRunners().stream().anyMatch
            (runner -> runner.getMap() != null && runner.getMap().getName().equals(parkourMap.getName()))) {

            String playingNames = ParkourMakerPlugin.instance().getRunnerHandler().getAllRunners().stream()
                .filter(runner -> runner.getMap() != null)
                .filter(runner -> runner.getMap().getName().equals(parkourMap.getName()))
                .map(runner -> {
                    if (runner.getPlayer() == null) return "&c" + runner.getName();
                    else return "&a" + runner.getName();
                }).collect(Collectors.toSet()).toString().replaceAll("\\[|]", "");
            MessageManager.instance().newInternalMessage(" - Currently Playing: %number%")
                .number(playingNames).removePrefix().colorScheme(true).send(sender);
        } else {
            MessageManager.instance().newInternalMessage(" - Currently Playing: %number%")
                .colorScheme(true).number("No one").removePrefix().send(sender);
        }

    }

}