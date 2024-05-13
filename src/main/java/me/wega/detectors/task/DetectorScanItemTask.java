package me.wega.detectors.task;

import me.wega.detectors.config.ConfigValue;
import me.wega.detectors.manager.DetectorManager;
import me.wega.detectors.utils.DetectorUtils;
import me.wega.toolkit.task.PlayerTask;
import me.wega.toolkit.utils.SchedulerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static me.wega.detectors.WegaMetalDetectors.instance;


public class DetectorScanItemTask extends PlayerTask {
    private static final HashSet<UUID> COOLDOWNS = new HashSet<>();
    private static final DetectorManager DETECTOR_MANAGER = instance.getDetectorManager();
    private final @NotNull Item droppedItem;

    public DetectorScanItemTask(@NotNull Player player, @NotNull Item droppedItem) {
        super(player);
        this.droppedItem = droppedItem;
    }

    @Override
    protected @Nullable BukkitTask runInternal() {
        AtomicInteger i = new AtomicInteger();
        final Player player = getPlayer();
        final UUID uuid = player.getUniqueId();
        return SchedulerUtils.runTaskTimer(() -> {
            i.getAndIncrement();
            // prevent running this forever
            if (i.get() == 60) {
                this.stop();
                return;
            }

            Entity ent = Bukkit.getEntity(droppedItem.getUniqueId());
            if (ent == null || !ent.isValid() || !(ent instanceof Item)) {
                this.stop();
                return;
            }
            Location itemLoc = ent.getLocation();

            for (double sub : ConfigValue.Settings.SUBTRACTIONS) {
                Block block = itemLoc.clone().subtract(0, sub, 0).getBlock();
                if (!DETECTOR_MANAGER.isWorkingDetector(block)) return;
                if (COOLDOWNS.contains(uuid)) return;

                DetectorUtils.activateItem(player, block, droppedItem.getItemStack());
                COOLDOWNS.add(uuid);

                // FIXME cannot have so many tasks running at the same time
                //  why does the COOLDOWNS even exist?
                Bukkit.getScheduler().runTaskLaterAsynchronously(instance, () ->
                        COOLDOWNS.remove(uuid), 100L);
            }
        }, 1L, 1L);
    }

    @Override
    protected void stopInternal() {

    }
}
