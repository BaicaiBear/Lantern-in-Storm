package top.bearcabbage.lanterninstorm;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import top.bearcabbage.lanterninstorm.lantern.BeginningLanternEntity;
import top.bearcabbage.lanterninstorm.player.LiSPlayer;

public class LanternInStormAPI {
    public static boolean setRTPSpawnWhenSpawnpointCommand = true;

    public static void overrideRTPSpawnSetting() {
        setRTPSpawnWhenSpawnpointCommand = false;
    }

    public static void setRTPSpawn(ServerPlayerEntity player, BlockPos pos) {
        if(player instanceof LiSPlayer liSPlayer){
            liSPlayer.getLS().setInvincibleTick(80);
            liSPlayer.getLS().setRtpSpawn(pos);
            BeginningLanternEntity.create(player.getServerWorld(), player);
        }
    }

    public static boolean setPlayerSpirit(ServerPlayerEntity player, int spirit) {
        return ((LiSPlayer) player).getLS().setSpirit(spirit);
    }

    public static int getPlayerSpirit(ServerPlayerEntity player) {
        return ((LiSPlayer) player).getLS().getSpirit();
    }

    public static boolean addPlayerSpirit(ServerPlayerEntity player, int spirit) {
        return ((LiSPlayer) player).getLS().addSpirit(spirit);
    }
}
