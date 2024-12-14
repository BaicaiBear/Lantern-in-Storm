package top.bearcabbage.lanterninstorm.player;

import eu.pb4.playerdata.api.PlayerDataApi;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import top.bearcabbage.lanterninstorm.LanternInStorm;
import top.bearcabbage.lanterninstorm.entity.SpiritLanternEntity;
import top.bearcabbage.lanterninstorm.utils.Config;

import java.util.HashMap;
import java.util.Map;

import static top.bearcabbage.lanterninstorm.utils.Math.HorizontalDistance;

/*
    这是之前的Spirit类
    作为玩家属性的PlayerSpirit与Player一一对应
 */
public class PlayerSpirit {

    public static final int SPIRIT_RADIUS = 10;

    private int mass;
    private int leftMass;
    private final ServerPlayerEntity player;
    /*
        把Spirit数据存在灯笼实体，实体在被加载/卸载的时候通知全局灯笼列表；玩家通过全局灯笼列表访问灯笼
        灯笼实体 -> 全局灯笼列表 -> 玩家灵魂初始化
        全局灯笼列表：{灯笼ID，是否被卸载，灯笼实体（卸载则为null），灵魂（位置及质量）}
     */
    private Map<String, Spirit> SPIRIT_OVERWORLD = new HashMap<>();
    private Map<String, Spirit> SPIRIT_NETHER = new HashMap<>();
    private Map<String, Spirit> SPIRIT_END = new HashMap<>();
    private final Map<RegistryKey<World>, Map<String, Spirit>> SPIRIT = new HashMap<>();

    public PlayerSpirit(ServerPlayerEntity player, boolean firstTime) {
        {
            this.player = player;
            Config config = new Config(PlayerDataApi.getPathFor(this.player).resolve("lanterninstorm.json"));
            if (firstTime) {
                mass = leftMass = 0;
                config.set("spirit_mass", mass);
                config.set("spirit_left_mass", leftMass);
                config.set("SPIRIT_OVERWORLD", SPIRIT_OVERWORLD);
                config.set("SPIRIT_NETHER", SPIRIT_NETHER);
                config.set("SPIRIT_END", SPIRIT_END);
                config.save();
            } else {
                mass = config.getInt("spirit_mass");
                leftMass = config.getInt("spirit_left_mass");
                SPIRIT_OVERWORLD = config.getMap("SPIRIT_OVERWORLD");
                SPIRIT_NETHER = config.getMap("SPIRIT_NETHER");
                SPIRIT_END = config.getMap("SPIRIT_END");
            }
            config.close();
            SPIRIT.computeIfAbsent(World.OVERWORLD, k -> SPIRIT_OVERWORLD);
            SPIRIT.computeIfAbsent(World.NETHER, k -> SPIRIT_NETHER);
            SPIRIT.computeIfAbsent(World.END, k -> SPIRIT_END);
        }
    }

    // 玩家获取灵魂
    public boolean addMass(int i) {
        mass += i;
        leftMass += i;
        NbtCompound data = PlayerDataApi.getCustomDataFor(player, LanternInStorm.LSData);
        data.putInt("spirit_mass", mass);
        data.putInt("spirit_left_mass", leftMass);
        PlayerDataApi.setCustomDataFor(player, LanternInStorm.LSData, data);
        return true;
    }

    // 检查玩家是否在安全区内
    public boolean isStabilized() {
        Map<String, Spirit> spiritPos = SPIRIT.get(player.getServerWorld().getRegistryKey());
        if (spiritPos == null) {
            return false;
        }
        // 检查该维度内是否有灵魂位置在玩家周围满足半径要求
        else return spiritPos.values().stream().anyMatch(pos -> (HorizontalDistance(player.getPos(), pos.getPos()) < pos.getSpiritMass() * SPIRIT_RADIUS));
    }

    // 灵魂灯笼添减灵魂 LSid为灵魂灯笼实体的唯一识别ID
    public boolean setSpiritToLantern(SpiritLanternEntity lantern, int i) {
        if (lantern.getEntityWorld().isClient) {
            return false;
        }
        Map<String, Spirit> spiritPosMap = SPIRIT.get(lantern.getEntityWorld().getRegistryKey());
        Spirit pos_prev = spiritPosMap.get(lantern.getLSid());
        if (pos_prev == null) {
            pos_prev = Spirit.of(this.player,lantern.getPos(), 0, lantern.getEntityWorld().getRegistryKey());
        }
        if (i - pos_prev.getSpiritMass() > leftMass) {
            // 剩余的不足以填充这么多的灵魂进入灯笼
            return false;
        }
        leftMass -= i - pos_prev.getSpiritMass();
        spiritPosMap.put(lantern.getLSid(), Spirit.of(this.player, lantern.getPos(), i, lantern.getEntityWorld().getRegistryKey()));
        return true;
    }

    // 灵魂灯笼死亡
    public boolean removeSpiritFromLantern(SpiritLanternEntity lantern) {
        if (lantern.getEntityWorld().isClient) {
            return false;
        }
        Map<String, Spirit> spiritPosMap = SPIRIT.get(lantern.getEntityWorld().getRegistryKey());
        Spirit pos_prev = spiritPosMap.get(lantern.getLSid());
        if (pos_prev == null) {
            return false;
        }
        mass -= pos_prev.getSpiritMass();
        spiritPosMap.remove(lantern.getLSid());
        return true;
    }

    public int getMass() {
        return mass;
    }

    public void save(){
        Config config = new Config(PlayerDataApi.getPathFor(this.player).resolve("lanterninstorm.json"));
        config.set("spirit_mass", mass);
        config.set("spirit_left_mass", leftMass);
        config.set("SPIRIT_OVERWORLD", SPIRIT_OVERWORLD);
        config.set("SPIRIT_NETHER", SPIRIT_NETHER);
        config.set("SPIRIT_END", SPIRIT_END);
        config.save();
        config.close();
    }

    public void load(){
        Config config = new Config(PlayerDataApi.getPathFor(this.player).resolve("lanterninstorm.json"));
        mass = config.getInt("spirit_mass");
        leftMass = config.getInt("spirit_left_mass");
        SPIRIT_OVERWORLD = config.getMap("SPIRIT_OVERWORLD");
        SPIRIT_NETHER = config.getMap("SPIRIT_NETHER");
        SPIRIT_END = config.getMap("SPIRIT_END");
        config.close();
    }
}