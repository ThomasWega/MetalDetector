package me.wega.detectors.utils;

import me.wega.detectors.manager.BlockedMessagesManager;
import me.wega.detectors.WegaMetalDetectors;
import me.wega.detectors.manager.WhitelistManager;
import me.wega.detectors.config.ConfigValue;
import me.wega.toolkit.WegaToolkit;
import me.wega.toolkit.utils.ColorUtils;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class DetectorUtils {
    private static final WhitelistManager WHITELIST_MANAGER = WegaMetalDetectors.instance.getWhitelistManager();
    private static final BlockedMessagesManager BLOCKED_MESSAGES_MANAGER = WegaMetalDetectors.instance.getBlockedMessagesManager();

    public static void activate(@NotNull Player player, @NotNull Block detector) {
        activateItem(player, detector, player.getInventory().getContents());
    }

    public static void activateItem(@NotNull Player player, @NotNull Block detector, ItemStack @NotNull ... itemStack) {
        if (!ItemCheckUtils.anyMatch(WHITELIST_MANAGER.getSet(), Arrays.asList(itemStack), ItemCheckUtils.CheckLevel.REGULAR))
            return;

        detector.getWorld().playSound(detector.getLocation(), Sound.BLOCK_BELL_USE, 2.0F, 2.0F);
        detector.getWorld().getPlayers().stream()
                .filter(online -> !BLOCKED_MESSAGES_MANAGER.has(online.getUniqueId()))
                .filter(online -> player.getLocation().distance(online.getLocation()) <= 30.0)
                .forEach(online -> WegaToolkit.adventure.player(online).sendMessage(ColorUtils.color(ConfigValue.Messages.DETECTED_MESSAGE.getValue())));
    }
}
