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

    /*
        破防的xxbc：
            求求好人帮我改改下面的load和save吧啊啊啊啊啊啊啊啊
            现在只能存出来这样的json：
                {
                  "8a2cfe7a-e562-48aa-97e4-34377274bdd9": [],
                  "b6273155-f1f1-406d-b0e2-e553c969669e": [],
                  "ba057be4-bda7-4f61-87cc-27c90a2c82b7": [],
                  "0d1192c8-5a9f-416f-bc35-ed4d26bacf76": [],
                  "ea18c990-766d-4ee5-bac9-8ca73d4e8fb9": [],
                  "3f331361-2af7-4d39-bd35-b2b325ac4150": [],
                  "4f5de68e-eaac-4280-8432-ed38390cc7ad": [],
                  "5f47f47b-5e61-4c62-9bdd-25b76c7baaa5": []
                }
             简单来说，现在做的事情是在试图调用Google的Gson包（在utils.Config里做了些小工具）来存，但这需要将数据可逆转换为Gson能读写的简单的数据结构。
             以下就是在尝试转换）））））））））））））））））））））））））））））））））））））））））））））））））））））））））））））））））））））））））））））））））））））爆了！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
            此致
        敬礼
     */
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
