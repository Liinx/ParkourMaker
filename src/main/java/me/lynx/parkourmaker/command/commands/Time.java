package me.lynx.parkourmaker.command.commands;

import me.lynx.parkourmaker.ParkourMakerPlugin;
import me.lynx.parkourmaker.command.ChildCommandBase;
import me.lynx.parkourmaker.command.MainCommand;
import me.lynx.parkourmaker.command.tabcomplete.Argument;
import me.lynx.parkourmaker.io.message.MessageManager;
import me.lynx.parkourmaker.model.map.ParkourMap;
import me.lynx.parkourmaker.model.runner.Runner;
import me.lynx.parkourmaker.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Time extends ChildCommandBase {

    public Time(MainCommand parentCommand) {
        super("Time", parentCommand,
            "displays best time for maps",
            "/PM Time <MapName/PlayerName> [PlayerName]",
            "parkour-maker.command.time",
            "toptime");
    }

    @Override
    public Set<Argument> onTabComplete() {
        Set<Argument> toReturn = new HashSet<>();

        Set<String> firstPossibilities = new HashSet<>();
        firstPossibilities.addAll(ParkourMakerPlugin.instance().getMapHandler().getAllMapNames());
        firstPossibilities.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toSet()));

        toReturn.add(new Argument(1, firstPossibilities));
        toReturn.add(new Argument(2, Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toSet())));
        return toReturn;
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (!hasPermission(sender, true)) return;
        if (!hasAllArgs(sender, (short) args.length, true)) return;
        ParkourMakerPlugin plugin = ParkourMakerPlugin.instance();

        /* 0 = invalid, 1 = map name only, 2 = player name only, 3 = map and player name */
        int typeUsed = 0;

        ParkourMap parkourMap = plugin.getMapHandler().getByName(args[1]);
        Runner runner = plugin.getRunnerHandler().getRunnerFromPlayer(args[1]);
        if (runner == null && args.length > 2) runner = plugin.getRunnerHandler().getRunnerFromPlayer(args[2]);

        if (parkourMap == null && runner == null) typeUsed = 0;
        if (parkourMap != null && runner == null) typeUsed = 1;
        if (parkourMap == null && runner != null) typeUsed = 2;
        if (parkourMap != null && runner != null) typeUsed = 3;

        if (typeUsed == 0) {
            MessageManager.instance().newMessage("invalid-parkour-and-map-name")
                .parkourName(args[1])
                .playerName(args.length > 2 ? args[2] : args[1])
                .send(sender);
        } else if (typeUsed == 1) {
            MessageManager.instance().newMessage("map-best-time-tittle")
                .parkourName(parkourMap.getDisplayName()).send(sender);
            sender.sendMessage("");
            Map<String,String> leaderboard = plugin.getStorage().getEveryoneBestTimes(parkourMap.getName());
            Map<String,Long> sortedBoard = setPlacesInOrder(leaderboard);

            final AtomicInteger counter = new AtomicInteger(0);
            sortedBoard.entrySet().stream()
                .limit(10)
                .forEachOrdered(entry ->
                    MessageManager.instance().newMessage("map-best-time-line")
                        .number(counter.incrementAndGet() + "")
                        .playerName(entry.getKey())
                        .runTime(Utils.toReadableTime(entry.getValue(), true))
                        .removePrefix()
                        .send(sender));
        } else if (typeUsed == 2) {
            MessageManager.instance().newMessage("player-best-time-tittle")
                    .playerName(runner.getName()).send(sender);
            sender.sendMessage("");
            Map<String,String> leaderboard = plugin.getStorage().getAllBestTimes(runner.getName());
            Map<String,Long> sortedBoard = setPlacesInOrder(leaderboard);

            final AtomicInteger counter = new AtomicInteger(0);
            sortedBoard.entrySet().stream()
                .limit(10)
                .forEachOrdered(entry ->
                    MessageManager.instance().newMessage("player-best-time-line")
                        .number(counter.incrementAndGet() + "")
                        .parkourName(plugin.getMapHandler().getByName(entry.getKey()).getDisplayName())
                        .runTime(Utils.toReadableTime(entry.getValue(), true))
                        .removePrefix()
                        .send(sender));
        } else {
            String bestTime = plugin.getStorage().getBestTime(runner.getName(), parkourMap.getName());

            if (bestTime == null) MessageManager.instance().newMessage("player-best-time-in-map-not-found")
                .playerName(runner.getName()).send(sender);
            else MessageManager.instance().newMessage("player-best-time-in-map")
                .playerName(runner.getName())
                .parkourName(parkourMap.getDisplayName())
                .runTime(bestTime)
                .send(sender);
        }
    }

    public static Map<String,Long> setPlacesInOrder(Map<String,String> leaderboard) {
        Map<String,Long> parsedMap = new HashMap<>();
        leaderboard.forEach((owner, time) -> {
            if (time != null) parsedMap.put(owner, Utils.savedTimeToDuration(time).toMillis());
        });
        Map<String,Long> sortedBoard = new LinkedHashMap<>();
        parsedMap.entrySet().stream()
            .sorted(Map.Entry.comparingByValue())
            .forEachOrdered(entry -> sortedBoard.put(entry.getKey(), entry.getValue()));

        return sortedBoard;
    }

}