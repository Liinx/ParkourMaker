package me.lynx.parkourmaker.command.commands;

import me.lynx.parkourmaker.command.ChildCommandBase;
import me.lynx.parkourmaker.command.MainCommand;
import me.lynx.parkourmaker.command.tabcomplete.Argument;
import me.lynx.parkourmaker.io.message.MessageManager;
import me.lynx.parkourmaker.model.map.ParkourMap;
import me.lynx.parkourmaker.model.sign.SignText;
import me.lynx.parkourmaker.util.Utils;
import org.bukkit.command.CommandSender;

import java.util.HashSet;
import java.util.Set;

public class AddSignText extends ChildCommandBase {

    public AddSignText(MainCommand parentCommand) {
        super("AddSignText", parentCommand,
            "adds text to join signs of a parkour map",
            "/PM AddSignText <Line> [Text]",
            "parkour-maker.command.addsigntext",
            "ast" ,"addsigntxt");
    }

    @Override
    public Set<Argument> onTabComplete() {
        Set<Argument> toReturn = new HashSet<>();

        toReturn.add(new Argument(1, Set.of("1", "2", "3", "4")));
        return toReturn;
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (!hasPermission(sender, true)) return;
        if (!hasAllArgs(sender, (short) args.length, true)) return;
        if (!isPlayer(sender, true)) return;
        if (inEditorMode(sender, true) == null) return;
        ParkourMap map = inEditorMode(sender);

        int line = 0;
        try {
            line = Integer.parseInt(args[1]);

            if (line < 1 || line > 4) {
                MessageManager.instance().newMessage("invalid-line")
                    .line(args[1]).send(sender);
                return;
            }
        } catch (NumberFormatException e) {
            MessageManager.instance().newMessage("invalid-number")
                .number(args[1]).send(sender);
            return;
        }
        String text = Utils.argsToMessage(args, 2);

        SignText signText = map.getSignText();
        signText.setLine(line, text, true);

        MessageManager.instance().newMessage("sign-text-added")
            .line(line + "")
            .lineText(text)
            .parkourName(map.getDisplayName())
            .send(sender);
    }

}