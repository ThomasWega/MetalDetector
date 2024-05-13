package me.wega.detectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.wega.detectors.command.MetalDetectorCommand;
import me.wega.detectors.listener.DetectorPlaceBreakListener;
import me.wega.detectors.listener.DetectorAlertListener;
import me.wega.detectors.listener.DetectorHackListener;
import me.wega.detectors.manager.BlockedMessagesManager;
import me.wega.detectors.manager.DetectorManager;
import me.wega.detectors.manager.WhitelistManager;
import me.wega.toolkit.config.ConfigHandler;
import me.wega.toolkit.json.adapter.BlockAdapter;
import me.wega.toolkit.json.adapter.ItemStackAdapter;
import me.wega.toolkit.json.adapter.WorldAdapter;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

@Getter
public final class WegaMetalDetectors extends JavaPlugin {
    public static final Random RANDOM = new Random();
    public static WegaMetalDetectors instance;
    private WhitelistManager whitelistManager;
    private BlockedMessagesManager blockedMessagesManager;
    private DetectorManager detectorManager;
    private ConfigHandler dataFile;
    private final Gson gson = new GsonBuilder()
            .enableComplexMapKeySerialization()
            .registerTypeAdapter(ItemStack.class, new ItemStackAdapter())
            .registerTypeHierarchyAdapter(Block.class, new BlockAdapter())
            .registerTypeHierarchyAdapter(World.class, new WorldAdapter())
            .create();

    @Override
    public void onEnable() {
        instance = this;

        this.dataFile = new ConfigHandler(WegaMetalDetectors.instance, "", "data.json");
        dataFile.scheduleAsyncBackup(5 * 60 * 20, this::saveData);

        this.initializeManagers();
        this.loadData();

        this.registerListeners();
        this.registerCommands();
    }

    private void initializeManagers() {
        this.whitelistManager = new WhitelistManager();
        this.blockedMessagesManager = new BlockedMessagesManager();
        this.detectorManager = new DetectorManager();
    }

    private void loadData() {
        this.blockedMessagesManager.load();
        this.whitelistManager.load();
        this.detectorManager.load();
    }

    private void saveData() {
        detectorManager.save();
        blockedMessagesManager.save();
    }

    private void registerCommands() {
        new MetalDetectorCommand();
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new DetectorHackListener(), this);
        Bukkit.getPluginManager().registerEvents(new DetectorPlaceBreakListener(), this);
        Bukkit.getPluginManager().registerEvents(new DetectorAlertListener(), this);
    }

    @Override
    public void onDisable() {
        this.saveData();
    }
}
