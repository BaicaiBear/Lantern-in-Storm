package top.bearcabbage.lanterninstorm;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import top.bearcabbage.lanterninstorm.entity.SpiritLanternEntity;
import top.bearcabbage.lanterninstorm.interfaces.PlayerAccessor;
import top.bearcabbage.lanterninstorm.utils.Config;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import static top.bearcabbage.lanterninstorm.utils.Math.HorizontalDistance;

/*
    用于管理Spirit
    取代了原先的Spirit,PlayerSpirit,SpiritData,SpiritLanternEntityManager
 */
public abstract class LanternInStormSpiritManager {

    private static final Map<String, Map<Long, Integer>> playerData = new HashMap<>();
    private static final Map<Long, GlobalPos> lanternPosFromLSID = new HashMap<>();
    private static final int DISTANCE_PER_SPIRIT = 10; // 每Spirit可稳定的半径

    public static boolean playerIsSafe (@NotNull ServerPlayerEntity player) {
        Map<Long, Integer> validLantern = playerData.get(player.getUuidAsString());
        if (validLantern == null) return false;
        for (Map.Entry<Long, Integer> entry : validLantern.entrySet()) {
            GlobalPos lanternPos = lanternPosFromLSID.get(entry.getKey());
            if (lanternPos == null) {
                // 可以在这里加入灯笼不存在时，玩家Spirit的处理
                continue;
            }
            if (lanternPos.dimension() == player.getServerWorld().getRegistryKey() &&
                    HorizontalDistance(lanternPos.pos(), player.getBlockPos()) < entry.getValue() * DISTANCE_PER_SPIRIT) {
                return true;
            }
        }
        return false;
    }

    public static boolean playerDistributeSpirits (ServerPlayerEntity player, SpiritLanternEntity lantern, int spirits) {
        if (((PlayerAccessor) player).getLS().getSpiritsBanlance() < spirits) return false;
        long LSID = lantern.getLSid();
        if (LSID == -1) return false;
        Map<Long, Integer> validLantern = playerData.computeIfAbsent(player.getUuidAsString(), k -> new HashMap<>());
        Integer v = validLantern.get(LSID);
        if (v == null) v = 0;
        if (v+spirits < 0) return false;
        // 如果要加灯笼内的灵魂上限，可以在这里加条件判断
        validLantern.put(LSID, v+spirits);
        return true; // 返回true后由玩家自己调用方法，减少spiritsBanlance
    }

    public static long lanternGenerateLSID () { return System.currentTimeMillis(); }

    public static void lanternPosUpdate (long LSID, GlobalPos pos) { lanternPosFromLSID.put(LSID, pos); }

    public static void lanternRemove (long LSID) { lanternPosFromLSID.remove(LSID); } // 破坏后玩家的灵魂不会归还玩家，上限会减少

    public static void save(Path path) {
        Config data = new Config(path);
        data.set("playerData", playerData);
        // 使用GlobalPos中的toString方法将lanternPosFromLSID中的ClobalPos值转换为字符串，然后保存
        Map<Long, String> lanternPosStringMap = new HashMap<>();
        for (Map.Entry<Long, GlobalPos> entry : lanternPosFromLSID.entrySet()) {
            lanternPosStringMap.put(entry.getKey(), entry.getValue().toString());
        }
        data.set("lanternPosFromLSID", lanternPosStringMap);
        data.save();
        data.close();
    }
    
    public static void load(Path path) {
        Config data = new Config(path);
        Map<String, String> lanternPosStringMap = new HashMap<>();
        try {
            Map<String, Object> playerdata1 = data.get("playerData", Map.class);
            // 分步将playerdata转换为Map<String, Map<Long, Integer>>的正确格式，然后写入playerData
            for (Map.Entry<String, Object> entry : playerdata1.entrySet()) {
                Map<Long, Integer> playerdata2 = new HashMap<>();
                for(Map.Entry<String, String> entry2 : ((Map<String, String>)entry.getValue()).entrySet()) {
                    playerdata2.put(Long.parseLong(entry2.getKey()), Integer.parseInt(entry2.getValue()));
                }
                playerData.put(entry.getKey(), playerdata2);
            }
            lanternPosStringMap = data.get("lanternPosFromLSID",Map.class);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        for (Map.Entry<String, String> entry : lanternPosStringMap.entrySet()) {
            String[] parts = entry.getValue().split("] BlockPos\\{");
            parts[0] = parts[0].replace("ResourceKey[minecraft:dimension / ","").replace("minecraft:","");
            parts[1] = parts[1].replace("}", "").replace("x=","").replace("y=","").replace("z=","");

            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid string format");
            }
            Identifier dimension = Identifier.ofVanilla(parts[0]);
            String[] parts2 = parts[1].split(", ");
            int x = Integer.parseInt(parts2[0]);
            int y = Integer.parseInt(parts2[1]);
            int z = Integer.parseInt(parts2[2]);
            BlockPos pos = new BlockPos(x,y,z);
            lanternPosFromLSID.put(Long.parseLong(entry.getKey()), GlobalPos.create(RegistryKey.of(RegistryKeys.WORLD, dimension), pos));
        }
        data.close();
    }
}
