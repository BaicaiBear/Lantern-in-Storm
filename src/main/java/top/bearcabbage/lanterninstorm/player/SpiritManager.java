package top.bearcabbage.lanterninstorm.player;

import com.google.gson.JsonSyntaxException;
import net.minecraft.server.MinecraftServer;
import top.bearcabbage.lanterninstorm.entity.SpiritLanternEntity;
import top.bearcabbage.lanterninstorm.utils.Config;
//import top.bearcabbage.lanterninstorm.utils.SpiritData;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public abstract class SpiritManager {

    /*
    无向图。两个uuid分别对应玩家和灯笼，没有顺序，因此每个spirit被存储两次。
    如果第一个uuid是玩家，第二个uuid是null，那么代表玩家的leftmass。
     */
    public static HashMap<UUID, HashMap<UUID, Integer>> spirit_mass = new HashMap<>();

    private static boolean confirm_existence(UUID key){
        if(spirit_mass.containsKey(key)){
            return true;
        } else{
            spirit_mass.put(key, new HashMap<>());
            return false;
        }
    }


    public static int get(UUID player, UUID lantern){
        confirm_existence(player);
        confirm_existence(lantern);
        int mass1 = spirit_mass.get(player).getOrDefault(lantern, 0);
        int mass2 = spirit_mass.get(lantern).getOrDefault(player, 0);
        assert (mass1 == mass2);
        return mass1;
    }

    public static void set(UUID player, UUID lantern, int mass){
        confirm_existence(player);
        confirm_existence(lantern);
        if (mass==0) {
            spirit_mass.get(player).remove(lantern);
            spirit_mass.get(lantern).remove(player);
        } else {
            spirit_mass.get(player).put(lantern, mass);
            spirit_mass.get(lantern).put(player, mass);
        }
    }

    public static void increase(UUID player, UUID lantern, int mass){
        int old = get(player, lantern);
        set(player, lantern, old + mass);
    }


    public static int get_left(UUID player){
        confirm_existence(player);
        return spirit_mass.get(player).getOrDefault(null, 0);
    }

    public static void set_left(UUID player, int mass){
        confirm_existence(player);
        if (mass==0) {
            spirit_mass.get(player).remove(null);
        } else {
            spirit_mass.get(player).put(null, mass);
        }
    }

    public static void increase_left(UUID player, int mass){
        int old = get_left(player);
        set_left(player,old + mass);
    }


    public static int get_sum(UUID key){
        int sum = 0;
        confirm_existence(key);
        for(Integer mass: spirit_mass.get(key).values()){
            sum += mass;
        }
        return sum;
    }


    public static void save(Path path){
        // 保存全局灯笼列表到配置文件
        Config config = new Config(path);
        config.set("spirit_mass", spirit_mass);
        config.save();
        config.close();
    }
    public static void load(MinecraftServer server, Path path){
        // 从配置文件读取全局灯笼列表
        try {
            Config config = new Config(path);
            try {
                spirit_mass = new HashMap<>();
                Map<String, Map<String, Double>> map = (Map<String, Map<String, Double>>)config.getMap("spirit_mass");
                for(String key1: map.keySet()){
                    Map<String, Double> value1 = map.get(key1);
                    UUID uuid1 = UUID.fromString(key1);
                    HashMap<UUID, Integer> hashMap = new HashMap<>();
                    spirit_mass.put(uuid1, hashMap);
                    for(String key2:value1.keySet()){
                        Integer value2 = value1.get(key2).intValue();
                        if(Objects.equals(key2, "null")){
                            hashMap.put(null, value2);
                            continue;
                        }
                        UUID uuid2 = UUID.fromString(key2);
                        hashMap.put(uuid2, value2);
                    }
                }
            } catch(Exception e) {
                spirit_mass = new HashMap<>();
            }
            config.close();
        } catch(Exception e){
            spirit_mass = new HashMap<>();
        }
    }
}
