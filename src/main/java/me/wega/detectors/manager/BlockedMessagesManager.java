package me.wega.detectors.manager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import me.wega.toolkit.config.ConfigHandler;
import me.wega.toolkit.data.DataManager;

import java.lang.reflect.Type;
import java.util.List;
import java.util.UUID;

import static me.wega.detectors.WegaMetalDetectors.instance;

public final class BlockedMessagesManager extends DataManager<UUID> {
    private final Gson gson = instance.getGson();
    private final ConfigHandler dataFile = instance.getDataFile();
    private final Type type = new TypeToken<List<UUID>>(){}.getType();

    public void save() {
        dataFile.getConfig().set("blocked-messages", gson.toJson(this.getSet(), type));
        dataFile.saveConfig();
    }
    public void load() {
        List<UUID> uuids = gson.fromJson(dataFile.getConfig().getString("blocked-messages"), type);
        if (uuids != null)
            this.set(uuids);
    }
}

