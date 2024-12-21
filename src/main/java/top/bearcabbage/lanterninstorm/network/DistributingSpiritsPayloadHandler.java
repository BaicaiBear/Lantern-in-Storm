package top.bearcabbage.lanterninstorm.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import top.bearcabbage.lanterninstorm.LanternInStormSpiritManager;

import java.util.UUID;


public class DistributingSpiritsPayloadHandler {

    // 收到加减灵魂的包
    public static void onDistributingSpiritsPayload(DistributingSpiritsPayload payload, ServerPlayNetworking.Context context)  {
        ServerPlayerEntity serverPlayer = context.player();
        UUID player = serverPlayer.getUuid();
        UUID lantern = payload.lantern();
        int spirits = payload.spirits();

        int player_left = LanternInStormSpiritManager.get_left(player);
        int lantern_left = LanternInStormSpiritManager.get(player, lantern);
        if(spirits > player_left){
            serverPlayer.sendMessage(Text.of("灵魂不够了～～～"));
            return;
        }
        if(spirits + lantern_left < 0){
            serverPlayer.sendMessage(Text.of("灯笼空了…………"));
            return;
        }
        LanternInStormSpiritManager.increase_left(player, -spirits);
        LanternInStormSpiritManager.increase(player, lantern, spirits);
        serverPlayer.sendMessage(Text.of("成功分配了"+spirits+"个灵魂"));
        serverPlayer.getServer().getPlayerManager().getPlayerList().forEach(LanternInStormSpiritManager::sendAll);
    }

}