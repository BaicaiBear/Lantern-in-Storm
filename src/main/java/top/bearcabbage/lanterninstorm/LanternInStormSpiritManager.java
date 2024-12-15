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
    /*
    [Option 2] 如果玩家加减灵魂时卡顿，就启用双向Map
    private static final Map<GlobalPos, Long> lanternLSIDFromPos = new java.util.HashMap<>();
    */
    public static boolean playerIsSafe (@NotNull ServerPlayerEntity player) {
        Map<Long, Integer> validLantern = playerData.get(player.getUuidAsString());
        if (validLantern == null) return false;
        for (Map.Entry<Long, Integer> entry : validLantern.entrySet()) {
            GlobalPos lanternPos = lanternPosFromLSID.get(entry.getKey());
            if (lanternPos != null &&
                    lanternPos.dimension() == player.getServerWorld().getRegistryKey() &&
                    HorizontalDistance(lanternPos.pos(), player.getBlockPos()) < entry.getValue() * DISTANCE_PER_SPIRIT) {
                return true;
            }
        }
        return false;
    }

    public static boolean playerDistributeSpirits (ServerPlayerEntity player, GlobalPos pos, int spirits) {
        if (((PlayerAccessor) player).getLS().getSpiritsBanlance() < spirits) return false;
        long LSID = posToLSID(pos);
        if (LSID == -1) return false;
        Map<Long, Integer> validLantern = playerData.computeIfAbsent(player.getUuidAsString(), k -> new HashMap<>());
        // 如果要加灯笼内的灵魂上限，可以在这里加条件判断
        validLantern.put(LSID, spirits);
        return true; // 返回true后由玩家自己调用方法，减少spiritsBanlance
    }

    public static long lanternGenerateLSID () { return System.currentTimeMillis(); }

    public static void lanternPosUpdate (long LSID, GlobalPos pos) {
        lanternPosFromLSID.put(LSID, pos);
        /*
        [Option 2]
        if (lanternPosFromLSID.get(LSID) != null) {
            lanternLSIDFromPos.remove(lanternPosFromLSID.get(LSID));
        }
        lanternLSIDFromPos.put(pos, LSID);
        lanternPosFromLSID.put(LSID, pos);
        */
    }

    private static long posToLSID(GlobalPos pos){
        for (Map.Entry<Long, GlobalPos> entry : lanternPosFromLSID.entrySet()) {
            if (entry.getValue().equals(pos)) {
                return entry.getKey();
            }
        }
        return -1;

        /*
        [Option 2]
        return lanternLSIDFromPos.get(pos);
        */
    }

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
        Map<Long, String> lanternPosStringMap = new HashMap<>();
        try {
            playerData.putAll(data.get("playerData", Map.class));
            lanternPosStringMap = data.get("lanternPosFromLSID",Map.class);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        for (Map.Entry<Long, String> entry : lanternPosStringMap.entrySet()) {
            String[] parts = entry.getValue().split(" ");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid string format");
            }
            Identifier dimension = Identifier.ofVanilla(parts[0]);

            parts[1] = parts[1].replace("(", "").replace(")", "");
            String[] parts2 = parts[1].split(", ");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid string format");
            }
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            int z = Integer.parseInt(parts[2]);
            BlockPos pos = new BlockPos(x,y,z);

            lanternPosFromLSID.put(entry.getKey(), GlobalPos.create(RegistryKey.of(RegistryKeys.WORLD, dimension), pos));
        }
        data.close();
    }
}
