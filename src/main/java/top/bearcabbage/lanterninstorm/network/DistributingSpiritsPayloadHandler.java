package top.bearcabbage.lanterninstorm.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import top.bearcabbage.lanterninstorm.LanternInStormSpiritManager;
import top.bearcabbage.lanterninstorm.entity.SpiritLanternEntity;


public class DistributingSpiritsPayloadHandler {

    // 收到加减灵魂的包
    public static void onDistributingSpiritsPayload(DistributingSpiritsPayload payload, ServerPlayNetworking.Context context)  {
            LanternInStormSpiritManager.set(context.player().getUuid(), payload.lantern(), payload.spirits());
    }

}