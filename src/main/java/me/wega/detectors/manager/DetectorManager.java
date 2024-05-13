package me.wega.detectors.manager;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import me.wega.toolkit.config.ConfigHandler;
import me.wega.toolkit.data.DataMapManager;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Map;

import static me.wega.detectors.WegaMetalDetectors.instance;

public final class DetectorManager extends DataMapManager<Block, Boolean> {
    private final Type type = new TypeToken<Map<Block, Boolean>>(){}.getType();
    private final Gson gson = instance.getGson();
    private final ConfigHandler dataFile = instance.getDataFile();

    public boolean isWorkingDetector(@NotNull Block block) {
        @Nullable Boolean bool = get(block);
        return bool != null && bool;
    }

    public void save() {
        dataFile.getConfig().set("detectors", gson.toJson(this.getMap(), type));
        dataFile.saveConfig();
    }

    public void load() {
        Map<Block, Boolean> map = gson.fromJson(dataFile.getConfig().getString("detectors"), type);
        if (map != null)
            this.set(map);
    }
}

