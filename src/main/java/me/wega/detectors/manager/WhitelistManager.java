package me.wega.detectors.manager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import me.wega.toolkit.config.ConfigHandler;
import me.wega.toolkit.data.DataManager;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import static me.wega.detectors.WegaDetectors.instance;

public class WhitelistManager extends DataManager<ItemStack> {
    private final Gson gson = instance.getGson();
    private final ConfigHandler dataFile = instance.getDataFile();
    private final Type type = new TypeToken<List<ItemStack>>(){}.getType();

    @Override
    public boolean add(@NotNull ItemStack data) {
        boolean result = super.add(data);
        if (result)
            this.save();
        return result;
    }

    @Override
    public @NotNull Collection<ItemStack> addAll(@NotNull Collection<ItemStack> data) {
        Collection<ItemStack> result = super.addAll(data);
        this.save();
        return result;
    }

    @Override
    public void set(@NotNull Collection<ItemStack> data) {
        super.set(data);
        this.save();
    }

    @Override
    public boolean remove(@NotNull ItemStack data) {
        boolean result = super.remove(data);
        if (result)
            this.save();
        return result;
    }

    @Override
    public void clear() {
        super.clear();
        this.save();
    }

    public void save() {
        // FIXME fix saving
     //   dataFile.getConfig().set("whitelisted", gson.toJson(this.getSet(), type));
        dataFile.saveConfig();
    }
    public void load() {
        List<ItemStack> items = gson.fromJson(dataFile.getConfig().getString("whitelisted"), type);
        if (items != null)
            this.set(items);
    }
}
