package top.bearcabbage.lanterninstorm.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.util.math.MatrixStack;
//import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionf;
import top.bearcabbage.lanterninstorm.LanternInStorm;

@Environment(EnvType.CLIENT)
public class SpiritLanternEntityRenderer extends EntityRenderer<SpiritLanternEntity> {
    protected static final Identifier TEXTURE = Identifier.of(LanternInStorm.MOD_ID, "textures/entity/lantern_ring.png");
    protected static final RenderLayer END_CRYSTAL;
    protected static final float SINE_45_DEGREES;
    protected final ModelPart light_ring;
    protected final ModelPart dark_ring;
    protected final ModelPart bottom;

    public SpiritLanternEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.shadowRadius = 0.5F;
        ModelPart modelPart = context.getPart(EntityModelLayers.END_CRYSTAL);
        this.dark_ring = modelPart.getChild("glass");
        this.light_ring = modelPart.getChild(EntityModelPartNames.CUBE);
        this.bottom = modelPart.getChild("base");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("glass", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), ModelTransform.NONE);
        modelPartData.addChild("cube", ModelPartBuilder.create().uv(32, 0).cuboid(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), ModelTransform.NONE);
        modelPartData.addChild("base", ModelPartBuilder.create().uv(0, 16).cuboid(-6.0F, 0.0F, -6.0F, 12.0F, 4.0F, 12.0F), ModelTransform.NONE);
        return TexturedModelData.of(modelData, 64, 32);
    }

    public void render(SpiritLanternEntity Entity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        float h = getYOffset(Entity, g);
        float j = ((float)Entity.Age + g) * 3.0F;
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(END_CRYSTAL);
        matrixStack.push();
        matrixStack.scale(3.0F, 3.0F, 3.0F);
        matrixStack.translate(0.0F, -0.5F, 0.0F);
        int k = OverlayTexture.DEFAULT_UV;
        float l = 0.875F;

        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(j));
        matrixStack.translate(0.0F, 1.5F + h / 2.0F, 0.0F);
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
//        this.frame.render(matrixStack, vertexConsumer, i, k);
//        matrixStack.scale(l, l, l);
//        matrixStack.multiply((new Quaternionf()).setAngleAxis(((float)Math.PI / 3F), SINE_45_DEGREES, 0.0F, SINE_45_DEGREES));
//        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(j));
//        this.frame.render(matrixStack, vertexConsumer, i, k);
//        matrixStack.scale(l, l, l);
//        matrixStack.multiply((new Quaternionf()).setAngleAxis(((float)Math.PI / 3F), SINE_45_DEGREES, 0.0F, SINE_45_DEGREES));
//        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(j));
//        this.frame.render(matrixStack, vertexConsumer, i, k);
//        matrixStack.scale(l, l, l);
//        matrixStack.multiply((new Quaternionf()).setAngleAxis(((float)Math.PI / 3F), SINE_45_DEGREES, 0.0F, SINE_45_DEGREES));
//        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(j));

        matrixStack.pop();
        matrixStack.pop();
//        Vec3d Pos = Entity.getWorld().getPlayers().getFirst().getPos();
//        if (Pos != null) {
//            float m = (float)Pos.getX() + 0.5F;
//            float n = (float)Pos.getY() + 0.5F;
//            float o = (float)Pos.getZ() + 0.5F;
//            float p = (float)((double)m - Entity.getX());
//            float q = (float)((double)n - Entity.getY()+1);
//            float r = (float)((double)o - Entity.getZ());
//            matrixStack.translate(p, q, r);
//            EnderDragonEntityRenderer.renderCrystalBeam(-p, -q + h, -r, g, Entity.Age, matrixStack, vertexConsumerProvider, i);
//        }

        super.render(Entity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    public static float getYOffset(SpiritLanternEntity crystal, float tickDelta) {/*
        float f = (float)crystal.Age + tickDelta;
        float g = MathHelper.sin(f * 0.2F) / 2.0F + 0.5F;
        g = (g * g + g) * 0.4F;*///末影水晶的Y摆动
        return 0;
    }

    public Identifier getTexture(SpiritLanternEntity Entity) {
        return TEXTURE;
    }

    static {
        END_CRYSTAL = RenderLayer.getEntityCutoutNoCull(TEXTURE);
        SINE_45_DEGREES = (float)Math.sin((Math.PI / 4D));
    }
}
