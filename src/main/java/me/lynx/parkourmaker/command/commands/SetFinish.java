package me.lynx.parkourmaker.command.commands;

import me.lynx.parkourmaker.command.ChildCommandBase;
import me.lynx.parkourmaker.command.MainCommand;
import me.lynx.parkourmaker.command.tabcomplete.Argument;
import me.lynx.parkourmaker.io.message.MessageManager;
import me.lynx.parkourmaker.model.map.ParkourMap;

import me.lynx.parkourmaker.model.map.Selection;
import me.lynx.parkourmaker.model.map.SelectionType;
import me.lynx.parkourmaker.util.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class SetFinish extends ChildCommandBase {

    public SetFinish(MainCommand parentCommand) {
        super("SetFinish", parentCommand,
            "sets finish position of parkour map",
            "/PM SetFinish <Type>",
            "parkour-maker.command.setfinish",
            "sf");
    }

    @Override
    public Set<Argument> onTabComplete() {
        Set<Argument> toReturn = new HashSet<>();

        toReturn.add(new Argument(1, Set.of("Single", "Multi")));
        return toReturn;
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (!hasPermission(sender, true)) return;
        if (!hasAllArgs(sender, (short) args.length, true)) return;
        if (!isPlayer(sender, true)) return;
        if (inEditorMode(sender, true) == null) return;
        ParkourMap map = inEditorMode(sender);

        SelectionType type = null;
        try {
            type = SelectionType.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            MessageManager.instance().newMessage("invalid-type").type(args[1]).send(sender);
            return;
        }

        if (type == SelectionType.MULTI) {
            Player player = (Player) sender;
            Selection selection = Utils.getSelection(player);
            if (selection == null) {
                MessageManager.instance().newMessage("invalid-selection").send(sender);
                return;
            }

            map.setFinishLocation(selection);
            MessageManager.instance().newMessage("finish-set").parkourName(map.getDisplayName()).send(sender);
        } else if (type == SelectionType.SINGLE) {
            map.setFinishLocation(new Selection(((Player) sender).getLocation()));
            MessageManager.instance().newMessage("finish-set").parkourName(map.getDisplayName()).send(sender);
        }
    }

}