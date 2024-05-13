package me.wega.detectors.utils;

import me.wega.detectors.WegaDetectors;
import me.wega.shadow.morepersistentdatatypes.DataType;
import me.wega.toolkit.pdc.key.IPDCKey;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
@Getter
public enum DetectorKey implements IPDCKey {
    USES("uses", PersistentDataType.INTEGER),
    ITEM("detector", DataType.BOOLEAN),
    HACKTOOL("hacktool", DataType.BOOLEAN);


    private final String key;
    private final PersistentDataType<?, ?> type;

    @Override
    public @NotNull JavaPlugin getInstance() {
        return WegaDetectors.instance;
    }

}