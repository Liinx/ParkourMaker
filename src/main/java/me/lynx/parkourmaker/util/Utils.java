package me.lynx.parkourmaker.util;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import me.lynx.parkourmaker.model.map.Selection;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Utils {

    private static final int CENTER_PX = 158;
    private static final char SYMBOL_CHAR = '\u00a7';

    /**
     * Provides a counter to be used with a playing consumer in for each stream
     * for example.
     */
    public static <T> Consumer<T> withCounter(BiConsumer<Integer, T> consumer) {
        AtomicInteger counter = new AtomicInteger(0);
        return item -> consumer.accept(counter.getAndIncrement(), item);
    }

    /**
     * Combines all the arguments typed in a command to a single message.
     * @param args array containing all the arguments
     * @param startIndex starting position of the message
     * @return message as a string.
     */
    public static String argsToMessage(String[] args, int startIndex) {
        StringBuffer sb = new StringBuffer();
        for (int i = startIndex; i < args.length; i++) {
            sb.append(args[i]);
            if (i != (args.length - 1)) sb.append(" ");
        }
        return sb.toString();
    }

    /**
     * Gets multi type selection from a certain player that was made using
     * a world edit plugin.
     * If player didn't make a selection, or it is unfinished it will
     * return null;
     * @return selection
     */
    public static Selection getSelection(Player player) {
        BukkitPlayer pl = BukkitAdapter.adapt(player);
        try {
            Region region = WorldEdit.getInstance().getSessionManager().get(pl).getSelection(pl.getWorld());

            BlockVector3 minPoint = region.getMinimumPoint();
            BlockVector3 maxPoint = region.getMaximumPoint();

            Location minLocation = new Location(player.getWorld(), minPoint.getBlockX(), minPoint.getBlockY(), minPoint.getBlockZ());
            Location maxLocation = new Location(player.getWorld(), maxPoint.getBlockX(), maxPoint.getBlockY(), maxPoint.getBlockZ());

            return new Selection(minLocation, maxLocation);
        } catch (IncompleteRegionException e) {
            return null;
        }
    }

    /**
     * Checks weather a collection contains a string while ignoring case sensitivity
     */
    public static boolean ignoreCaseContains(Collection<String> collection, String string) {
        return collection.stream().anyMatch(e -> e.equalsIgnoreCase(string));
    }

    /**
     * Makes the first letter be uppercase while the rest are lowercase.
     */
    public static String capitalizeFirstLetter(String word) {
        return word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
    }

    /**
     * Centres a message based on pixels, works for the default font not tested on others!
     * Does not work if you're using %nl% placeholder for making new lines!
     */
    public static String centerMessage(final String message) {
        if(message == null || message.equals("")) return "";

        int messagePxSize = 0;
        boolean isSectionSymbol = false;
        boolean isBold = false;

        for (char c : message.toCharArray()) {
            if (c == SYMBOL_CHAR) isSectionSymbol = true;
            else if(isSectionSymbol) {
                isSectionSymbol = false;
                isBold = c == 'l' || c == 'L';
            } else {
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
        }

        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = CENTER_PX - halvedMessageSize;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder sb = new StringBuilder();
        while (compensated < toCompensate) {
            sb.append(" ");
            compensated += spaceLength;
        }

        return sb.toString() + message;
    }

}