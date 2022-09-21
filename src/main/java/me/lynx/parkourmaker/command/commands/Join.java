package me.lynx.parkourmaker.command.commands;

import me.lynx.parkourmaker.ParkourMakerPlugin;
import me.lynx.parkourmaker.command.ChildCommandBase;
import me.lynx.parkourmaker.command.MainCommand;
import me.lynx.parkourmaker.command.tabcomplete.Argument;
import me.lynx.parkourmaker.io.message.MessageManager;
import me.lynx.parkourmaker.model.map.ParkourMap;
import me.lynx.parkourmaker.model.runner.Cooldown;
import me.lynx.parkourmaker.model.runner.CooldownType;
import me.lynx.parkourmaker.model.runner.Runner;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class Join extends ChildCommandBase {

    public Join(MainCommand parentCommand) {
        super("Join", parentCommand,
            "joins a parkour map",
            "/PM Join <Name>",
            "parkour-maker.command.join",
            "j", "enter", "go");
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
        if (!isPlayer(sender, true)) return;

        if (ParkourMakerPlugin.instance().getRunnerHandler().isInMap(sender.getName())) {
            MessageManager.instance().newMessage("already-in-map").send(sender);
            return;
        }

        ParkourMap parkourMap = ParkourMakerPlugin.instance().getMapHandler().getByName(args[1]);
        if (parkourMap == null) {
            MessageManager.instance().newMessage("map-not-found").parkourName(args[1]).send(sender);
            return;
        }

        if (!sender.hasPermission("parkour-maker.join." + parkourMap.getName()) &&
            !sender.hasPermission("parkour-maker.join.*")) {

            MessageManager.instance().newMessage("no-permission")
                .playerName(sender.getName())
                .send(sender);
            return;
        }

        if (!parkourMap.isEnabled()) {
            MessageManager.instance().newMessage("map-disabled")
                .parkourName(parkourMap.getDisplayName()).send(sender);
            return;
        }

        Runner runner = ParkourMakerPlugin.instance().getRunnerHandler().addRunner((Player) sender);
        Cooldown cooldown = runner.getCooldown(parkourMap.getName(), CooldownType.JOIN);
        if (!sender.hasPermission("parkour-maker.ignore-cooldown.join") &&
            (cooldown != null && !cooldown.cooldownExpired())) {
            MessageManager.instance().newMessage("join-cooldown")
                .cooldown(cooldown.getTimeLeft() + "")
                .parkourName(parkourMap.getDisplayName())
                .send(sender);
            return;
        }

        runner.joinMap(parkourMap);
        MessageManager.instance().newMessage("started-map")
            .parkourName(parkourMap.getDisplayName()).send(sender);
    }

}