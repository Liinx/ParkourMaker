package me.lynx.parkourmaker.command.commands;

import me.lynx.parkourmaker.ParkourMakerPlugin;
import me.lynx.parkourmaker.command.ChildCommandBase;
import me.lynx.parkourmaker.command.MainCommand;
import me.lynx.parkourmaker.command.tabcomplete.Argument;
import me.lynx.parkourmaker.io.message.Message;
import me.lynx.parkourmaker.io.message.MessageManager;
import me.lynx.parkourmaker.model.map.ParkourMap;
import org.bukkit.command.CommandSender;

import java.util.Set;

public class List extends ChildCommandBase {

    public List(MainCommand parentCommand) {
        super("List", parentCommand,
            "lists all parkour maps",
            "/PM List",
            "parkour-maker.command.list",
            "ls");
    }

    @Override
    public Set<Argument> onTabComplete() {
        return null;
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (!hasPermission(sender, true)) return;
        if (!hasAllArgs(sender, (short) args.length, true)) return;

        Set<String> names = ParkourMakerPlugin.instance().getMapHandler().getAllMapNames();
        MessageManager.instance().newMessage("map-list-tittle").send(sender);
        sender.sendMessage("");
        names.forEach(name -> {
            if (!sender.hasPermission("parkour-maker.join." + name)) return;
            ParkourMap map = ParkourMakerPlugin.instance().getMapHandler().getByName(name);

            Message message = MessageManager.instance().newMessage("map-list-map")
                .parkourName(map.getDisplayName() + " (" + name + ")").removePrefix();
            if (map.isEnabled()) message.send(sender);
            else {
                if (sender.hasPermission("parkour-maker.command.list.seedisabled")) message.send(sender);
            }
        });
    }

}