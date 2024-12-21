package top.bearcabbage.lanterninstorm;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.bearcabbage.lanterninstorm.network.*;
import top.bearcabbage.lanterninstorm.renderer.PrivateLanternRenderer;
import top.bearcabbage.lanterninstorm.renderer.PublicLanternRenderer;
import top.bearcabbage.lanterninstorm.player.PlayerEventRegistrator;

import static top.bearcabbage.lanterninstorm.entity.SpiritLanternEntity.PRIVATE_LANTERN;
import static top.bearcabbage.lanterninstorm.entity.SpiritLanternEntity.PUBLIC_LANTERN;

public class LanternInStormClient implements ClientModInitializer {
    public static final Logger LOGGER = LogManager.getLogger("LS");
    public static final String MOD_NAMESPACE = "lanterninstorm";

    @Override
    public void onInitializeClient() {
        // 注册渲染器
        EntityRendererRegistry.INSTANCE.register(PRIVATE_LANTERN, PrivateLanternRenderer::new);
        EntityRendererRegistry.INSTANCE.register(PUBLIC_LANTERN, PublicLanternRenderer::new);
        // 注册玩家事件
        PlayerEventRegistrator.registerClient();
        ClientPlayNetworking.registerGlobalReceiver(SpiritMassPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                // 处理接收到的数据
                SpiritMassPayloadHandler.onSpiritMassPayload(payload, context);
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(LanternPosPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                // 处理接收到的数据
                LanternPosPayloadHandler.onLanternPosPayload(payload, context);
            });
        });
    }
}
