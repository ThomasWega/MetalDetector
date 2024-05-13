package me.wega.detectors.command;

import me.wega.shadow.commandapi.CommandAPICommand;
import me.wega.shadow.commandapi.arguments.IntegerArgument;
import me.wega.shadow.commandapi.arguments.PlayerArgument;
import me.wega.shadow.commandapi.executors.CommandArguments;
import me.wega.detectors.manager.BlockedMessagesManager;
import me.wega.detectors.manager.DetectorManager;
import me.wega.detectors.config.ConfigValue;
import me.wega.detectors.gui.WhitelistItemsMenu;
import me.wega.toolkit.utils.BlockUtils;
import me.wega.toolkit.utils.ColorUtils;
import me.wega.shadow.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

import static me.wega.detectors.WegaDetectors.instance;
import static me.wega.toolkit.WegaToolkit.adventure;

public final class DetectorsCommand {
    private final DetectorManager detectorManager = instance.getDetectorManager();
    private final BlockedMessagesManager blockedMessagesManager = instance.getBlockedMessagesManager();

    public DetectorsCommand() {
        this.register();
    }

    private void register() {
        new CommandAPICommand("detectors")
                .withSubcommand(
                        new CommandAPICommand("turnon")
                                .withPermission(ConfigValue.Settings.Permissions.TURN_ON_PERMISSION)
                                .executesPlayer(this::turnOn))
                .withSubcommand(
                        new CommandAPICommand("whitelist")
                                .withPermission(ConfigValue.Settings.Permissions.ADMIN_PERMISSION)
                                .executesPlayer(this::whitelist))
                .withSubcommand(
                        new CommandAPICommand("spawn")
                                .withPermission(ConfigValue.Settings.Permissions.ADMIN_PERMISSION)
                                .withArguments(new PlayerArgument("target"))
                                .withOptionalArguments(new IntegerArgument("amount"))
                                .executesPlayer(this::spawn))
                .withSubcommand(
                        new CommandAPICommand("hacktool")
                                .withPermission(ConfigValue.Settings.Permissions.ADMIN_PERMISSION)
                                .withArguments(new PlayerArgument("target"))
                                .withOptionalArguments(new IntegerArgument("amount"))
                                .executesPlayer(this::hackTool)
                )
                .withSubcommand(new CommandAPICommand("message")
                        .executesPlayer(this::message))
                .register();

    }

    private void turnOn(Player player, CommandArguments args) {
        Block targetBlock = player.getTargetBlock(null, 4);
        if (!detectorManager.has(targetBlock))
            adventure.player(player).sendMessage(ColorUtils.color(ConfigValue.Messages.BLOCK_IS_NOT_DETECTOR.getValue()));
        else if (detectorManager.isWorkingDetector(targetBlock))
            adventure.player(player).sendMessage(ColorUtils.color(ConfigValue.Messages.REPAIRING_WORKING_DETECTOR.getValue()));
        else {
            Set<Block> blocks = BlockUtils.getConnected(player.getTargetBlock(null, 5), (block) -> !detectorManager.isWorkingDetector(block));
            Map<Block, Boolean> blockMap = blocks.stream()
                    .collect(Collectors.toMap(block -> block, block -> true));

            detectorManager.addAll(blockMap);
            adventure.player(player).sendMessage(ColorUtils.color(ConfigValue.Messages.DETECTOR_REPAIR.getValue()));
        }
    }

    private void whitelist(Player player, CommandArguments args) {
        new WhitelistItemsMenu().show(player);
    }

    private void spawn(Player player, CommandArguments args) {
        int amount = (int) args.getOptionalUnchecked("amount").orElse(1);
        Player target = (Player) args.get("target");
        assert target != null;

        for (int i = 0; i < amount; ++i)
            target.getInventory().addItem(ConfigValue.Settings.DETECTOR_ITEM);

        adventure.player(player).sendMessage(ColorUtils.color(
                ConfigValue.Messages.DETECTOR_GIVE_MESSAGE.getValue(),
                Placeholder.parsed("amount", String.valueOf(amount)),
                Placeholder.parsed("player", target.getName())
        ));
    }

    private void hackTool(Player player, CommandArguments args) {
        int amount = (int) args.getOptionalUnchecked("amount").orElse(1);
        Player target = (Player) args.get("target");
        assert target != null;

        for (int i = 0; i < amount; ++i)
            target.getInventory().addItem(ConfigValue.Settings.HACK_TOOL);

        adventure.player(player).sendMessage(ColorUtils.color(
                ConfigValue.Messages.HACK_TOOL_GIVE_MESSAGE.getValue(),
                Placeholder.parsed("amount", String.valueOf(amount)),
                Placeholder.parsed("player", target.getName())
        ));
    }

    private void message(Player player, CommandArguments args) {
        UUID uuid = player.getUniqueId();
        if (blockedMessagesManager.has(player.getUniqueId())) {
            adventure.player(player).sendMessage(ColorUtils.color(ConfigValue.Messages.CAN_SEE_DETECTOR_MESSAGES.getValue()));
            blockedMessagesManager.remove(uuid);
        } else {
            adventure.player(player).sendMessage(ColorUtils.color(ConfigValue.Messages.CAN_NOT_SEE_DETECTOR_MESSAGES.getValue()));
            blockedMessagesManager.add(uuid);
        }
    }
}
