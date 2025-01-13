package top.bearcabbage.lanterninstorm.lantern;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.model.*;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityAttachmentType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import static top.bearcabbage.lanterninstorm.LanternInStorm.MOD_ID;

@Environment(EnvType.CLIENT)
public class BeginningLanternRenderer extends EntityRenderer<BeginningLanternEntity> {
    protected static final Identifier TEXTURE = Identifier.of(MOD_ID, "textures/entity/lantern_ring.png");
    protected static final RenderLayer END_CRYSTAL;
    protected static final float SINE_45_DEGREES;
    protected final ModelPart light_ring;
    protected final ModelPart dark_ring;
    protected final ModelPart bottom;


    public BeginningLanternRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.shadowRadius = 0.5F;
        ModelPart modelPart = context.getPart(EntityModelLayers.END_CRYSTAL);
        this.dark_ring = modelPart.getChild("glass");
        this.light_ring = modelPart.getChild(EntityModelPartNames.CUBE);
        this.bottom = modelPart.getChild("base");
    }

    protected int getBlockLight(BeginningLanternEntity xiaoxiongbaicaidahuaidan, BlockPos wuwuwu) {
        return 15;
    }

    public void render(BeginningLanternEntity Entity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        float h = 0;
        float j = ((float)Entity.Age + g) * 3.0F;
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(END_CRYSTAL);
        matrixStack.push();
        matrixStack.translate(0.0F,  1.0F + h, 0.0F);
        matrixStack.scale(3.0F, 3.0F, 3.0F);
        int k = OverlayTexture.DEFAULT_UV;
        float l = 0.875F;
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(j));
        matrixStack.multiply((new Quaternionf()).setAngleAxis(((float)Math.PI / 3F), SINE_45_DEGREES, 0.0F, SINE_45_DEGREES));
        for(int r=0;r<6 ;++r) {
            if((r & 1) == 0) {
                this.dark_ring.render(matrixStack, vertexConsumer, i, k);
            } else{
                this.light_ring.render(matrixStack, vertexConsumer, i, k);
            }
            matrixStack.scale(l, l, l);
            matrixStack.multiply((new Quaternionf()).setAngleAxis(((float) Math.PI / 3F), SINE_45_DEGREES, 0.0F, SINE_45_DEGREES));
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(j));
            j /= l;
        }
        super.render(Entity, f, g, matrixStack, vertexConsumerProvider, i);
        matrixStack.pop();
        this.renderLabelIfPresent(Entity, Entity.getDisplayName(), matrixStack, vertexConsumerProvider, this.getLight(Entity,20), 20);
    }


    protected void renderLabelIfPresent(BeginningLanternEntity entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float tickDelta) {
        double d = this.dispatcher.getSquaredDistanceToCamera(entity);
        if (!(d > 4096.0)) {
            matrices.push();
            matrices.translate(0, 2.0, 0);
            matrices.multiply(this.dispatcher.getRotation());
            matrices.scale(0.025F, -0.025F, 0.025F);
            Matrix4f matrix4f = matrices.peek().getPositionMatrix();
            float f = MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25F);
            int j = (int)(f * 255.0F) << 24;
            TextRenderer textRenderer = this.getTextRenderer();
            float g = (float)(-textRenderer.getWidth(text) / 2);
            textRenderer.draw(text, g, 0, 553648127, false, matrix4f, vertexConsumers, TextRenderer.TextLayerType.SEE_THROUGH, j, light);
            textRenderer.draw(text, g, 0, -1, false, matrix4f, vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, light);
            matrices.pop();

        }
    }


    public Identifier getTexture(BeginningLanternEntity Entity) {
        return TEXTURE;
    }

    static {
        END_CRYSTAL = RenderLayer.getEntityCutoutNoCull(TEXTURE);
        SINE_45_DEGREES = (float)Math.sin((Math.PI / 4D));
    }
}
