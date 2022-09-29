package me.lynx.parkourmaker.command;

import com.google.common.collect.Sets;
import me.lynx.parkourmaker.command.commands.*;
import me.lynx.parkourmaker.io.message.MessageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class MainCommand  implements CommandExecutor, ParentCommand {

    private final List<ChildCommand> commands;
    private final String name;
    private final Set<String> aliases;

    public MainCommand() {
        commands = new ArrayList<>();
        name = "parkourmaker";
        aliases = Sets.newHashSet("pm", "parkour");

        commands.add(new Reload(this));
        commands.add(new Help(this));
        commands.add(new SetLobby(this));
        commands.add(new Lobby(this));
        commands.add(new CreateParkour(this));
        commands.add(new SetStart(this));
        commands.add(new SetFinish(this));
        commands.add(new SetStartMessage(this));
        commands.add(new SetFinishMessage(this));
        commands.add(new AddCheckpoint(this));
        commands.add(new AddFallzone(this));
        commands.add(new AddReward(this));
        commands.add(new EditMap(this));
        commands.add(new Toggle(this));
        commands.add(new Join(this));
        commands.add(new SetFinishTeleport(this));
        commands.add(new AddSignText(this));
        commands.add(new me.lynx.parkourmaker.command.commands.List(this));
        commands.add(new SetCooldown(this));
        commands.add(new CheckpointCommand(this));
        commands.add(new SetDisplayName(this));
        commands.add(new SetRewardType(this));
        commands.add(new SetAttempts(this));
        commands.add(new Time(this));
        commands.add(new Info(this));
        commands.add(new Delete(this));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length > 0) {
            ChildCommandBase childCommand = getCommandByName(args[0]);
            if (childCommand != null) {
                childCommand.run(sender, args);
                return true;
            }

            AtomicBoolean aliasOwnerFound = new AtomicBoolean(false);
            commands.forEach(cmd -> {
                if (aliasOwnerFound.get()) return;
                cmd.getAliases().forEach(alias -> {
                    if (aliasOwnerFound.get()) return;
                    if (alias.equalsIgnoreCase(args[0])) aliasOwnerFound.set(true);
                });

                if (aliasOwnerFound.get()) cmd.run(sender,args);
            });
            if (aliasOwnerFound.get()) return true;
            else MessageManager.instance().newMessage("invalid-command")
                .playerName(sender.getName())
                .command(args[0])
                .send(sender);
        } else {
            getCommandByName("help").run(sender, args);
        }
        return true;
    }

    @Override
    public ChildCommandBase getCommandByName(String commandName) {
        Supplier<Stream<ChildCommand>> commandSupplier = () -> commands.stream()
            .filter(command -> command.getName().equalsIgnoreCase(commandName));

        if (commandSupplier.get().count() < 1) return null;
        return (ChildCommandBase) commandSupplier.get()
            .findFirst().orElseGet(null);
    }

    @Override
    public List<ChildCommand> getAllCommands() {
        return commands;
    }

    @Override
    public void addChildCommand(ChildCommand childCommand) {
        commands.add(childCommand);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Set<String> getAliases() {
        return aliases;
    }

}