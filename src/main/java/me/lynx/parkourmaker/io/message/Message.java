package me.lynx.parkourmaker.io.message;

import me.clip.placeholderapi.PlaceholderAPI;
import me.lynx.parkourmaker.ParkourMakerPlugin;
import me.lynx.parkourmaker.io.file.ProcessedConfigValue;
import me.lynx.parkourmaker.util.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class Message {

    private final ParkourMakerPlugin plugin = ParkourMakerPlugin.instance();

    private final String keyMap;
    private boolean removePrefix;
    private final Set<Placeholder> placeholders;
    private String message;
    private final boolean internal;
    private boolean isWarning;
    private boolean isCentered;
    private boolean useScheme;

    Message() {
        keyMap = null;
        placeholders = new HashSet<>();
        internal = true;
        isWarning = false;
        isCentered = false;
        useScheme = MessageManager.instance().getColorScheme().useColorScheme();

        placeholders.add(new Placeholder("%nl%", "\n",
            MessageManager.instance().getColorScheme().getDefaultText()));
    }

    Message(String keyMap) {
        this.keyMap = keyMap;
        placeholders = new HashSet<>();
        internal = false;
        isCentered = false;
        useScheme = MessageManager.instance().getColorScheme().useColorScheme();

        placeholders.add(new Placeholder("%nl%", "\n",
                MessageManager.instance().getColorScheme().getDefaultText()));
    }

    private String addPrefixAndSuffix(String value, String prefix, String suffix) {
        return prefix + value + suffix;
    }

    private String prepare() {
        if (!internal) {
            message = plugin.getFileManager().getLang().getString(keyMap + ".message");
            isWarning = plugin.getFileManager().getLang().getBoolean(keyMap + ".warning", false);
        }

        ColorScheme colorScheme = MessageManager.instance().getColorScheme();

        String defaultColor = isWarning ? colorScheme.getWarning() : colorScheme.getDefaultText();
        message = useScheme ? defaultColor + message : message;
        placeholders.forEach(placeholder -> {
            if (useScheme) {
                message = message.replaceAll(placeholder.getPlaceholder(),
                        addPrefixAndSuffix(placeholder.getValue(), placeholder.getSchemeColor(), defaultColor));
            } else message = message.replaceAll(placeholder.getPlaceholder(), placeholder.getValue());
        });
        message = removePrefix ? message : ProcessedConfigValue.of().messagePrefix() + message;
        return MessageManager.instance().translate(message);
    }

    /**
     * Logs the text without any colors just placeholders replaced.
     * Mostly indeed for console display
     */
    private void sendFormattedRawText() {
        if (!internal) {
            message = plugin.getFileManager().getLang().getString(keyMap + ".message");
            isWarning = plugin.getFileManager().getLang().getBoolean(keyMap + ".warning", false);
        }

        placeholders.forEach(placeholder ->
            message = message.replaceAll(placeholder.getPlaceholder(), placeholder.getValue()));
        message = ChatColor.stripColor(MessageManager.instance().translate(message));
        if (isCentered) message = Utils.centerMessage(message);
        if (isWarning) ParkourMakerPlugin.instance().getLogger().warning(message);
        else ParkourMakerPlugin.instance().getLogger().info(message);
    }

    /**
     * Returns the formatted text without sending it to anyone.
     * @return formatted text
     */
    public String getFormattedText() {
        return prepare();
    }

    /**
     * Can be used for player and console
     * @param receiver message receiver
     */
    public void send(CommandSender receiver) {
        String msg = prepare();

        if (receiver instanceof Player) {
            Player player = (Player) receiver;

            if (plugin.doesPAPIExist()) msg = PlaceholderAPI.setPlaceholders(player, msg);
            if (isCentered) msg = Utils.centerMessage(msg);

            receiver.sendMessage(msg);
        } else sendFormattedRawText();
    }

    public Message isWarning() {
        isWarning = true;
        return this;
    }

    public Message isCentered() {
        isCentered = true;
        return this;
    }

    public Message colorScheme(boolean use) {
        useScheme = use;
        return this;
    }

    public Message internalMessage(String message) {
        this.message = message;
        return this;
    }

    public Message appendInternal(String message) {
        this.message += message;
        return this;
    }

    public Message checkpointPosition(String position) {
        placeholders.add(new Placeholder("%checkpoint-position%", position,
            MessageManager.instance().getColorScheme().getAlternativeText()));
        return this;
    }

    public Message type(String type) {
        placeholders.add(new Placeholder("%type%", type,
            MessageManager.instance().getColorScheme().getAlternativeText()));
        return this;
    }

    public Message checkpointName(String name) {
        placeholders.add(new Placeholder("%checkpoint-name%", name,
            MessageManager.instance().getColorScheme().getAlternativeText()));
        return this;
    }

    public Message fallzoneName(String name) {
        placeholders.add(new Placeholder("%fallzone-name%", name,
            MessageManager.instance().getColorScheme().getAlternativeText()));
        return this;
    }

    public Message parkourName(String name) {
        placeholders.add(new Placeholder("%parkour-name%", name,
            MessageManager.instance().getColorScheme().getParkourMap()));
        return this;
    }

    public Message parkourDisplayName(String name) {
        placeholders.add(new Placeholder("%map-display-name%", name,
                MessageManager.instance().getColorScheme().getParkourMap()));
        return this;
    }

    public Message line(String line) {
        placeholders.add(new Placeholder("%line%", line,
            MessageManager.instance().getColorScheme().getDefaultText()));
        return this;
    }

    public Message lineText(String line) {
        placeholders.add(new Placeholder("%line-text%", line,
            MessageManager.instance().getColorScheme().getAlternativeText()));
        return this;
    }

    public Message number(String name) {
        placeholders.add(new Placeholder("%number%", name,
            MessageManager.instance().getColorScheme().getAlternativeText()));
        return this;
    }

    public Message commandUsage(String usage) {
        placeholders.add(new Placeholder("%command-usage%", usage,
            MessageManager.instance().getColorScheme().getAlternativeText()));
        return this;
    }

    public Message commandHelpMsg(String message) {
        placeholders.add(new Placeholder("%command-message%", message,
            MessageManager.instance().getColorScheme().getAlternativeText()));
        return this;
    }

    public Message commandAlias(String alias) {
        placeholders.add(new Placeholder("%command-alias%", alias,
            MessageManager.instance().getColorScheme().getAlternativeText()));
        return this;
    }

    public Message command(String command) {
        placeholders.add(new Placeholder("%command%", command,
            MessageManager.instance().getColorScheme().getAlternativeText()));
        return this;
    }

    public Message playerName(String playerName) {
        placeholders.add(new Placeholder("%player-name%", playerName,
            MessageManager.instance().getColorScheme().getPlayer()));
        return this;
    }

    public Message cooldown(String joinCooldown) {
        placeholders.add(new Placeholder("%cooldown%", joinCooldown,
            MessageManager.instance().getColorScheme().getAlternativeText()));
        return this;
    }

    public Message amount(String amount) {
        placeholders.add(new Placeholder("%amount%", amount,
            MessageManager.instance().getColorScheme().getAlternativeText()));
        return this;
    }

    public Message runTime(String runTime) {
        placeholders.add(new Placeholder("%run-time%", runTime,
                MessageManager.instance().getColorScheme().getAlternativeText()));
        return this;
    }

    public Message ms(long ms) {
        placeholders.add(new Placeholder("%ms%", ms + "",
            MessageManager.instance().getColorScheme().getAlternativeText()));
        return this;
    }

    public Message removePrefix() {
        removePrefix = true;
        return this;
    }

}