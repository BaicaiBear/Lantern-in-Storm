package top.bearcabbage.lanterninstorm.renderer;

import top.bearcabbage.lanterninstorm.utils.ObjModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import top.bearcabbage.lanterninstorm.entity.LanternBoundaryEntity;

import static net.minecraft.client.render.LightmapTextureManager.pack;
import static top.bearcabbage.lanterninstorm.LanternInStormClient.MOD_NAMESPACE;

@Environment(EnvType.CLIENT)
public class LanternBoundaryRenderer extends EntityRenderer<LanternBoundaryEntity> {
    public static final Identifier TEXTURE_ON = Identifier.of("lanterninstorm", "textures/entity/lantern_boundary_on.png");
    public static final Identifier TEXTURE_OFF = Identifier.of("lanterninstorm", "textures/entity/lantern_boundary_off.png");

    public static ObjModel model;

    public LanternBoundaryRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    public void render(LanternBoundaryEntity Entity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        if (model == null) {
            model = new ObjModel(MOD_NAMESPACE, "entity/lantern_boundary");
        }
        float size = Entity.getRadius();
        matrixStack.push();
        matrixStack.translate(0.5F, 1.0F, 0.5F);
        matrixStack.scale(size, size, size);
        RenderLayer.MultiPhaseParameters compositeState = RenderLayer.MultiPhaseParameters.builder()
                .program(RenderPhase.TRANSLUCENT_PROGRAM)
                .transparency(RenderPhase.TRANSLUCENT_TRANSPARENCY)
                .texture(new RenderPhase.Texture(TEXTURE_ON, false, false))
                .cull(RenderPhase.DISABLE_CULLING)
                .overlay(RenderPhase.ENABLE_OVERLAY_COLOR)
                .writeMaskState(RenderPhase.COLOR_MASK)
                .build(true);RenderLayer renderLayer = RenderLayer.of("Boundary",VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, VertexFormat.DrawMode.TRIANGLES, 1536, true, true, compositeState);
        VertexConsumer consumer = vertexConsumerProvider.getBuffer(renderLayer);
        model.render(matrixStack, consumer, i, pack(0, 10));
        matrixStack.pop();
    }

    @Override
    public Identifier getTexture(LanternBoundaryEntity entity) {
        return null;
    }
}
