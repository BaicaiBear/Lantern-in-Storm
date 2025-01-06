package top.bearcabbage.lanterninstorm;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import top.bearcabbage.lanterninstorm.utils.Config;

import java.nio.file.Path;
import java.util.*;
;

public abstract class LanternInStormLanternManager {
    public static final Set<GlobalPos> LANTERNS_ON = new HashSet<>();

    public static void addLantern(GlobalPos pos) {
        LANTERNS_ON.add(pos);
    }

    public static void removeLantern(GlobalPos pos) {
        LANTERNS_ON.remove(pos);
    }

    public static boolean hasLanternOn(GlobalPos pos) {
        return LANTERNS_ON.contains(pos);
    }

    public static void save(Path path) {
        Config config = new Config(path);
        config.set("lanterns_on", LANTERNS_ON);
        config.save();
    }

    public static void load(Path path) {
        Config config = new Config(path);
        try {
            LANTERNS_ON.clear();
            var tmpSet = config.get("lanterns_on", Set.class);
            for (var pos : tmpSet) {
                Map value = (Map) pos;
                Identifier new_dimension = Identifier.of((String) ((Map) ((Map) value.get("dimension")).get("value")).get("namespace"), (String) ((Map) ((Map) value.get("dimension")).get("value")).get("path"));
                Map posMap = (Map) value.get("pos");
                int x = ((Double) posMap.get("x")).intValue();
                int y = ((Double) posMap.get("y")).intValue();
                int z = ((Double) posMap.get("z")).intValue();
                BlockPos new_pos = new BlockPos(x, y, z);
                GlobalPos new_global_pos = new GlobalPos(RegistryKey.of(RegistryKeys.WORLD, new_dimension), new_pos);
                LANTERNS_ON.add(new_global_pos);
            }
        } catch (Exception e) {
            LanternInStorm.LOGGER.warn("No Lantern Data, generating blank file...");
        }
    }
}
