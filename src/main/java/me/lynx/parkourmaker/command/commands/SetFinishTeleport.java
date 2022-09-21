package me.lynx.parkourmaker.command.commands;

import me.lynx.parkourmaker.command.ChildCommandBase;
import me.lynx.parkourmaker.command.MainCommand;
import me.lynx.parkourmaker.command.tabcomplete.Argument;
import me.lynx.parkourmaker.io.message.MessageManager;
import me.lynx.parkourmaker.model.map.ParkourMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;

public class SetFinishTeleport extends ChildCommandBase {

    public SetFinishTeleport(MainCommand parentCommand) {
        super("SetFinishTeleport", parentCommand,
            "sets teleport place after completing parkour map",
            "/PM SetFinishTeleport",
            "parkour-maker.command.setfinishteleport",
            "sft" ,"setfinishport");
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
        map.setFinishTeleportLocation(((Player) sender).getLocation());
        MessageManager.instance().newMessage("finish-teleport-set")
            .parkourName(map.getDisplayName()).send(sender);
    }

}