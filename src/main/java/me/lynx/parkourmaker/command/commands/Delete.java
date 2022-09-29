package me.lynx.parkourmaker.command.commands;

import me.lynx.parkourmaker.ParkourMakerPlugin;
import me.lynx.parkourmaker.command.ChildCommandBase;
import me.lynx.parkourmaker.command.MainCommand;
import me.lynx.parkourmaker.command.tabcomplete.Argument;
import me.lynx.parkourmaker.io.message.MessageManager;
import me.lynx.parkourmaker.model.map.ParkourMap;
import me.lynx.parkourmaker.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Delete extends ChildCommandBase {

    private final Map<String,Boolean> securityCheck;

    public Delete(MainCommand parentCommand) {
        super("Delete", parentCommand,
            "deletes a certain feature of map or entire parkour map",
            "/PM Delete <Feature> [Identifier]",
            "parkour-maker.command.delete",
            "del");
        securityCheck = new HashMap<>();
    }

    @Override
    public Set<Argument> onTabComplete() {
        Set<Argument> toReturn = new HashSet<>();

        toReturn.add(new Argument(1, Set.of("FinishTeleport", "Checkpoint", "Fallzone", "Reward", "Map")));
        toReturn.add(new Argument(2, Set.of())); /* Handled as special event in tab completer */
        return toReturn;
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (!hasPermission(sender, true)) return;
        if (!hasAllArgs(sender, (short) args.length, true)) return;
        if (!isPlayer(sender, true)) return;
        if (inEditorMode(sender, true) == null) return;
        ParkourMap map = inEditorMode(sender);

        FeatureType type;
        try {
            type = FeatureType.valueOf(args[1].toUpperCase());
        }  catch (IllegalArgumentException e) {
            MessageManager.instance().newMessage("invalid-type").type(args[1]).send(sender);
            return;
        }

        if (args.length < 3 && type != FeatureType.FINISHTELEPORT) {
            MessageManager.instance().newMessage("command-usage")
                .playerName(sender.getName())
                .commandUsage(usage)
                .send(sender);
            return;
        }


        String identifier = "";
        if (type == FeatureType.FINISHTELEPORT) {
            if (map.getFinishTeleportLocation() != null) map.setFinishTeleportLocation(null);
        } else if (type == FeatureType.CHECKPOINT) {
            try {
                int position = Integer.parseInt(args[2]);
                if (position < 1 || map.getCheckpoint(position) == null) {
                    MessageManager.instance().newMessage("invalid-position")
                        .checkpointPosition(args[2]).send(sender);
                    return;
                }
                identifier = map.getCheckpoint(position).getName();
                map.removeCheckpoint(position);
            } catch (NumberFormatException e) {
                MessageManager.instance().newMessage("invalid-position")
                    .checkpointPosition(args[2]).send(sender);
                return;
            }
        } else if (type == FeatureType.FALLZONE) {
            String name = Utils.argsToMessage(args, 2);
            if (map.getFallzone(name) == null) {
                MessageManager.instance().newMessage("invalid-name")
                    .fallzoneName(args[2]).send(sender);
                return;
            }
            identifier = name;
            map.removeFallzone(name);
        } else if (type == FeatureType.REWARD) {
            try {
                int id = Integer.parseInt(args[2]);
                if (map.getReward(id) == null) {
                    MessageManager.instance().newMessage("invalid-number")
                        .number(args[2]).send(sender);
                    return;
                }
                identifier = id + "";
                map.removeReward(id);
            } catch (NumberFormatException e) {
                MessageManager.instance().newMessage("invalid-number")
                    .number(args[2]).send(sender);
                return;
            }
        } else if (type == FeatureType.MAP) {
            if (!args[2].equals(map.getName())) {
                MessageManager.instance().newMessage("map-not-found").parkourName(args[2]).send(sender);
                return;
            }

            if (securityCheck.get(sender.getName()) == null) {
                securityCheck.put(sender.getName(), false);
                MessageManager.instance().newMessage("map-delete-security-check")
                    .parkourName(map.getDisplayName()).send(sender);

                Bukkit.getScheduler().scheduleSyncDelayedTask(ParkourMakerPlugin.instance().getAdapter(), () ->
                    securityCheck.put(sender.getName(), null), 20 * 30);
                return;
            } else {
                securityCheck.put(sender.getName(), null);
                map.disable();
                ParkourMakerPlugin.instance().getRunnerHandler().removeAllFromMap(map.getName());
                ParkourMakerPlugin.instance().getMapHandler().deleteMap(map.getName());
                identifier = map.getDisplayName();
            }
        }

        MessageManager.instance().newMessage("feature-deleted")
            .type(Utils.capitalizeFirstLetter(type.name()))
            .identifier(identifier)
            .send(sender);
    }

    private enum FeatureType {

        FINISHTELEPORT,
        CHECKPOINT,
        FALLZONE,
        REWARD,
        MAP

    }

}