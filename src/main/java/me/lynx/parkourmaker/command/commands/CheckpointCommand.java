package me.lynx.parkourmaker.command.commands;

import me.lynx.parkourmaker.ParkourMakerPlugin;
import me.lynx.parkourmaker.command.ChildCommandBase;
import me.lynx.parkourmaker.command.MainCommand;
import me.lynx.parkourmaker.command.tabcomplete.Argument;
import me.lynx.parkourmaker.io.message.MessageManager;
import me.lynx.parkourmaker.model.map.Checkpoint;
import me.lynx.parkourmaker.model.runner.Runner;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;

public class CheckpointCommand extends ChildCommandBase {

    public CheckpointCommand(MainCommand parentCommand) {
        super("Checkpoint", parentCommand,
            "teleports you to your last checkpoint",
            "/PM Checkpoint",
            "parkour-maker.command.checkpoint",
            "cp");
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

        if (!ParkourMakerPlugin.instance().getRunnerHandler().isInMap(sender.getName())) {
            MessageManager.instance().newMessage("not-in-parkour").send(sender);
            return;
        }

        Runner runner = ParkourMakerPlugin.instance().getRunnerHandler().getRunnerFromPlayer(sender.getName());
        if (runner.getCurrentCheckpoint() == 0) {
            ((Player) sender).teleport(runner.getMap().getStartLocation());
        } else {
            Checkpoint checkpoint = runner.getMap().getCheckpoint(runner.getCurrentCheckpoint());
            ((Player) sender).teleport(checkpoint.getTeleportLocation());
        }
        MessageManager.instance().newMessage("teleported-to-checkpoint").send(sender);
    }

}
