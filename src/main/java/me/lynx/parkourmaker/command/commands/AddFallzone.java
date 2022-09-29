package me.lynx.parkourmaker.command.commands;

import me.lynx.parkourmaker.command.ChildCommandBase;
import me.lynx.parkourmaker.command.MainCommand;
import me.lynx.parkourmaker.command.tabcomplete.Argument;
import me.lynx.parkourmaker.io.message.MessageManager;
import me.lynx.parkourmaker.model.map.Fallzone;
import me.lynx.parkourmaker.model.map.ParkourMap;
import me.lynx.parkourmaker.model.map.Selection;
import me.lynx.parkourmaker.util.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;

public class AddFallzone extends ChildCommandBase {

    public AddFallzone(MainCommand parentCommand) {
        super("AddFallzone", parentCommand,
            "adds fallzone to a parkour map",
            "/PM AddFallzone <Name>",
            "parkour-maker.command.addfallzone",
            "addfz", "afz");
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

        String name = Utils.argsToMessage(args, 1);
        Player player = (Player) sender;
        Selection selection = Utils.getSelection(player);
        if (selection == null) {
            MessageManager.instance().newMessage("invalid-selection").send(sender);
            return;
        }

        map.addFallzone(new Fallzone(name, selection.getStartPoint(), selection.getEndPoint()));
        MessageManager.instance().newMessage("fallzone-added")
            .fallzoneName(name)
            .parkourName(map.getDisplayName())
            .send(sender);
    }

}