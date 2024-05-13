package me.wega.detectors.task;

import me.wega.toolkit.task.PlayerTask;
import me.wega.toolkit.utils.SchedulerUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class DetectorHackToolTask extends PlayerTask {
    private static final Set<UUID> RUNNING_FOR = new HashSet<>();
    private final @NotNull ItemStack hackTool;
    private final @NotNull UUID uuid;

    public DetectorHackToolTask(@NotNull Player player, @NotNull ItemStack hackTool) {
        super(player);
        this.hackTool = hackTool;
        this.uuid = player.getUniqueId();
    }

    @Override
    protected @Nullable BukkitTask runInternal() {
        final Player player = getPlayer();
        final UUID uuid = player.getUniqueId();
        if (RUNNING_FOR.contains(uuid))
            return null;

        RUNNING_FOR.add(uuid);
        final int initialRemainingTicks = 2 * 20;
        final int[] remainingTicks = {initialRemainingTicks};
        return SchedulerUtils.runTaskTimer(() -> {
            if (!player.getInventory().getItemInMainHand().equals(hackTool)) {
                stop();
                return;
            }
            if (remainingTicks[0] == 0) {
                stop();
                if (getOnComplete() != null)
                    getOnComplete().run();
                return;
            }

            remainingTicks[0]--;
            player.setExp((float) (initialRemainingTicks - remainingTicks[0]) / (float) initialRemainingTicks);

        }, 1, 1);
    }

    @Override
    protected void stopInternal() {
        RUNNING_FOR.remove(uuid);
        getPlayer().setExp(0F);
    }
}
