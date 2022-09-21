package me.lynx.parkourmaker.command.commands;

import me.lynx.parkourmaker.command.ChildCommandBase;
import me.lynx.parkourmaker.command.MainCommand;
import me.lynx.parkourmaker.command.tabcomplete.Argument;
import me.lynx.parkourmaker.io.message.MessageManager;
import me.lynx.parkourmaker.model.map.ParkourMap;
import me.lynx.parkourmaker.util.Utils;
import org.bukkit.command.CommandSender;

import java.util.Set;

public class SetFinishMessage extends ChildCommandBase {

    public SetFinishMessage(MainCommand parentCommand) {
        super("SetFinishMessage", parentCommand,
            "sets start message for parkour map",
            "/PM SetFinishMessage <Message>",
            "parkour-maker.command.setfinishmessage",
            "sfmsg");
    }

    @Override /* No tab completion for this command */
    public Set<Argument> onTabComplete() {
        return null;
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (!hasPermission(sender, true)) return;
        if (!hasAllArgs(sender, (short) args.length, true)) return;
        if (!isPlayer(sender, true)) return;
        if (inEditorMode(sender, true) == null) return;

        ParkourMap map = inEditorMode(sender);
        map.setFinishMessage(Utils.argsToMessage(args, 1));
        MessageManager.instance().newMessage("finish-message-set")
            .parkourName(map.getDisplayName()).send(sender);
    }

}