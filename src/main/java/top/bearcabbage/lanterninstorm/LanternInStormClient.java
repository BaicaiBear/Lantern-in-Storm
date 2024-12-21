package top.bearcabbage.lanterninstorm;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.S2CPlayChannelEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.registry.Registry;
import net.minecraft.resource.ResourceManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.bearcabbage.lanterninstorm.entity.LanternBoundaryEntity;
import top.bearcabbage.lanterninstorm.network.*;
import top.bearcabbage.lanterninstorm.renderer.LanternBoundaryRenderer;
import top.bearcabbage.lanterninstorm.renderer.PrivateLanternRenderer;
import top.bearcabbage.lanterninstorm.renderer.PublicLanternRenderer;
import top.bearcabbage.lanterninstorm.player.PlayerEventRegistrator;

import static top.bearcabbage.lanterninstorm.entity.SpiritLanternEntity.PRIVATE_LANTERN;
import static top.bearcabbage.lanterninstorm.entity.SpiritLanternEntity.PUBLIC_LANTERN;
import static top.bearcabbage.lanterninstorm.entity.LanternBoundaryEntity.LANTERN_BOUNDARY;

public class LanternInStormClient implements ClientModInitializer {
    public static final Logger LOGGER = LogManager.getLogger("LS");
    public static final String MOD_NAMESPACE = "lanterninstorm";

    @Override
    public void onInitializeClient() {
        // 注册渲染器
        EntityRendererRegistry.INSTANCE.register(PRIVATE_LANTERN, PrivateLanternRenderer::new);
        EntityRendererRegistry.INSTANCE.register(PUBLIC_LANTERN, PublicLanternRenderer::new);
        EntityRendererRegistry.INSTANCE.register(LANTERN_BOUNDARY, LanternBoundaryRenderer::new);
        // 注册玩家事件
        PlayerEventRegistrator.registerClient();
        // 注册网络数据包
        ClientPlayNetworking.registerGlobalReceiver(LanternBoundaryPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                // 处理接收到的数据
                LanternBoundaryPayloadHandler.onLanternBoundaryPayload(payload, context);
            });
        });
    }
}
