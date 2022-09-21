package me.lynx.parkourmaker.command;

import com.google.common.collect.Sets;
import me.lynx.parkourmaker.ParkourMakerPlugin;
import me.lynx.parkourmaker.io.message.MessageManager;
import me.lynx.parkourmaker.model.map.ParkourMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;

public abstract class ChildCommandBase implements ChildCommand {

    protected final String name;
    protected final Set<String> aliases;
    protected final MainCommand parentCommand;
    protected final String usage;
    protected final String helpMessage;
    protected final String permission;
    protected final int minArgsAmount;

    public ChildCommandBase(String name,
                            MainCommand parentCommand,
                            String helpMessage,
                            String usage,
                            String permission,
                            String... aliases) {

        this.name = name;
        this.aliases = Sets.newHashSet(aliases);
        this.parentCommand = parentCommand;
        this.helpMessage = helpMessage;
        this.usage = usage;
        this.permission = permission;
        minArgsAmount = (int) (1 + usage.chars().filter(c -> c == '<').count());
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Set<String> getAliases() {
        return aliases;
    }

    @Override
    public String getUsage() {
        return usage;
    }

    @Override
    public String getPermission() {
        return permission;
    }

    @Override
    public String getHelpMessage() {
        return helpMessage;
    }

    @Override
    public MainCommand getParentCommand() {
        return parentCommand;
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission(permission);
    }

    public boolean hasPermission(CommandSender sender, boolean displayMessage) {
        boolean result = hasPermission(sender);
        if (displayMessage && !result) {
            MessageManager.instance().newMessage("no-permission")
                .playerName(sender.getName())
                .command(getName())
                .send(sender);
        }

        return result;
    }

    @Override
    public boolean hasAllArgs(CommandSender sender, short argsLength) {
        return argsLength >= minArgsAmount;
    }

    public boolean hasAllArgs(CommandSender sender, short argsLength, boolean displayMessage) {
        boolean result = hasAllArgs(sender, argsLength);
        if (displayMessage && !result) {
            MessageManager.instance().newMessage("command-usage")
                .playerName(sender.getName())
                .commandUsage(usage)
                .send(sender);
        }
        return result;
    }

    @Override /* Not case-sensitive */
    public ParkourMap inEditorMode(CommandSender sender) {
        return ParkourMakerPlugin.instance().getMapHandler().getEditedMap(sender.getName());
    }

    public ParkourMap inEditorMode(CommandSender sender, boolean displayMessage) {
        ParkourMap map = inEditorMode(sender);
        if (displayMessage && map == null) {
            MessageManager.instance().newMessage("not-editing-map")
                .playerName(sender.getName())
                .send(sender);
        }
        return map;
    }

    @Override
    public boolean isPlayer(CommandSender sender) {
        return sender instanceof Player;
    }

    public boolean isPlayer(CommandSender sender, boolean displayMessage) {
        boolean result = isPlayer(sender);
        if (displayMessage && !result) {
            MessageManager.instance().newMessage("player-only-command")
                .playerName(sender.getName())
                .send(sender);
        }
        return result;
    }

}