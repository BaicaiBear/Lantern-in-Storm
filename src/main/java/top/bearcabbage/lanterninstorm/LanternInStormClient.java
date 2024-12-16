package top.bearcabbage.lanterninstorm;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import top.bearcabbage.lanterninstorm.entity.renderers.PrivateLanternRenderer;
import top.bearcabbage.lanterninstorm.entity.renderers.PublicLanternRenderer;
import top.bearcabbage.lanterninstorm.player.PlayerEventRegistrator;

import static top.bearcabbage.lanterninstorm.entity.SpiritLanternEntity.PRIVATE_LANTERN;
import static top.bearcabbage.lanterninstorm.entity.SpiritLanternEntity.PUBLIC_LANTERN;

public class LanternInStormClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // 注册渲染器
        EntityRendererRegistry.INSTANCE.register(PRIVATE_LANTERN, PrivateLanternRenderer::new);
        EntityRendererRegistry.INSTANCE.register(PUBLIC_LANTERN, PublicLanternRenderer::new);

        PlayerEventRegistrator.registerClient();
    }
}
