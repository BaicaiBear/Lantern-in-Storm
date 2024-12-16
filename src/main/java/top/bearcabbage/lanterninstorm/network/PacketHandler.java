package top.bearcabbage.lanterninstorm.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import top.bearcabbage.lanterninstorm.entity.SpiritLanternEntity;


public class PacketHandler {
    public static final Identifier SCROLL_PACKET_ID = Identifier.of("LanternInStorm", "scroll_event");

    public static void sendScrollEventToServer(PlayerEntity sender,boolean isScrollingUp) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBoolean(isScrollingUp);
        ClientPlayNetworking.send((ServerPlayerEntity)sender,SCROLL_PACKET_ID,ScrollEventPayload);
    }

    public static void handleScrollEventOnServer(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        boolean isScrollingUp = buf.readBoolean();

        //实现查找看向实体的逻辑
         SpiritLanternEntity lookedAtLantern = findLookedAtLantern(player);
         if (lookedAtLantern != null) {
             if (isScrollingUp) {
                 // 执行向上滚动的逻辑
             } else {
                // 执行向下滚动的逻辑
             }
         }
    }

    private static SpiritLanternEntity findLookedAtLantern(ServerPlayerEntity player) {
        //先不写 返回目视lantern的数据
        // 如果没有找到符合条件的实体，则返回null
        return null;
    }
}