package top.bearcabbage.lanterninstorm;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import top.bearcabbage.lanterninstorm.lantern.BeginningLanternEntity;
import top.bearcabbage.lanterninstorm.player.PlayerAccessor;

public class LanternInStormAPI {
    public static boolean setRTPSpawnWhenSpawnpointCommand = true;

    public static void overrideRTPSpawnSetting() {
        setRTPSpawnWhenSpawnpointCommand = false;
    }

    public static void setRTPSpawn(ServerPlayerEntity player, BlockPos pos) {
        if(player instanceof PlayerAccessor playerAccessor){
            playerAccessor.getLS().setInvincibleTick(80);
            playerAccessor.getLS().setRtpSpawn(pos);
            BeginningLanternEntity.create(player.getServerWorld(), player);
        }
    }
}
