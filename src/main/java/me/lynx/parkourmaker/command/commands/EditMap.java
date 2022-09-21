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

public class EditMap extends ChildCommandBase {

    public EditMap(MainCommand parentCommand) {
        super("EditMap", parentCommand,
            "enter/exit edit more for certain map",
            "/PM EditMap [Name]",
            "parkour-maker.command.editmap",
            "em", "edit");
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

        ParkourMap map = inEditorMode(sender);
        if (map == null) {
            if (args.length < 2) {
                MessageManager.instance().newMessage("command-usage")
                    .playerName(sender.getName())
                    .commandUsage(usage)
                    .send(sender);
                return;
            }

            ParkourMap parkourMap = ParkourMakerPlugin.instance().getMapHandler().getByName(args[1]);
            if (parkourMap == null) {
                MessageManager.instance().newMessage("map-not-found").parkourName(args[1]).send(sender);
                return;
            }

            parkourMap.addEditor(sender.getName());
            MessageManager.instance().newMessage("edit-mode-activated")
                .parkourName(parkourMap.getDisplayName()).send(sender);
            return;
        } else {
            if (args.length > 1 && !map.getName().equalsIgnoreCase(args[1])) {
                MessageManager.instance().newMessage("already-editing-another-map")
                        .parkourName(map.getDisplayName()).send(sender);
                return;
            }

            map.removeEditor(sender.getName());
            map.checkIfOperational();
            MessageManager.instance().newMessage("edit-mode-deactivated")
                .parkourName(map.getDisplayName()).send(sender);
        }
    }

}
