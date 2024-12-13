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

public class Spirit {

    public static final int SPIRIT_RADIUS = 10;

    private int mass;
    private int leftMass;
    private final ServerPlayerEntity player;
    private Map<String, SpiritPos> SPIRIT_OVERWORLD = new HashMap<>();
    private Map<String, SpiritPos> SPIRIT_NETHER = new HashMap<>();
    private Map<String, SpiritPos> SPIRIT_END = new HashMap<>();
    private final Map<RegistryKey<World>, Map<String, SpiritPos>> SPIRIT = new HashMap<>();

    public Spirit(ServerPlayerEntity player, boolean firstTime) {
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
        Map<String, SpiritPos> spiritPos = SPIRIT.get(player.getServerWorld().getRegistryKey());
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
        Map<String, SpiritPos> spiritPosMap = SPIRIT.get(lantern.getEntityWorld().getRegistryKey());
        SpiritPos pos_prev = spiritPosMap.get(lantern.getLSid());
        if (pos_prev == null) {
            pos_prev = SpiritPos.of(lantern.getPos(), 0, lantern.getEntityWorld().getRegistryKey());
        }
        if (i - pos_prev.getSpiritMass() > leftMass) {
            // 剩余的不足以填充这么多的灵魂进入灯笼
            return false;
        }
        leftMass -= i - pos_prev.getSpiritMass();
        spiritPosMap.put(lantern.getLSid(), SpiritPos.of(lantern.getPos(), i, lantern.getEntityWorld().getRegistryKey()));
        return true;
    }

    // 灵魂灯笼死亡
    public boolean removeSpiritFromLantern(SpiritLanternEntity lantern) {
        if (lantern.getEntityWorld().isClient) {
            return false;
        }
        Map<String, SpiritPos> spiritPosMap = SPIRIT.get(lantern.getEntityWorld().getRegistryKey());
        SpiritPos pos_prev = spiritPosMap.get(lantern.getLSid());
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