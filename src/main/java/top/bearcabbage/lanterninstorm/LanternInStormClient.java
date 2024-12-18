package top.bearcabbage.lanterninstorm;

import dev.onyxstudios.foml.obj.OBJLoader;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.networking.v1.S2CPlayChannelEvents;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.minecraft.registry.Registry;
import net.minecraft.resource.ResourceManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.bearcabbage.lanterninstorm.entity.LanternBoundaryEntity;
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
        // 注册模型加载器
        ModelLoadingPlugin.register(OBJLoader.INSTANCE);
    }
}
