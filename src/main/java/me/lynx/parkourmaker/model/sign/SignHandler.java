package me.lynx.parkourmaker.model.sign;

import me.lynx.parkourmaker.ParkourMakerPlugin;
import me.lynx.parkourmaker.io.message.MessageManager;
import me.lynx.parkourmaker.model.map.ParkourMap;
import me.lynx.parkourmaker.model.runner.Cooldown;
import me.lynx.parkourmaker.model.runner.CooldownType;
import me.lynx.parkourmaker.model.runner.Runner;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class SignHandler implements Listener {

    private final String SIGN_CREATION = "[SetJoinSign]";

    @EventHandler
    public void onSignCreate(SignChangeEvent e) {
        if (e.getLine(1) == null || !e.getLine(1).equalsIgnoreCase(SIGN_CREATION)) return;
        ParkourMap map = ParkourMakerPlugin.instance().getMapHandler().getEditedMap(e.getPlayer().getName());
        if (map == null) return;

        if (!e.getPlayer().hasPermission("parkour-maker.sign.create")) {
            MessageManager.instance().newMessage("no-permission")
                .playerName(e.getPlayer().getName())
                .send(e.getPlayer());
            return;
        }

        SignText signText = map.getSignText();
        for (int i = 0; i < 4; i++) {
            String line = signText.getLine(i + 1);
            if (line != null) {
                line = MessageManager.instance().newInternalMessage(line)
                    .removePrefix()
                    .colorScheme(false)
                    .parkourName(map.getDisplayName())
                    .getFormattedText();
                e.setLine(i,line);
            }
        }

        MessageManager.instance().newMessage("join-sign-created")
            .parkourName(map.getDisplayName()).send(e.getPlayer());
    }

    @EventHandler
    private void onSignClick(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null) return;
        if (!(e.getClickedBlock().getState() instanceof Sign)) return;

        Sign sign = (Sign) e.getClickedBlock().getState();
        String mapName = getSignName(sign);
        if (mapName == null) return;
        if (e.getPlayer().isSneaking() && e.getAction() == Action.LEFT_CLICK_BLOCK) return;

        if (ParkourMakerPlugin.instance().getRunnerHandler().isInMap(e.getPlayer().getName())) {
            MessageManager.instance().newMessage("already-in-map").send(e.getPlayer());
            return;
        }

        if (!e.getPlayer().hasPermission("parkour-maker.sign.use") ||
            (!e.getPlayer().hasPermission("parkour-maker.join." + mapName) &&
            !e.getPlayer().hasPermission("parkour-maker.join.*"))) {

            MessageManager.instance().newMessage("no-permission")
                .playerName(e.getPlayer().getName())
                .send(e.getPlayer());
            return;
        }

        ParkourMap parkourMap = ParkourMakerPlugin.instance().getMapHandler().getByName(mapName);
        if (!parkourMap.isEnabled()) {
            MessageManager.instance().newMessage("map-disabled")
                .parkourName(parkourMap.getDisplayName()).send(e.getPlayer());
            return;
        }

        Runner runner = ParkourMakerPlugin.instance().getRunnerHandler().addRunner(e.getPlayer());
        Cooldown cooldown = runner.getCooldown(parkourMap.getName(), CooldownType.JOIN);
        if (!e.getPlayer().hasPermission("parkour-maker.ignore-cooldown.join") &&
            (cooldown != null && !cooldown.cooldownExpired())) {
            MessageManager.instance().newMessage("join-cooldown")
                .cooldown(cooldown.getTimeLeft() + "")
                .parkourName(parkourMap.getDisplayName())
                .send(e.getPlayer());
            return;
        }

        runner.joinMap(parkourMap);
        MessageManager.instance().newMessage("started-map")
            .parkourName(parkourMap.getDisplayName()).send(e.getPlayer());
    }

    @EventHandler(ignoreCancelled = false)
    private void onSignBreak(BlockBreakEvent e) {
        if (!(e.getBlock().getState() instanceof Sign)) return;
        Sign sign = (Sign) e.getBlock().getState();
        String mapName = getSignName(sign);
        if (mapName == null) return;
        e.setCancelled(true);

        if (!e.getPlayer().isSneaking()) return;
        if (!e.getPlayer().hasPermission("parkour-maker.sign.create")) {
            MessageManager.instance().newMessage("no-permission")
                .playerName(e.getPlayer().getName())
                .send(e.getPlayer());
            return;
        }
        e.setCancelled(false);
    }

    private String getSignName(Sign sign) {
        return ParkourMakerPlugin.instance().getMapHandler().getAllMapNames().stream().filter(mapName -> {
            ParkourMap map = ParkourMakerPlugin.instance().getMapHandler().getByName(mapName);
            SignText text = map.getSignText();
            if (text.isEmpty()) return false;

            for (int i = 0; i < sign.getLines().length; i++) {
                if (text.getLine(i + 1) == null) {
                    if (!sign.getLine(i).isEmpty()) return false;
                }
                else {
                    String formattedText = MessageManager.instance().newInternalMessage(text.getLine(i + 1))
                        .removePrefix()
                        .colorScheme(false)
                        .parkourName(map.getDisplayName())
                        .getFormattedText();

                    if (!formattedText.equalsIgnoreCase(sign.getLine(i))) return false;
                }
            }
            return true;
        }).findFirst().orElse(null);
    }

}