package me.wega.detectors.listener;

import me.wega.detectors.manager.DetectorManager;
import me.wega.detectors.config.ConfigValue;
import me.wega.detectors.gui.HackingMiniGame;
import me.wega.detectors.task.DetectorHackToolTask;
import me.wega.detectors.utils.DetectorKey;
import me.wega.toolkit.utils.BlockUtils;
import me.wega.toolkit.utils.ColorUtils;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import static me.wega.detectors.WegaMetalDetectors.instance;
import static me.wega.toolkit.WegaToolkit.adventure;

public final class DetectorHackListener implements Listener {
    private final DetectorManager detectorManager = instance.getDetectorManager();

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Player player = event.getPlayer();
        if (!player.isSneaking()) return;
        if (!event.hasItem()) return;
        ItemStack itemInHand = event.getItem();
        ItemMeta meta = itemInHand.getItemMeta();
        if (!DetectorKey.HACKTOOL.has(meta)) return;
        if (!detectorManager.isWorkingDetector(event.getClickedBlock())) return;

        DetectorHackToolTask task = new DetectorHackToolTask(player, itemInHand);
        task.setOnComplete(() -> {
            int uses = DetectorKey.USES.getInt(meta);
            Damageable dam = (Damageable) meta;
            short maxDur = itemInHand.getType().getMaxDurability();
            dam.setDamage(dam.getDamage() + (int) Math.ceil((double) maxDur / uses));
            itemInHand.setItemMeta((ItemMeta) dam);
            if (dam.getDamage() >= maxDur)
                player.getInventory().setItemInMainHand(null);

            // start the minigame
            HackingMiniGame miniGame = new HackingMiniGame();
            miniGame.setOnEnd(bool -> {
                player.closeInventory();
                if (bool) {
                    adventure.player(player).sendMessage((ColorUtils.color(ConfigValue.Messages.METAL_DETECTOR_BREAK_SUCCESS.getValue())));
                    for (Block detector : BlockUtils.getConnected(event.getClickedBlock(), detectorManager::isWorkingDetector))
                        detectorManager.add(detector, false);
                } else
                    adventure.player(player).sendMessage((ColorUtils.color(ConfigValue.Messages.METAL_DETECTOR_BREAK_FAIL.getValue())));
            });
            miniGame.show(player);
        });
        task.run();
    }
}
