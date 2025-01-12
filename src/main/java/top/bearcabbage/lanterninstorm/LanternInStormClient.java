package top.bearcabbage.lanterninstorm;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.client.particle.EndRodParticle;
import net.minecraft.client.render.RenderLayer;
import top.bearcabbage.lanterninstorm.lantern.BeginningLanternRenderer;
import top.bearcabbage.lanterninstorm.particle.BorderParticle;

import static top.bearcabbage.lanterninstorm.lantern.SpiritLanternBlocks.OAK_WOODEN_LANTERN;
import static top.bearcabbage.lanterninstorm.lantern.SpiritLanternBlocks.WHITE_PAPER_LANTERN;
import static top.bearcabbage.lanterninstorm.lantern.BeginningLanternEntity.*;

public class LanternInStormClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // 注册渲染
        BlockRenderLayerMap.INSTANCE.putBlock(WHITE_PAPER_LANTERN, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(OAK_WOODEN_LANTERN, RenderLayer.getTranslucent());
        EntityRendererRegistry.INSTANCE.register(BEGINNING_LANTERN, BeginningLanternRenderer::new);
        ParticleFactoryRegistry.getInstance().register(BorderParticle.border, EndRodParticle.Factory::new);
    }
}
