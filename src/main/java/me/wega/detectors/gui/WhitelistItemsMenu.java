package me.wega.detectors.gui;

import me.wega.shadow.IF.gui.GuiItem;
import me.wega.shadow.IF.gui.type.ChestGui;
import me.wega.shadow.IF.pane.PaginatedPane;
import me.wega.shadow.IF.pane.Pane;
import me.wega.detectors.WegaMetalDetectors;
import me.wega.detectors.manager.WhitelistManager;
import me.wega.toolkit.gui.pane.PageNavPane;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

// FIXME fix menu
public class WhitelistItemsMenu extends ChestGui {
    private static final WhitelistManager WHITELIST_MANAGER = WegaMetalDetectors.instance.getWhitelistManager();
    private final PaginatedPane pagesPane = new PaginatedPane(0, 0, 9, 5, Pane.Priority.HIGH);
    private final List<ItemStack> items = WHITELIST_MANAGER.getSet()
            .stream()
            .map(ItemStack::clone)
            .collect(Collectors.toList());

    public WhitelistItemsMenu() {
        this(0);
    }

    public WhitelistItemsMenu(int page) {
        super(6, "MetalDetector Whitelist");
        this.initialize(page);
    }

    private void initialize(int page) {
        this.pagesPane.populateWithGuiItems(this.items.stream()
                .map(item -> {
                    ItemStack itemClone = item.clone();
                            return new GuiItem(itemClone, event -> {
                                WHITELIST_MANAGER.remove(itemClone);
                                new WhitelistItemsMenu(this.pagesPane.getPage()).show(event.getWhoClicked());
                            });
                        }
                )
                .collect(Collectors.toList())
        );
        this.addPane(new PageNavPane(this, this.pagesPane, 0, 5, 9, 1));
        this.addPane(this.pagesPane);
        if (page > 0 && this.pagesPane.getPages() > 0)
            this.pagesPane.setPage(Math.min(this.pagesPane.getPages(), page));

        this.setOnClose(this::onClose);
        this.setOnBottomClick(this::handleItemAdd);
    }

    private void handleItemAdd(InventoryClickEvent event) {
        event.setCancelled(true);
        ItemStack current = event.getCurrentItem();
        if (current == null) return;
        if (WHITELIST_MANAGER.has(current)) return;

        System.out.println("ADDING ITEM = " + current);
        WHITELIST_MANAGER.add(current.clone());
        System.out.println(WHITELIST_MANAGER.getSet());
        new WhitelistItemsMenu().show(event.getWhoClicked());
    }

    private void onClose(InventoryCloseEvent event) {
        WHITELIST_MANAGER.set(this.items);
    }
}
