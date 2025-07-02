package top.bearcabbage.lanterninstorm;

import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.EndRodParticle;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import top.bearcabbage.lanterninstorm.lantern.BeginningLanternRenderer;
import top.bearcabbage.lanterninstorm.lantern.WarpBeginningLanternRenderer;
import top.bearcabbage.lanterninstorm.lantern.BorderParticle;
import top.bearcabbage.lanterninstorm.lantern.SpiritLanternBlock;

import static top.bearcabbage.lanterninstorm.LanternInStorm.MOD_ID;
import static top.bearcabbage.lanterninstorm.LanternInStormItems.FLASHLIGHT;
import static top.bearcabbage.lanterninstorm.LanternInStormItems.TALISMAN;
import static top.bearcabbage.lanterninstorm.lantern.SpiritLanternBlocks.*;
import static top.bearcabbage.lanterninstorm.lantern.BeginningLanternEntity.*;

public class LanternInStormClient implements ClientModInitializer {

    public static int how_to_defend = 0;
    public static boolean damaged = false;
    public static boolean exhausted = false;
    public static int client_tick = 0;

    @Override
    public void onInitializeClient() {
        // 注册渲染
        BlockRenderLayerMap.INSTANCE.putBlock(CUBIC_GLASS_LANTERN, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(WHITE_PAPER_LANTERN, RenderLayer.getTranslucent());
        // 木质立方灯
        BlockRenderLayerMap.INSTANCE.putBlock(OAK_WOODEN_LANTERN, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(SPRUCE_WOODEN_LANTERN, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(BIRCH_WOODEN_LANTERN, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(JUNGLE_WOODEN_LANTERN, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ACACIA_WOODEN_LANTERN, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(DARK_OAK_WOODEN_LANTERN, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(MANGROVE_WOODEN_LANTERN, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(CHERRY_WOODEN_LANTERN, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(BAMBOO_WOODEN_LANTERN, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(CRIMSON_WOODEN_LANTERN, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(WARPED_WOODEN_LANTERN, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(FIR_WOODEN_LANTERN, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(PINE_WOODEN_LANTERN, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(MAPLE_WOODEN_LANTERN, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(REDWOOD_WOODEN_LANTERN, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(MAHOGANY_WOODEN_LANTERN, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(JACARANDA_WOODEN_LANTERN, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(PALM_WOODEN_LANTERN, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(WILLOW_WOODEN_LANTERN, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(DEAD_WOODEN_LANTERN, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(MAGIC_WOODEN_LANTERN, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(UMBRAN_WOODEN_LANTERN, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(HELLBARK_WOODEN_LANTERN, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(EMPYREAL_WOODEN_LANTERN, RenderLayer.getTranslucent());
        // 神话金属灯
        BlockRenderLayerMap.INSTANCE.putBlock(MIDAS_LANTERN, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(MORKITE_LANTERN, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(STARRITE_LANTERN, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(PROMETHIUM_LANTERN, RenderLayer.getTranslucent());

        // 限量彩灯
        BlockRenderLayerMap.INSTANCE.putBlock(SNOWMAN_LANTERN, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(SNAKE_NEWYEAR_LANTERN, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(KONGMING_LANTERN_2025, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(DEATHBEAR_LANTERN, RenderLayer.getTranslucent());


        ClientTickEvents.END_WORLD_TICK.register(clientWorld -> {
                    if (client_tick++ % 10 == 0 && MinecraftClient.getInstance().player != null && MinecraftClient.getInstance().world != null) {
                        PlayerEntity player = MinecraftClient.getInstance().player;
                        TrinketsApi.getTrinketComponent(player).ifPresent(trinkets -> trinkets.forEach((slot, stack) -> {
                            if (slot.getId().contains("offhand/glove")) {
                                // 手电在饰品位
                                if (stack.isOf(FLASHLIGHT) || player.getMainHandStack().isOf(FLASHLIGHT) || player.getOffHandStack().isOf(FLASHLIGHT)) {
                                    how_to_defend = 1;
                                    damaged = stack.isOf(FLASHLIGHT) && ((double) stack.getDamage()) / ((double) stack.getMaxDamage()) > 0.9
                                            || player.getMainHandStack().isOf(FLASHLIGHT) && ((double) player.getMainHandStack().getDamage()) / ((double) player.getMainHandStack().getMaxDamage()) > 0.9
                                            || player.getOffHandStack().isOf(FLASHLIGHT) && ((double) player.getOffHandStack().getDamage()) / ((double) player.getOffHandStack().getMaxDamage()) > 0.9;
                                } else if (stack.isOf(TALISMAN) || player.getMainHandStack().isOf(TALISMAN) || player.getOffHandStack().isOf(TALISMAN)) {
                                    how_to_defend = 2;
                                    exhausted = stack.isOf(TALISMAN) && stack.getDamage() == stack.getMaxDamage() - 1
                                            || player.getMainHandStack().isOf(TALISMAN) && player.getMainHandStack().getDamage() == player.getMainHandStack().getMaxDamage() - 1
                                            || player.getOffHandStack().isOf(TALISMAN) && player.getOffHandStack().getDamage() == player.getOffHandStack().getMaxDamage() - 1;
                                    if (!exhausted) damaged = stack.isOf(TALISMAN) && ((double) stack.getDamage()) / ((double) stack.getMaxDamage()) > 0.9
                                            || player.getMainHandStack().isOf(TALISMAN) && ((double) player.getMainHandStack().getDamage()) / ((double) player.getMainHandStack().getMaxDamage()) > 0.9
                                            || player.getOffHandStack().isOf(TALISMAN) && ((double) player.getOffHandStack().getDamage()) / ((double) player.getOffHandStack().getMaxDamage()) > 0.9;
                                    else  damaged = false;
                                } else {
                                    how_to_defend = 0;
                                }
                            }
                        }));
                    }
                });

        HudRenderCallback.EVENT.register((context, renderTickCounter) -> {
            if (MinecraftClient.getInstance().options.hudHidden) return;
            int textureWidth = 36; // Width of the texture
            int margin = 10; // Margin from the edge of the window
            int x = context.getScaledWindowWidth() - textureWidth - margin; // Calculate x for the right side
            int y = context.getScaledWindowHeight() - 46; // Keep y the same for the bottom
            if (damaged && client_tick % 20 < 10) return;
            if (how_to_defend == 1) {
                context.drawTexture(Identifier.of(MOD_ID, "textures/item/flashlight.png"), x, y, 0, 0, textureWidth, textureWidth, textureWidth, textureWidth);
            } else if (how_to_defend == 2) {
                if (!exhausted) context.drawTexture(Identifier.of(MOD_ID, "textures/item/talisman.png"), x, y, 0, 0, textureWidth, textureWidth, textureWidth, textureWidth);
                else context.drawTexture(Identifier.of(MOD_ID, "textures/item/talisman_exhausted.png"), x, y, 0, 0, textureWidth, textureWidth, textureWidth, textureWidth);
            }
        });
        EntityRendererRegistry.INSTANCE.register(BEGINNING_LANTERN, BeginningLanternRenderer::new);
        EntityRendererRegistry.INSTANCE.register(WARP_BEGINNING_LANTERN, WarpBeginningLanternRenderer::new);
        ParticleFactoryRegistry.getInstance().register(BorderParticle.border, EndRodParticle.Factory::new);
    }
}
