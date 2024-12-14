package top.bearcabbage.lanterninstorm.player;

import net.minecraft.server.network.ServerPlayerEntity;
import top.bearcabbage.lanterninstorm.entity.SpiritLanternEntity;
import top.bearcabbage.lanterninstorm.interfaces.PlayerAccessor;

public abstract class PlayerHandler {

    // 处理玩家每个tick的事件
    public static void onTick(ServerPlayerEntity player) {
        if (player instanceof PlayerAccessor lsPlayer  && lsPlayer.getLS().onTick()){
            // 疲惫的状态：扛灵魂灯笼
            if (player.getPassengerList().stream().anyMatch(entity -> entity instanceof SpiritLanternEntity)) {
                //lsPlayer.getLS().onTiredTick();
            }
            // 休息的状态：刚刚放下灵魂灯笼
            else if (lsPlayer.getLS().getTiredTick() > 0){
                lsPlayer.getLS().onRestTick();
            }
            // 不在灵魂灯笼范围内
            else if (!lsPlayer.getLS().getSpirit().isStabilized()){
                lsPlayer.getLS().onUnstableTick();
            }
        }
    }
}
