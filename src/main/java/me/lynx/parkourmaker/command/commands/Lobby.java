package me.lynx.parkourmaker.command.commands;

import me.lynx.parkourmaker.ParkourMakerPlugin;
import me.lynx.parkourmaker.command.ChildCommandBase;
import me.lynx.parkourmaker.command.MainCommand;
import me.lynx.parkourmaker.command.tabcomplete.Argument;
import me.lynx.parkourmaker.io.message.MessageManager;
import me.lynx.parkourmaker.model.runner.RunnerHandler;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;

public class Lobby extends ChildCommandBase {

    public Lobby(MainCommand parentCommand) {
        super("Lobby", parentCommand,
            "teleports you to parkour lobby",
            "/PM Lobby",
            "parkour-maker.command.lobby",
            "lb");
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

        Location lobbyLoc = ParkourMakerPlugin.instance().getStorage().getLobbyLocation();
        if (lobbyLoc == null) {
            MessageManager.instance().newMessage("lobby-location-not-set").send(sender);
            return;
        }
        ((Player) sender).teleport(lobbyLoc);

        RunnerHandler runnerHandler = ParkourMakerPlugin.instance().getRunnerHandler();
        if (runnerHandler.isInMap(sender.getName())) {
            runnerHandler.getRunnerFromPlayer(sender.getName()).quitMap();
        }

        MessageManager.instance().newMessage("teleported-to-lobby").send(sender);
    }

}