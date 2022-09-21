package me.lynx.parkourmaker.command.commands;

import me.lynx.parkourmaker.command.ChildCommandBase;
import me.lynx.parkourmaker.command.MainCommand;
import me.lynx.parkourmaker.command.tabcomplete.Argument;
import me.lynx.parkourmaker.io.message.MessageManager;
import me.lynx.parkourmaker.model.map.*;
import me.lynx.parkourmaker.util.Utils;
import org.bukkit.command.CommandSender;

import java.util.HashSet;
import java.util.Set;

public class SetRewardType extends ChildCommandBase {

    public SetRewardType(MainCommand parentCommand) {
        super("SetRewardType", parentCommand,
            "sets which way rewards are given",
            "/PM SetRewardType <Type>",
            "parkour-maker.command.setrewardtype",
            "setrt", "srt");
    }

    @Override
    public Set<Argument> onTabComplete() {
        Set<Argument> toReturn = new HashSet<>();

        toReturn.add(new Argument(1, Set.of("All", "Random")));
        return toReturn;
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (!hasPermission(sender, true)) return;
        if (!hasAllArgs(sender, (short) args.length, true)) return;
        if (!isPlayer(sender, true)) return;
        if (inEditorMode(sender, true) == null) return;
        ParkourMap map = inEditorMode(sender);

        RewardType type = null;
        try {
            type = RewardType.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            MessageManager.instance().newMessage("invalid-type").type(args[1]).send(sender);
            return;
        }

        map.setRewardType(type);
        MessageManager.instance().newMessage("reward-type-set")
            .parkourName(map.getDisplayName())
            .type(Utils.capitalizeFirstLetter(type.name()))
            .send(sender);
    }

}