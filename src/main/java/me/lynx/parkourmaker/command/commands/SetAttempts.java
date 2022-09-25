package me.lynx.parkourmaker.command.commands;

import me.lynx.parkourmaker.command.ChildCommandBase;
import me.lynx.parkourmaker.command.MainCommand;
import me.lynx.parkourmaker.command.tabcomplete.Argument;
import me.lynx.parkourmaker.io.message.MessageManager;
import me.lynx.parkourmaker.model.map.ParkourMap;
import me.lynx.parkourmaker.model.runner.CooldownType;
import me.lynx.parkourmaker.util.Utils;
import org.bukkit.command.CommandSender;

import java.util.Set;

public class SetAttempts extends ChildCommandBase {

    public SetAttempts(MainCommand parentCommand) {
        super("SetAttempts", parentCommand,
            "sets amount of attempts players have",
            "/PM SetAttempts <Amount>",
            "parkour-maker.command.setattempts",
            "setatt", "setlives");
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

        int amount = 0;
        try {
            amount = Integer.parseInt(args[1]);
            if (amount < 0) {
                MessageManager.instance().newMessage("invalid-amount").amount(args[1]).send(sender);
                return;
            }
        } catch (NumberFormatException e) {
            MessageManager.instance().newMessage("invalid-amount").amount(args[1]).send(sender);
            return;
        }

        map.setAttempts(amount);
        MessageManager.instance().newMessage("attempts-set")
            .parkourName(map.getDisplayName())
            .amount(amount + "")
            .send(sender);
    }

}