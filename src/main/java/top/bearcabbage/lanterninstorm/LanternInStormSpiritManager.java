package top.bearcabbage.lanterninstorm;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import top.bearcabbage.lanterninstorm.entity.SpiritLanternEntity;
import top.bearcabbage.lanterninstorm.network.LanternPosPayload;
import top.bearcabbage.lanterninstorm.network.NetworkingConstants;
import top.bearcabbage.lanterninstorm.network.SpiritMassPayload;
import top.bearcabbage.lanterninstorm.utils.Config;
//import top.bearcabbage.lanterninstorm.utils.SpiritData;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import static top.bearcabbage.lanterninstorm.network.NetworkingConstants.NULL_UUID;

public abstract class LanternInStormSpiritManager {

    public static final int DISTANCE_PER_SPIRIT = 10;
    /*
    无向图。两个uuid分别对应玩家和灯笼，没有顺序，因此每个spirit被存储两次。
    如果第一个uuid是玩家，第二个uuid是NULL_UUID，那么代表玩家的leftmass。
     */
    public static Map<UUID, Map<UUID, Integer>> spirit_mass = new HashMap<>();
    public static Map<UUID, GlobalPos> lantern_pos = new HashMap<>();

    public static void lanternPosUpdate(SpiritLanternEntity lantern) {
        UUID uuid = lantern.getUuid();
        GlobalPos pos = GlobalPos.create(lantern.getWorld().getRegistryKey(), lantern.getBlockPos());
        lantern_pos.put(uuid, pos);
    }

    private static boolean confirm_existence(UUID key) {
        if (spirit_mass.containsKey(key)) {
            return true;
        } else {
            spirit_mass.put(key, new HashMap<>());
            return false;
        }
    }

    public static Map get_player_spirits(UUID player) {
        confirm_existence(player);
        return spirit_mass.get(player);
    }


    public static int get(UUID player, UUID lantern) {
        confirm_existence(player);
        confirm_existence(lantern);
        int mass1 = spirit_mass.get(player).getOrDefault(lantern, 0);
        int mass2 = spirit_mass.get(lantern).getOrDefault(player, 0);
        assert (mass1 == mass2);
        return mass1;
    }

    public static void set(UUID player, UUID lantern, int mass) {
        confirm_existence(player);
        confirm_existence(lantern);
        if (mass == 0) {
            spirit_mass.get(player).remove(lantern);
            spirit_mass.get(lantern).remove(player);
        } else {
            spirit_mass.get(player).put(lantern, mass);
            spirit_mass.get(lantern).put(player, mass);
        }
    }

    public static void increase(UUID player, UUID lantern, int mass) {
        int old = get(player, lantern);
        set(player, lantern, old + mass);
    }


    public static int get_left(UUID player) {
        confirm_existence(player);
        return spirit_mass.get(player).getOrDefault(NULL_UUID, 0);
    }

    public static void set_left(UUID player, int mass) {
        confirm_existence(player);
        spirit_mass.get(player).put(NULL_UUID, mass);
    }

    public static void increase_left(UUID player, int mass) {
        int old = get_left(player);
        set_left(player, old + mass);
    }


    public static int get_sum(UUID key) {
        int sum = 0;
        confirm_existence(key);
        for (Integer mass : spirit_mass.get(key).values()) {
            sum += mass;
        }
        return sum;
    }

    // this function DOES NOT require the lantern to be loaded
    public static void remove_lantern_records(UUID lantern) {
        Map<UUID, Integer> players_and_spirits = spirit_mass.getOrDefault(lantern, null);
        if (players_and_spirits != null)
            for (UUID player : players_and_spirits.keySet()) {
                set(player, lantern, 0);
            }
        spirit_mass.remove(lantern);
        lantern_pos.remove(lantern);
    }

    public static void save(Path path) {
        // 保存全局灯笼列表到配置文件
        Config config = new Config(path);
        config.set("spirit_mass", spirit_mass);
        config.set("lantern_pos", lantern_pos);
        config.save();
    }


    public static void load(MinecraftServer server, Path path) {
        // 从配置文件读取全局灯笼列表
        try {
            Config config = new Config(path);
            try {
                spirit_mass = new HashMap<>();
                Map<String, Map<String, Double>> spirit_raw_map = (Map<String, Map<String, Double>>) config.getMap("spirit_mass");
                for (String key1 : spirit_raw_map.keySet()) {
                    Map<String, Double> value1 = spirit_raw_map.get(key1);
                    UUID uuid1 = UUID.fromString(key1);
                    HashMap<UUID, Integer> hashMap = new HashMap<>();
                    spirit_mass.put(uuid1, hashMap);
                    for (String key2 : value1.keySet()) {
                        Integer value2 = value1.get(key2).intValue();
                        if (Objects.equals(key2, "null")) {
                            hashMap.put(NULL_UUID, value2);
                            continue;
                        }
                        UUID uuid2 = UUID.fromString(key2);
                        hashMap.put(uuid2, value2);
                    }
                }

                lantern_pos = new HashMap<>();
                Map<String, Map> lantern_raw_map = (Map<String, Map>) config.getMap("lantern_pos");
                for (String lantern_string : lantern_raw_map.keySet()) {
                    Map value = lantern_raw_map.get(lantern_string);
                    UUID lantern = UUID.fromString(lantern_string);
                    String dimension = (String) ((Map) ((Map) value.get("dimension")).get("value")).get("path");
                    Identifier new_dimension = Identifier.ofVanilla(dimension);
                    Map pos = (Map) value.get("pos");
                    int x = ((Double) pos.get("x")).intValue();
                    int y = ((Double) pos.get("y")).intValue();
                    int z = ((Double) pos.get("z")).intValue();
                    BlockPos new_pos = new BlockPos(x, y, z);
                    GlobalPos new_global_pos = new GlobalPos(RegistryKey.of(RegistryKeys.WORLD, new_dimension), new_pos);
                    lantern_pos.put(lantern, new_global_pos);
                }

            } catch (Exception e) {
                System.out.println(e.toString());
                spirit_mass = new HashMap<>();
                lantern_pos = new HashMap<>();
            }
            config.save();
        } catch (Exception e) {
            System.out.println(e.toString());
            spirit_mass = new HashMap<>();
            lantern_pos = new HashMap<>();
        }
    }

    public static void sendAll(ServerPlayerEntity player) {
        ServerPlayNetworking.send(player, new SpiritMassPayload(spirit_mass));
        ServerPlayNetworking.send(player, new LanternPosPayload(lantern_pos));
    }


    @Environment(EnvType.CLIENT)
    public static void loadMass(Map<UUID, Map<UUID, Integer>> spirit_mass) {
        LanternInStormSpiritManager.spirit_mass = new HashMap<>(spirit_mass);

    }

    @Environment(EnvType.CLIENT)
    public static void loadPos(Map<UUID, GlobalPos> lantern_pos) {
        LanternInStormSpiritManager.lantern_pos = new HashMap<>(lantern_pos);
    }
}
