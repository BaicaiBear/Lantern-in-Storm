package top.bearcabbage.lanterninstorm.lantern;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class LanternTimeManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("LanternTimeManager");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = Path.of("config/lantern_times.json");
    private static final Map<GlobalPos, Integer> lanternTimes = new HashMap<>();
    private static boolean dirty = false;

    // Load from file at startup
    public static void load() {
        lanternTimes.clear();
        if (Files.exists(CONFIG_PATH)) {
            try (FileReader reader = new FileReader(CONFIG_PATH.toFile())) {
                JsonObject obj = JsonParser.parseReader(reader).getAsJsonObject();
                for (String key : obj.keySet()) {
                    // Example key: ResourceKey[minecraft:dimension / starry_skies:overworld] class_2338{x=1256, y=300, z=-1572}
                    try {
                        int dimStart = key.indexOf("/ ") + 2;
                        int dimEnd = key.indexOf("] class_2338");
                        String dimStr = key.substring(dimStart, dimEnd).trim();
                        int posStart = key.indexOf("x=");
                        String posStr = key.substring(posStart).replace('=', ' ');
                        // posStr: "x 1256, y 300, z -1572}"
                        String[] parts = posStr.replace("}", "").split(", ");
                        int x = Integer.parseInt(parts[0].split(" ")[1]);
                        int y = Integer.parseInt(parts[1].split(" ")[1]);
                        int z = Integer.parseInt(parts[2].split(" ")[1]);
                        BlockPos pos = new BlockPos(x, y, z);
                        RegistryKey<World> dimKey = RegistryKey.of(RegistryKeys.WORLD, Identifier.tryParse(dimStr));
                        if (dimKey != null) {
                            GlobalPos gpos = GlobalPos.create(dimKey, pos);
                            lanternTimes.put(gpos, obj.get(key).getAsInt());
                        }
                    } catch (Exception e) {
                        LOGGER.error("Failed to parse lantern time key: " + key, e);
                    }
                }
            } catch (Exception e) {
                LOGGER.error("Failed to load lantern times: ", e);
            }
        }
    }

    // Save to file
    public static void save() {
        JsonObject obj = new JsonObject();
        for (Map.Entry<GlobalPos, Integer> entry : lanternTimes.entrySet()) {
            obj.addProperty(entry.getKey().toString(), entry.getValue());
        }
        try {
            if (Files.notExists(CONFIG_PATH.getParent())) {
                Files.createDirectories(CONFIG_PATH.getParent());
            }
            try (FileWriter writer = new FileWriter(CONFIG_PATH.toFile())) {
                GSON.toJson(obj, writer);
            }
            dirty = false;
        } catch (IOException e) {
            LOGGER.error("Failed to save lantern times: ", e);
        }
    }

    public static void saveIfDirty() {
        if (dirty) save();
    }

    public static void addLantern(GlobalPos pos, int time) {
        lanternTimes.put(pos, time);
        dirty = true;
    }

    public static void removeLantern(GlobalPos pos) {
        lanternTimes.remove(pos);
        dirty = true;
    }

    public static Integer getTime(GlobalPos pos) {
        return lanternTimes.get(pos);
    }

    public static void setTime(GlobalPos pos, int time) {
        lanternTimes.put(pos, time);
        dirty = true;
    }

    public static Map<GlobalPos, Integer> getAllLanterns() {
        return Collections.unmodifiableMap(lanternTimes);
    }

    private static BlockPos parseBlockPos(String s) {
        // Format: BlockPos{x=..., y=..., z=...}
        try {
            String[] parts = s.replace("BlockPos{", "").replace("}", "").split(", ");
            int x = Integer.parseInt(parts[0].split("=")[1]);
            int y = Integer.parseInt(parts[1].split("=")[1]);
            int z = Integer.parseInt(parts[2].split("=")[1]);
            return new BlockPos(x, y, z);
        } catch (Exception e) {
            return null;
        }
    }
} 