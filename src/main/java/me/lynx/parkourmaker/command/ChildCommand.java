package me.lynx.parkourmaker.command;

import me.lynx.parkourmaker.command.tabcomplete.Argument;
import me.lynx.parkourmaker.model.map.ParkourMap;
import org.bukkit.command.CommandSender;

import java.util.Set;

public interface ChildCommand extends Command{

    String getUsage();

    String getPermission();

    String getHelpMessage();

    ParentCommand getParentCommand();

    Set<Argument> onTabComplete();

    void run(CommandSender sender, String[] args);

    boolean hasPermission(CommandSender sender);

    boolean hasAllArgs(CommandSender sender, short argsLength);

    ParkourMap inEditorMode(CommandSender sender);

    boolean isPlayer(CommandSender sender);

}
