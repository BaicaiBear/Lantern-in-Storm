package top.bearcabbage.lanterninstorm.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import top.bearcabbage.lanterninstorm.LanternInStormSpiritManager;
import top.bearcabbage.lanterninstorm.entity.PrivateLanternEntity;

import java.util.UUID;

import static top.bearcabbage.lanterninstorm.entity.SpiritLanternEntity.lantern_list;
import static top.bearcabbage.lanterninstorm.network.NetworkingConstants.NULL_UUID;


public class OwnerQueryPayloadHandler {

    // 服务端收到查询的包
    public static void onOwnerQueryPayload_SERVER(OwnerQueryPayload payload, ServerPlayNetworking.Context context) {
        if (!(lantern_list.getOrDefault(payload.lantern(),null) instanceof PrivateLanternEntity) || !payload.owner().equals(NULL_UUID)) return;
        ServerPlayNetworking.send(context.player(), new OwnerQueryPayload(payload.lantern(), ((PrivateLanternEntity) lantern_list.get(payload.lantern())).getOwner()));
    }

    // 客户端收到返回的包
    public static void onOwnerQueryPayload_CLIENT(OwnerQueryPayload payload, ClientPlayNetworking.Context context) {
        if (!(lantern_list.getOrDefault(payload.lantern(),null) instanceof PrivateLanternEntity) || payload.owner().equals(NULL_UUID)) return;
        ((PrivateLanternEntity) lantern_list.get(payload.lantern())).setOwner(payload.owner());
    }

}