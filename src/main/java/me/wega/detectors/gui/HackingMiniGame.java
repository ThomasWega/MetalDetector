package me.wega.detectors.gui;

import me.wega.shadow.IF.gui.GuiItem;
import me.wega.shadow.IF.gui.type.ChestGui;
import me.wega.shadow.IF.pane.Pane;
import me.wega.shadow.IF.pane.StaticPane;
import me.wega.detectors.WegaDetectors;
import me.wega.toolkit.builder.ItemBuilder;
import me.wega.toolkit.utils.SchedulerUtils;
import lombok.Setter;
import me.wega.shadow.kyori.adventure.text.Component;
import me.wega.shadow.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class HackingMiniGame extends ChestGui {
    private static final String CHARACTER_SET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    @Setter
    public @Nullable Consumer<Boolean> onEnd;
    private final StaticPane backgroundPane = new StaticPane(0, 0, 9, 6);
    private final StaticPane clickItemsPane = new StaticPane(0, 0, 9, 6);
    private final String encryptionKey = this.generateRandomKey(3, 10);

    public HackingMiniGame() {
        super(6, "Hacking - XXX");
        this.initialize();
        this.playTimer();
    }

    private void playTimer() {
        SchedulerUtils.runTaskLater(() -> {
            // if clickedPane does not have any items, the player succeeded. If it has, the player failed.
            if (onEnd != null)
                onEnd.accept(clickItemsPane.getItems().isEmpty());
        }, this.getTicksTime());
    }

    private void initialize() {
        this.backgroundPane.setPriority(Pane.Priority.NORMAL);
        this.clickItemsPane.setPriority(Pane.Priority.HIGH);

        this.backgroundPane.fillWith(new ItemBuilder(Material.WHITE_WOOL)
                .displayName(Component.empty())
                .hideFlags()
                .build()
        );

        this.setTitle("Hacking - " + this.encryptionKey);

        this.addClickItems();

        this.addPane(this.backgroundPane);
        this.addPane(this.clickItemsPane);

        this.setOnGlobalClick(event -> event.setCancelled(true));
    }

    private void addClickItems() {
        for (int i = -1; i < this.getItemCount(); i++) {
            GuiItem item = new GuiItem(new ItemBuilder(Material.GREEN_WOOL)
                    .displayName(Component.text("Click me", NamedTextColor.GREEN))
                    .hideFlags()
                    .build()
            );

            item.setAction(event -> {
                this.clickItemsPane.removeItem(item);
                if (this.clickItemsPane.getItems().isEmpty() && onEnd != null)
                    onEnd.accept(true);
                this.update();
            });

            this.clickItemsPane.addItem(item, this.getRandomX(), this.getRandomY());
        }
    }

    private int getRandomX() {
        return WegaDetectors.RANDOM.nextInt(0, 8);
    }

    private int getRandomY() {
        return WegaDetectors.RANDOM.nextInt(0, 5);
    }

    private int getItemCount() {
        return this.encryptionKey.length();
    }

    private int getTicksTime() {
        return 250 / this.encryptionKey.length();
    }


    private String generateRandomKey(int minLength, int maxLength) {
        if (minLength <= 0 || maxLength <= 0 || minLength > maxLength)
            throw new IllegalArgumentException("Invalid input parameters");

        int length = minLength + WegaDetectors.RANDOM.nextInt(maxLength - minLength + 1);

        StringBuilder randomString = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int randomIndex = WegaDetectors.RANDOM.nextInt(CHARACTER_SET.length());
            char randomChar = CHARACTER_SET.charAt(randomIndex);
            randomString.append(randomChar);
        }

        return randomString.toString();
    }
}
