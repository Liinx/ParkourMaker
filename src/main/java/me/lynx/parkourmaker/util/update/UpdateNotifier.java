package me.lynx.parkourmaker.util.update;

import me.lynx.parkourmaker.io.file.ProcessedConfigValue;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class UpdateNotifier implements Listener {

    public UpdateNotifier() {}

    @EventHandler
    public void onOwnerJoin(PlayerJoinEvent e) {
        if (!e.getPlayer().isOp()) return;
        if (ProcessedConfigValue.of().notifyOperatorNewVersion()) {
            UpdateChecker.getInstance().check(false, e.getPlayer());
        }
    }

}