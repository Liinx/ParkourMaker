package me.lynx.parkourmaker.command.commands;

import me.lynx.parkourmaker.command.ChildCommandBase;
import me.lynx.parkourmaker.command.MainCommand;
import me.lynx.parkourmaker.command.tabcomplete.Argument;
import me.lynx.parkourmaker.io.message.MessageManager;
import me.lynx.parkourmaker.model.map.ParkourMap;
import me.lynx.parkourmaker.util.Utils;
import org.bukkit.command.CommandSender;

import java.util.Set;

public class SetDisplayName extends ChildCommandBase {

    public SetDisplayName(MainCommand parentCommand) {
        super("SetDisplayName", parentCommand,
            "enter/exit edit more for certain map",
            "/PM SetDisplayName <Name>",
            "parkour-maker.command.setdisplayname",
            "setdn", "sdn");
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
        map.setDisplayName(name);
        MessageManager.instance().newMessage("display-name-set")
            .parkourDisplayName(name)
            .parkourName(map.getName())
            .send(sender);
    }

}