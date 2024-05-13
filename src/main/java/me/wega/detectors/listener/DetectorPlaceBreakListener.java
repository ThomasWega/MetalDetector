package me.wega.detectors.listener;

import me.wega.detectors.manager.DetectorManager;
import me.wega.detectors.WegaDetectors;
import me.wega.detectors.utils.DetectorKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public final class DetectorPlaceBreakListener implements Listener {
    private final DetectorManager detectorManager = WegaDetectors.instance.getDetectorManager();

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        ItemStack itemPlaced = event.getItemInHand();
        if (!DetectorKey.ITEM.has(itemPlaced.getItemMeta())) return;
        detectorManager.add(event.getBlock(), true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        detectorManager.remove(event.getBlock());
    }
}
