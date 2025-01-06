package top.bearcabbage.lanterninstorm.player;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import top.bearcabbage.lanterninstorm.entity.SpiritLanternEntity;
import top.bearcabbage.lanterninstorm.interfaces.PlayerAccessor;

public abstract class PlayerHandler {

    // 处理玩家每个tick的事件
    public static void onTick(ServerPlayerEntity player) {
        if (player instanceof PlayerAccessor lsPlayer  && lsPlayer.getLS().onTick()){
            // 疲惫的状态：扛灵魂灯笼
            if (!lsPlayer.getLS().getSafety()){
                lsPlayer.getLS().onUnstableTick();
            }
        }
    }
}
