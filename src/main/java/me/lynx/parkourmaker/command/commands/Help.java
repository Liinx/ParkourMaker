package me.lynx.parkourmaker.command.commands;

import com.google.common.collect.Sets;
import me.lynx.parkourmaker.command.ChildCommandBase;
import me.lynx.parkourmaker.command.MainCommand;
import me.lynx.parkourmaker.command.tabcomplete.Argument;
import me.lynx.parkourmaker.io.message.MessageManager;
import org.bukkit.command.CommandSender;

import java.util.Set;
import java.util.stream.Collectors;

public class Help extends ChildCommandBase {

    public Help(MainCommand parentCommand) {
        super("Help", parentCommand,
            "displays a help page",
            "/PM Help [Command]",
            "parkour-maker.command.help");
    }

    @Override
    public Set<Argument> onTabComplete() {
        Set<String> commandNames = parentCommand.getAllCommands().stream()
            .map(cmd -> cmd.getName().toLowerCase()).collect(Collectors.toSet());

        return Sets.newHashSet(new Argument(1, commandNames));
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (!hasPermission(sender, true)) return;

        if (args.length > 1) {
            ChildCommandBase cmd = parentCommand.getCommandByName(args[1]);
            if (cmd == null) {
                MessageManager.instance().newMessage("invalid-command")
                    .playerName(sender.getName())
                    .command(args[1])
                    .send(sender);
                return;
            }

            if (!sender.hasPermission(cmd.getPermission())) {
                MessageManager.instance().newMessage("no-permission")
                    .playerName(sender.getName())
                    .command(getName())
                    .send(sender);
                return;
            }

            MessageManager.instance().newInternalMessage("Help for '%command%' command:%nl%%nl%" +
                "Command Name: %command%%nl%" +
                "Usage: %command-usage%%nl%" +
                "Aliases: %command-alias%%nl%" +
                "Description: %command-message%")
            .command(cmd.getName())
            .commandAlias(cmd.getAliases().toString().replaceAll("\\[|\\]", ""))
            .commandUsage(cmd.getUsage())
            .commandHelpMsg(cmd.getHelpMessage())
            .send(sender);

        } else sendHelpMessage(sender);
    }

    public void sendHelpMessage(CommandSender sender) {
        sender.sendMessage("");
        MessageManager.instance().newInternalMessage("#42473f------- [#75ff1fParkour Maker Help Page#42473f] -------")
            .isCentered()
            .removePrefix()
            .colorScheme(false)
            .send(sender);
        sender.sendMessage("");

        getParentCommand().getAllCommands().stream()
            .filter(cmd -> sender.hasPermission(cmd.getPermission()))
            .forEach(cmd ->
                MessageManager.instance().newInternalMessage("#ffd414%command-usage%#91908c - %command-message%")
                    .commandUsage(cmd.getUsage())
                    .commandHelpMsg(cmd.getHelpMessage())
                    .removePrefix()
                    .colorScheme(false)
                    .send(sender)
            );
    }

}
