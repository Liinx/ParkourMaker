package me.lynx.parkourmaker.command.commands;

import me.lynx.parkourmaker.ParkourMakerPlugin;
import me.lynx.parkourmaker.command.ChildCommandBase;
import me.lynx.parkourmaker.command.MainCommand;
import me.lynx.parkourmaker.command.tabcomplete.Argument;
import me.lynx.parkourmaker.io.message.MessageManager;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;

public class SetLobby extends ChildCommandBase {

    public SetLobby(MainCommand parentCommand) {
        super("SetLobby", parentCommand,
            "sets lobby location for parkour maps",
            "/PM SetLobby",
            "parkour-maker.command.setlobby",
            "sl");
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

        Location lobbyLoc = ((Player) sender).getLocation();
        ParkourMakerPlugin.instance().getStorage().setLobbyLocation(
            lobbyLoc.getX(),
            lobbyLoc.getY(),
            lobbyLoc.getZ(),
            lobbyLoc.getYaw(),
            lobbyLoc.getPitch(),
            lobbyLoc.getWorld().getName());

        MessageManager.instance().newMessage("lobby-location-set").send(sender);
    }

}
