package top.bearcabbage.lanterninstorm.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import top.bearcabbage.lanterninstorm.LanternInStormSpiritManager;

import java.util.UUID;


public class LanternPosPayloadHandler {


    // 收到Manager_Pos的包
    public static void onLanternPosPayload(LanternPosPayload payload, ClientPlayNetworking.Context context)  {
        LanternInStormSpiritManager.loadPos(payload.lantern_pos());
    }

}