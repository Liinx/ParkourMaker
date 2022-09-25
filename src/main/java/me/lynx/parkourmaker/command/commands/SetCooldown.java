package me.lynx.parkourmaker.command.commands;

import me.lynx.parkourmaker.command.ChildCommandBase;
import me.lynx.parkourmaker.command.MainCommand;
import me.lynx.parkourmaker.command.tabcomplete.Argument;
import me.lynx.parkourmaker.io.message.MessageManager;
import me.lynx.parkourmaker.model.map.ParkourMap;
import me.lynx.parkourmaker.model.runner.CooldownType;
import me.lynx.parkourmaker.util.Utils;
import org.bukkit.command.CommandSender;

import java.util.HashSet;
import java.util.Set;

public class SetCooldown extends ChildCommandBase {

    public SetCooldown(MainCommand parentCommand) {
        super("SetCooldown", parentCommand,
            "sets cooldown for specified type",
            "/PM SetCooldown <Type> <Amount>",
            "parkour-maker.command.setcooldown",
            "setcd", "scd");
    }

    @Override
    public Set<Argument> onTabComplete() {
        Set<Argument> toReturn = new HashSet<>();

        toReturn.add(new Argument(1, Set.of("Join", "Reward")));
        return toReturn;
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (!hasPermission(sender, true)) return;
        if (!hasAllArgs(sender, (short) args.length, true)) return;
        if (!isPlayer(sender, true)) return;
        if (inEditorMode(sender, true) == null) return;
        ParkourMap map = inEditorMode(sender);

        CooldownType type = null;
        try {
            type = CooldownType.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            MessageManager.instance().newMessage("invalid-type").type(args[1]).send(sender);
            return;
        }

        long amount = 0;
        try {
            amount = Long.parseLong(args[2]);
            if (amount < 0) {
                MessageManager.instance().newMessage("invalid-amount").amount(args[2]).send(sender);
                return;
            }
        } catch (NumberFormatException e) {
            MessageManager.instance().newMessage("invalid-amount").amount(args[2]).send(sender);
            return;
        }

        if (type == CooldownType.JOIN) map.setJoinCooldown(amount);
        else if (type == CooldownType.REWARD) map.setRewardCooldown(amount);
        MessageManager.instance().newMessage("cooldown-set")
            .parkourName(map.getDisplayName())
            .type(Utils.capitalizeFirstLetter(type.name()))
            .amount(amount + "")
            .send(sender);
    }

}