package top.bearcabbage.lanterninstorm.entity.renderers;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import top.bearcabbage.lanterninstorm.entity.SpiritLanternEntity;
import top.bearcabbage.lanterninstorm.entity.SpiritLanternEntityRenderer;

public class PublicLanternRenderer extends SpiritLanternEntityRenderer {
    private final static float SIZE = 5.0F;

    public PublicLanternRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public void render(SpiritLanternEntity Entity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.scale(SIZE,SIZE,SIZE);
        super.render(Entity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}
