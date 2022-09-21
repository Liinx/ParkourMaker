package me.lynx.parkourmaker.io.message;

import me.lynx.parkourmaker.ParkourMakerPlugin;
import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageManager {

    private static final MessageManager instance = new MessageManager();
    private ColorScheme colorScheme;
    private final Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");

    private MessageManager() {}

    public static MessageManager instance() {
        return instance;
    }

    public void prepare() {
        colorScheme = new ColorScheme();
    }

    public ColorScheme getColorScheme() {
        return colorScheme;
    }

    public Message newMessage(String keyMap) {
        return new Message(keyMap);
    }

    public Message newInternalMessage(String message) {
        Message msg = new Message();
        msg.internalMessage(message);
        return msg;
    }

    /**
     * Translates color codes and hex codes from the message
     * into formatted text that will be displayed with colors.
     * Hex codes only work from 1.16+.
     * @param message message to be transformed
     * @return formatted message
     */
    public String translate(String message) {
        if(ParkourMakerPlugin.instance().getServerVersion() >= 16) {
            Matcher matcher = pattern.matcher(message);

            while (matcher.find()) {
                String color = message.substring(matcher.start(), matcher.end());
                message = message.replace(color, ChatColor.of(color) + "");
                matcher = pattern.matcher(message);
            }
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

}