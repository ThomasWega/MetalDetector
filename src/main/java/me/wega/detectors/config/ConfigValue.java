package me.wega.detectors.config;

import me.wega.detectors.WegaDetectors;
import me.wega.detectors.utils.DetectorKey;
import me.wega.toolkit.builder.ConfigItemBuilder;
import me.wega.toolkit.config.ConfigHandler;
import me.wega.toolkit.config.ConfigProperty;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public class ConfigValue {
    public static final YamlConfiguration CONFIG_FILE;

    static {
        ConfigHandler settingsFile = new ConfigHandler(WegaDetectors.instance, "", "config.yml");
        settingsFile.saveDefaultConfig();
        CONFIG_FILE = settingsFile.getConfig();
    }

    public static class Messages {
        public static final YamlConfiguration MESSAGES_FILE;

        static {
            ConfigHandler mesagesFile = new ConfigHandler(WegaDetectors.instance, "", "messages.yml");
            mesagesFile.saveDefaultConfig();
            MESSAGES_FILE = mesagesFile.getConfig();
        }

        public static final ConfigProperty<String> BLOCK_IS_NOT_DETECTOR = new ConfigProperty<>(MESSAGES_FILE, "block-is-not-detector");
        public static final ConfigProperty<String> REPAIRING_WORKING_DETECTOR = new ConfigProperty<>(MESSAGES_FILE, "repairing-working-detector");
        public static final ConfigProperty<String> DETECTOR_REPAIR = new ConfigProperty<>(MESSAGES_FILE, "detector-repair");
        public static final ConfigProperty<String> DETECTOR_GIVE_MESSAGE = new ConfigProperty<>(MESSAGES_FILE, "detector-give-message");
        public static final ConfigProperty<String> HACK_TOOL_GIVE_MESSAGE = new ConfigProperty<>(MESSAGES_FILE, "hack-tool-give-message");
        public static final ConfigProperty<String> CAN_SEE_DETECTOR_MESSAGES = new ConfigProperty<>(MESSAGES_FILE, "can-see-detector-messages");
        public static final ConfigProperty<String> CAN_NOT_SEE_DETECTOR_MESSAGES = new ConfigProperty<>(MESSAGES_FILE, "can-not-see-detector-messages");
        public static final ConfigProperty<String> DETECTOR_BREAK_FAIL = new ConfigProperty<>(MESSAGES_FILE, "detector-break-fail");
        public static final ConfigProperty<String> DETECTOR_BREAK_SUCCESS = new ConfigProperty<>(MESSAGES_FILE, "detector-break-success");
        public static final ConfigProperty<String> DETECTED_MESSAGE = new ConfigProperty<>(MESSAGES_FILE, "detected-message");
    }

    public static class Settings {
        public static final ItemStack DETECTOR_ITEM = new ConfigItemBuilder(new ConfigProperty<ConfigurationSection>(CONFIG_FILE, "custom-items.detector").getValue())
                .builder()
                .pdcKey(DetectorKey.ITEM, true)
                .build();
        public static final ItemStack HACK_TOOL = new ConfigItemBuilder(new ConfigProperty<ConfigurationSection>(CONFIG_FILE, "custom-items.hack-tool").getValue())
                .builder()
                .pdcKey(DetectorKey.HACKTOOL, true)
                .pdcKey(DetectorKey.USES, new ConfigProperty<Integer>(CONFIG_FILE, "custom-items.hack-tool.uses").getValue())
                .build();
        public static final double[] SUBTRACTIONS = {0.0, 1.0, 2.0, 3.0};

        public static class Permissions {
            public static final String ADMIN_PERMISSION = "wegadetectors.admin.detectors";
            public static final String TURN_ON_PERMISSION = "wegadetectors.detectors.turnon";

        }
    }
}