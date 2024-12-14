package top.bearcabbage.lanterninstorm.entity;

import net.minecraft.server.MinecraftServer;
import top.bearcabbage.lanterninstorm.player.Spirit;
import top.bearcabbage.lanterninstorm.utils.Config;
import top.bearcabbage.lanterninstorm.utils.SpiritData;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public abstract class SpiritLanternEntityManager {
    /*
        把Spirit数据存在灯笼实体，实体在被加载/卸载的时候通知全局灯笼列表；玩家通过全局灯笼列表访问灯笼
        灯笼实体 -> 全局灯笼列表 -> 玩家灵魂初始化
        全局灯笼列表：{灯笼ID，是否被卸载，灯笼实体（卸载则为null），灵魂（位置及质量）}
     */
    // List 为 [? extends SpiritLanternEntity, Map<UUID, Spirit>]的结构
    public static final Map<String, List<Object>> LANTERNS = new HashMap<>();

    // PlayerSpirit请求给定LSID灯笼中的灵魂
    public static Spirit getSpiritFromPlayer(String LSid, UUID uuid){
        List<Object> lantern = LANTERNS.get(LSid);
        if (lantern == null || lantern.size()!=2){
            return null;
        }
        if (lantern.get(0) == null){
            return (Spirit) ((Map<?, ?>)lantern.get(1)).get(uuid);
        } else return ((SpiritLanternEntity) lantern.get(0)).getSpirit(uuid);
    }

    // SpiritLanternEntity被加载
    public static void loadSpiritLanternEntity(SpiritLanternEntity entity){
        LANTERNS.put(entity.getLSid(), Arrays.asList(entity, entity.getSpiritMap()));
    }

    // SpiritLanternEntity被卸载
    public static void unloadSpiritLanternEntity(SpiritLanternEntity entity){
        LANTERNS.put(entity.getLSid(), Arrays.asList(null, entity.getSpiritMap()));
    }

    // Mod初始化时从配置文件读取全局灯笼列表
    public static void load(MinecraftServer server, Path path){
        // 从配置文件读取全局灯笼列表
        Config config = new Config(path);
        Map<String, Object> lanternsDataRaw = config.getMap();
        // 将lanternsDataRaw中的List<SpiritData>转换为Collection<SpiritData>
        Map<String, Collection<SpiritData>> lanternsData = new HashMap<>();
        lanternsDataRaw.forEach((k, v) -> {
            Collection<SpiritData> spirits = ((List<?>)v).stream()
                    .map(data -> new SpiritData((Spirit) data))
                    .collect(Collectors.toList());
            lanternsData.put(k, spirits);
        });
        // 用toSpirit方法将SpiritData类型转换为Spirit类型
        if (lanternsData != null){
            Map<String, Collection<Spirit>> lanterns = new HashMap<>();
            lanternsData.forEach((k, v) -> {
                Collection<Spirit> spirits = v.stream()
                        .map(data -> data.toSpirit(server))
                        .collect(Collectors.toList());
                lanterns.put(k, spirits);
            });
            // 通过访问Spirit中的getOwnerUUID()方法将Collection<Spirit>整理为Map<UUID,Spirit>
            lanterns.forEach((k, v) -> {
                Map<UUID, Spirit> spirits = new HashMap<>();
                v.forEach(spirit -> spirits.put(spirit.getOwnerUUID(), spirit));
                LANTERNS.put(k, Arrays.asList(null, spirits));
            });
        } else {
            LANTERNS.clear();
        }
        config.close();
    }

    // Mod关闭时保存全局灯笼列表到配置文件
    public static void save(Path path){
        // 保存全局灯笼列表到配置文件
        Config config = new Config(path);
        // 取出LANTERNS中String和List<Spirit>的键值对
        Map<String, Collection<Spirit>> lanterns = new HashMap<>();
        LANTERNS.forEach((k, v) -> {
            lanterns.put(k, ((Map<UUID,Spirit>)v.get(1)).values());
        });
        // 将lantern中Collection<Spirit>List<SpiritData>类型
        Map<String, List<SpiritData>> lanternsData = new HashMap<>();
        lanterns.forEach((k, v) -> {
            List<SpiritData> spirits = v.stream()
                    .map(SpiritData::new)
                    .collect(Collectors.toList());
            lanternsData.put(k, spirits);
            config.set(k, spirits);
        });
        config.save();
        config.close();
    }
}
