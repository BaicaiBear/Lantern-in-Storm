package top.bearcabbage.lanterninstorm.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import top.bearcabbage.lanterninstorm.LanternInStormSpiritManager;
import top.bearcabbage.lanterninstorm.entity.SpiritLanternEntity;

import java.util.UUID;


public class LanternBoundaryPayloadHandler {

    // 收到加减灵魂的包
    public static void onLanternBoundaryPayload(LanternBoundaryPayload payload, ClientPlayNetworking.Context context)  {
        SpiritLanternEntity lantern = SpiritLanternEntity.lantern_list.getOrDefault(payload.lantern(), null);
        if (lantern == null) {
            return;
        }
        lantern.setRadius(payload.radius());
    }

}