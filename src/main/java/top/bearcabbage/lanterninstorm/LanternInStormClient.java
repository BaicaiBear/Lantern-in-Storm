package top.bearcabbage.lanterninstorm;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import top.bearcabbage.lanterninstorm.entity.SpiritLanternEntityRenderer;

import static top.bearcabbage.lanterninstorm.entity.SpiritLanternEntity.SPIRIT_LANTERN;

public class LanternInStormClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // 注册渲染器
        EntityRendererRegistry.INSTANCE.register(SPIRIT_LANTERN, SpiritLanternEntityRenderer::new);
    }
}
