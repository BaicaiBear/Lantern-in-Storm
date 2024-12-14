package top.bearcabbage.lanterninstorm.entity.renderers;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionf;
import top.bearcabbage.lanterninstorm.entity.SpiritLanternEntity;
import top.bearcabbage.lanterninstorm.entity.SpiritLanternEntityRenderer;

public class PrivateLanternRenderer extends SpiritLanternEntityRenderer {
    private final static float SIZE = 1.0f;

    public PrivateLanternRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public void render(SpiritLanternEntity Entity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.scale(SIZE,SIZE,SIZE);
        super.render(Entity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}
