package me.wega.detectors.listener;

import me.wega.detectors.config.ConfigValue;
import me.wega.detectors.manager.DetectorManager;
import me.wega.detectors.manager.WhitelistManager;
import me.wega.detectors.task.DetectorScanItemTask;
import me.wega.detectors.utils.DetectorUtils;
import me.wega.detectors.utils.ItemCheckUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.*;

import static me.wega.detectors.WegaMetalDetectors.instance;

public final class DetectorAlertListener implements Listener {
    private static final HashSet<UUID> COOLDOWNS = new HashSet<>();
    private final WhitelistManager whitelistManager = instance.getWhitelistManager();
    private final DetectorManager detectorManager = instance.getDetectorManager();

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();
        if (from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ())
            return;

        Player player = event.getPlayer();
        if (COOLDOWNS.contains(player.getUniqueId()))
            return;

        for (double sub : ConfigValue.Settings.SUBTRACTIONS) {
            if (!detectorManager.isWorkingDetector(to.clone().subtract(0.0, sub, 0.0).getBlock()))
                return;
        }

        List<Player> playersToCheck = new ArrayList<>();
        playersToCheck.add(player);
        player.getPassengers().stream()
                .filter(entity -> entity instanceof Player)
                .forEach(entity -> playersToCheck.add((Player) entity));

        for (Player p : playersToCheck) {
            DetectorUtils.activate(p, to.getBlock());
            COOLDOWNS.add(p.getUniqueId());
            Bukkit.getScheduler().runTaskLaterAsynchronously(instance, () ->
                    COOLDOWNS.remove(p.getUniqueId()), 100L);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onItemDrop(PlayerDropItemEvent event) {
        Item item = event.getItemDrop();
        Player player = event.getPlayer();
        if (!ItemCheckUtils.anyMatch(whitelistManager.getSet(), Collections.singleton(item.getItemStack()), ItemCheckUtils.CheckLevel.REGULAR))
            return;
        new DetectorScanItemTask(player, item).run();
    }
}
