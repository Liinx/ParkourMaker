package me.lynx.parkourmaker.command;

import me.lynx.parkourmaker.ParkourMakerPlugin;
import me.lynx.parkourmaker.model.map.Fallzone;
import me.lynx.parkourmaker.model.map.ParkourMap;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class MainCommandTabComplete implements TabCompleter {

    private final List<ChildCommand> commands;

    public MainCommandTabComplete(List<ChildCommand> commands) {
        this.commands = commands;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> returnList = new ArrayList<>();

        if (args.length == 1) {
            if (args[0].equals("")) returnList.addAll(commands.stream()
                .filter(cmd -> sender.hasPermission(cmd.getPermission()))
                .map(cmd -> cmd.getName().toLowerCase()).collect(Collectors.toList()));
            else commands.stream()
                .filter(cmd -> sender.hasPermission(cmd.getPermission()))
                .map(cmd -> cmd.getName().toLowerCase())
                .filter(cmd -> cmd.toLowerCase().startsWith(args[0].toLowerCase()))
                .forEach(returnList::add);
        } else {
            AtomicBoolean foundCmd = new AtomicBoolean(false);

            commands.forEach(cmd -> {
                if (!cmd.getName().equalsIgnoreCase(args[0])) return;
                if (cmd.onTabComplete() == null) return;

                foundCmd.set(true);
                cmd.onTabComplete().forEach(argument -> {
                    int currentArg = argument.getPosition();
                    if (currentArg + 1 != args.length) return;

                    Set<String> possibilities = argument.getPossibilities();
                    possibilities = modifyPossibilitiesInSpecialEvents(possibilities, sender, cmd, args, currentArg);

                    if (args[currentArg].equals("")) returnList.addAll(possibilities);
                    else possibilities.stream()
                        .filter(possibility -> possibility.toLowerCase().startsWith(args[currentArg].toLowerCase()))
                        .forEach(returnList::add);
                });
            });

            /* does the same thing as above code only for cmd aliases */
            if (!foundCmd.get()) {
                commands.forEach(cmd -> {
                    if (cmd.onTabComplete() == null) return;

                    cmd.getAliases().forEach(al -> {
                        if (!al.equalsIgnoreCase(args[0])) return;

                        cmd.onTabComplete().forEach(argument -> {
                            int currentArg = argument.getPosition();
                            if (currentArg + 1 != args.length) return;

                            Set<String> possibilities = argument.getPossibilities();
                            possibilities = modifyPossibilitiesInSpecialEvents(possibilities, sender, cmd, args, currentArg);
                            if (args[currentArg].equals("")) returnList.addAll(possibilities);
                            else possibilities.stream()
                                .filter(possibility -> possibility.toLowerCase().startsWith(args[currentArg].toLowerCase()))
                                .forEach(returnList::add);
                        });
                    });
                });
            }
        }

        return returnList;
    }

    private Set<String> modifyPossibilitiesInSpecialEvents(Set<String> possibilities, CommandSender sender,
                                                           ChildCommand cmd, String[] args, int currentArg) {
        /* Exclusive only for help command to handle permissions */
        if (cmd.getName().equalsIgnoreCase("help")) {
            possibilities = possibilities.stream()
                .filter(possibility ->
                    sender.hasPermission(cmd.getParentCommand().getCommandByName(possibility).getPermission()))
                .collect(Collectors.toSet());
        }
        /* Exclusive only for join command to handle permissions */
        if (cmd.getName().equalsIgnoreCase("join")) {
            possibilities = possibilities.stream()
                .filter(possibility -> sender.hasPermission("parkour-maker.join." + possibility))
                .collect(Collectors.toSet());
        }
        /* Exclusive only for delete command to handle previous args */
        if (cmd.getName().equalsIgnoreCase("delete") && currentArg == 2) {
            ParkourMap map = ParkourMakerPlugin.instance().getMapHandler().getEditedMap(sender.getName());
            if (map == null) return possibilities;

            if (args[1].equalsIgnoreCase("FinishTeleport")) possibilities = new HashSet<>();
            else if (args[1].equalsIgnoreCase("Checkpoint")) {
                possibilities = map.getAllCheckpoints().stream()
                    .map(checkpoint -> checkpoint.getPosition() + "").collect(Collectors.toSet());
            } else if (args[1].equalsIgnoreCase("Fallzone")) {
                possibilities = map.getAllFallzones().stream()
                    .map(Fallzone::getName).collect(Collectors.toSet());
            } else if (args[1].equalsIgnoreCase("Reward")) {
                possibilities = map.getAllRewards().stream()
                    .map(reward -> reward.getId() + "").collect(Collectors.toSet());
            } else if (args[1].equalsIgnoreCase("Map")) possibilities = new HashSet<>();
        }
        return possibilities;
    }

}