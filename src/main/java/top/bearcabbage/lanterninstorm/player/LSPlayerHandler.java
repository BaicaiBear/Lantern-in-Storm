package top.bearcabbage.lanterninstorm.player;

import net.minecraft.server.network.ServerPlayerEntity;
import top.bearcabbage.lanterninstorm.interfaces.LSPlayerAccessor;

public abstract class LSPlayerHandler {

    // 处理玩家每个tick的事件
    public static void onTick(ServerPlayerEntity player) {
        if (player instanceof LSPlayerAccessor lsPlayer &&
                lsPlayer.getLS().getLSLevel() != LSLevel.LEVELS.getLast() &&
                lsPlayer.getLS().onTick()){ //排除掉满级玩家
            if (!checkSafety(player)){
                lsPlayer.getLS().onUnsafeTick();//不安全的状态
            } else if (lsPlayer.getLS().getUnsafeTick() > 0){
                lsPlayer.getLS().onSafeTick();//刚刚回到安全区的状态
            }
        }
    }

    //检查安全状态
    private static boolean checkSafety(ServerPlayerEntity player){
        LSPlayerAccessor ceplayer = (LSPlayerAccessor) player;
        return false;
    }

}
