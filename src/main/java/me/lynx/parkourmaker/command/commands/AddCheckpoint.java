package me.lynx.parkourmaker.command.commands;

import me.lynx.parkourmaker.command.ChildCommandBase;
import me.lynx.parkourmaker.command.MainCommand;
import me.lynx.parkourmaker.command.tabcomplete.Argument;
import me.lynx.parkourmaker.io.message.MessageManager;
import me.lynx.parkourmaker.model.map.Checkpoint;
import me.lynx.parkourmaker.model.map.ParkourMap;
import me.lynx.parkourmaker.model.map.Selection;
import me.lynx.parkourmaker.model.map.SelectionType;
import me.lynx.parkourmaker.util.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class AddCheckpoint extends ChildCommandBase {

    public AddCheckpoint(MainCommand parentCommand) {
        super("AddCheckpoint", parentCommand,
            "adds checkpoint to a parkour map",
            "/PM AddCheckpoint <Type> <Position> [Name]",
            "parkour-maker.command.addcheckpoint",
            "addcp" ,"acp");
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

        SelectionType type;
        int position;
        try {
            type = SelectionType.valueOf(args[1].toUpperCase());
            position = Integer.parseInt(args[2]);

            if (position < 1) {
                MessageManager.instance().newMessage("invalid-position")
                    .checkpointPosition(args[2]).send(sender);
                return;
            }
        } catch (NumberFormatException e) {
            MessageManager.instance().newMessage("invalid-position")
                .checkpointPosition(args[2]).send(sender);
            return;
        } catch (IllegalArgumentException e) {
            MessageManager.instance().newMessage("invalid-type")
                .type(args[1]).send(sender);
            return;
        }

        Checkpoint checkpoint = new Checkpoint(map);
        checkpoint.setPosition(position);

        String name = Utils.argsToMessage(args, 3);
        if (name.isEmpty()) checkpoint.setName(position + "");
        else checkpoint.setName(name);

        if (type == SelectionType.SINGLE) checkpoint.setStartPoint(((Player) sender).getLocation());
        else if (type == SelectionType.MULTI) {
            Player player = (Player) sender;
            Selection selection = Utils.getSelection(player);
            if (selection == null) {
                MessageManager.instance().newMessage("invalid-selection").send(sender);
                return;
            }

            checkpoint.setStartPoint(selection.getStartPoint());
            checkpoint.setEndPoint(selection.getEndPoint());
            checkpoint.setTeleportLocation(player.getLocation());
        }

        map.addCheckpoint(checkpoint);
        MessageManager.instance().newMessage("checkpoint-added")
            .checkpointName(checkpoint.getName())
            .parkourName(map.getDisplayName())
            .checkpointPosition(checkpoint.getPosition() + "")
            .send(sender);
    }

}