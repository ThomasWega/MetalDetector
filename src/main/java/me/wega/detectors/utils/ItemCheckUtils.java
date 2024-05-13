package me.wega.detectors.utils;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Objects;

public final class ItemCheckUtils {

    private static boolean matches(@NotNull Collection<ItemStack> filter, @NotNull ItemStack item, @NotNull CheckLevel checkLevel) {
        return filter.stream()
                .filter(Objects::nonNull)
                .anyMatch(itemStack -> {
                    boolean sameType = itemStack.getType() == item.getType();
                    boolean customModelData = getCustomModelData(itemStack) == getCustomModelData(item);
                    if (checkLevel == CheckLevel.EXACT)
                        return itemStack.equals(item);
                     else if (checkLevel == CheckLevel.REGULAR)
                        return sameType && customModelData;
                     else if (checkLevel == CheckLevel.TYPE_ONLY)
                        return sameType;

                    return false;
                });
    }

    public static boolean anyMatch(@NotNull Collection<ItemStack> filter, @NotNull Collection<ItemStack> items, @NotNull CheckLevel checkLevel) {
        return items.stream()
                .filter(Objects::nonNull)
                .anyMatch(item -> matches(filter, item, checkLevel));
    }

    private static int getCustomModelData(@NotNull ItemStack item) {
        return item.hasItemMeta() && item.getItemMeta().hasCustomModelData() ? item.getItemMeta().getCustomModelData() : -1;
    }
    
    public enum CheckLevel {
        TYPE_ONLY,
        REGULAR,
        EXACT
    }
}
