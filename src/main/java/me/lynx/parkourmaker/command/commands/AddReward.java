package me.lynx.parkourmaker.command.commands;

import me.lynx.parkourmaker.command.ChildCommandBase;
import me.lynx.parkourmaker.command.MainCommand;
import me.lynx.parkourmaker.command.tabcomplete.Argument;
import me.lynx.parkourmaker.io.message.MessageManager;
import me.lynx.parkourmaker.model.map.*;
import me.lynx.parkourmaker.util.Utils;
import org.bukkit.command.CommandSender;

import java.util.Set;

public class AddReward extends ChildCommandBase {

    public AddReward(MainCommand parentCommand) {
        super("AddReward", parentCommand,
            "adds reward to a parkour map",
            "/PM AddReward <Command>",
            "parkour-maker.command.addreward",
            "ar");
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

        String command = Utils.argsToMessage(args, 1);
        map.addReward(command);
        MessageManager.instance().newMessage("reward-added")
            .command(command)
            .parkourName(map.getDisplayName())
            .send(sender);
    }

}
