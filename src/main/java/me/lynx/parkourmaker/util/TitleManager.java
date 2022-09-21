package me.lynx.parkourmaker.util;

import me.lynx.parkourmaker.ParkourMakerPlugin;
import me.lynx.parkourmaker.io.message.Message;
import org.bukkit.entity.Player;

public class TitleManager {

    private TitleManager() {}

    @SuppressWarnings("deprecation")
    public static void displayTitle(Player player, Message message) {
        String title = message.getFormattedText();

        if (ParkourMakerPlugin.instance().getServerVersion() > 8) {
            player.sendTitle(title, null, 20, 4 * 20, 2 * 20);
        } else {
            player.sendTitle(title, null);
        }
    }

}