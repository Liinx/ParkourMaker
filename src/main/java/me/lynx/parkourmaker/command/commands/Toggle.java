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

public class Toggle extends ChildCommandBase {

    public Toggle(MainCommand parentCommand) {
        super("Toggle", parentCommand,
            "enabled or disabled a parkour map",
            "/PM Toggle <Name>",
            "parkour-maker.command.toggle");
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

        if (parkourMap.isEnabled()) {
            parkourMap.disable();
            MessageManager.instance().newMessage("set-map-disabled")
                .parkourName(parkourMap.getDisplayName()).send(sender);
        } else {
            if (parkourMap.enable()) MessageManager.instance().newMessage("set-map-enabled")
                .parkourName(parkourMap.getDisplayName()).send(sender);
            else MessageManager.instance().newMessage("map-incomplete").parkourName(parkourMap.getDisplayName()).send(sender);
        }
    }

}