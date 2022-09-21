package me.lynx.parkourmaker.command.commands;

import me.lynx.parkourmaker.ParkourMakerPlugin;
import me.lynx.parkourmaker.command.ChildCommandBase;
import me.lynx.parkourmaker.command.MainCommand;
import me.lynx.parkourmaker.command.tabcomplete.Argument;
import me.lynx.parkourmaker.io.message.MessageManager;
import me.lynx.parkourmaker.util.Utils;
import org.bukkit.command.CommandSender;

import java.util.Set;

public class CreateParkour extends ChildCommandBase {

    public CreateParkour(MainCommand parentCommand) {
        super("CreateParkour", parentCommand,
            "beings the process of creating a parkour map",
            "/PM CreateParkour <Name>",
            "parkour-maker.command.createparkour",
            "create");
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

        if (ParkourMakerPlugin.instance().getStorage().getLobbyLocation() == null) {
            MessageManager.instance().newMessage("lobby-location-not-set").send(sender);
            return;
        }

        if (inEditorMode(sender) != null) {
            MessageManager.instance().newMessage("has-to-exit-edit-mode").send(sender);
            return;
        }

        if (Utils.ignoreCaseContains(ParkourMakerPlugin.instance().getMapHandler().getAllMapNames(), args[1])) {
            MessageManager.instance().newMessage("map-name-taken").parkourName(args[1]).send(sender);
            return;
        }

        ParkourMakerPlugin.instance().getMapHandler().createMap(args[1], sender.getName());
        MessageManager.instance().newMessage("parkour-map-created").parkourName(args[1]).send(sender);
    }

}