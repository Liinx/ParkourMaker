package me.lynx.parkourmaker.command.commands;

import me.lynx.parkourmaker.ParkourMakerPlugin;
import me.lynx.parkourmaker.command.ChildCommandBase;
import me.lynx.parkourmaker.command.MainCommand;
import me.lynx.parkourmaker.command.tabcomplete.Argument;
import me.lynx.parkourmaker.io.file.load.ConfigValues;
import me.lynx.parkourmaker.io.message.MessageManager;
import org.bukkit.command.CommandSender;

import java.util.Set;

public class Reload extends ChildCommandBase {

    public Reload(MainCommand parentCommand) {
        super("Reload", parentCommand,
            "reloads the plugin",
            "/PM Reload",
            "parkour-maker.command.reload",
            "rl");
    }

    @Override /* No tab completion for this command */
    public Set<Argument> onTabComplete() {
        return null;
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        final long start = System.currentTimeMillis();
        if (!hasPermission(sender, true)) return;
        if (!hasAllArgs(sender, (short) args.length, true)) return;

        ParkourMakerPlugin.instance().getFileManager().startupAsync(() -> {
            ConfigValues.setupValues();
            MessageManager.instance().prepare();
            ParkourMakerPlugin.instance().getStorage().onReload();
        });

        MessageManager.instance().newMessage("plugin-reloaded")
            .ms(System.currentTimeMillis() - start)
            .send(sender);
    }

}