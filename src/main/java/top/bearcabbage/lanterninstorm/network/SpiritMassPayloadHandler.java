package top.bearcabbage.lanterninstorm.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import top.bearcabbage.lanterninstorm.LanternInStormSpiritManager;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


public class SpiritMassPayloadHandler {

    // 收到Manager_Mass的包
    public static void onSpiritMassPayload(SpiritMassPayload payload, ClientPlayNetworking.Context context)  {
        LanternInStormSpiritManager.loadMass(payload.spirit_mass());
    }

}