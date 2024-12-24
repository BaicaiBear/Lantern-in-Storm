package top.bearcabbage.lanterninstorm.renderer;

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
import net.minecraft.entity.passive.GlowSquidEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionf;
import top.bearcabbage.lanterninstorm.LanternInStorm;
import top.bearcabbage.lanterninstorm.LanternInStormClient;
import top.bearcabbage.lanterninstorm.entity.PrivateLanternEntity;
import top.bearcabbage.lanterninstorm.entity.PublicLanternEntity;
import top.bearcabbage.lanterninstorm.entity.SpiritLanternEntity;
import top.bearcabbage.lanterninstorm.utils.ObjModel;

import java.awt.*;

import static net.minecraft.client.render.LightmapTextureManager.pack;
import static top.bearcabbage.lanterninstorm.LanternInStormClient.MOD_NAMESPACE;

@Environment(EnvType.CLIENT)
public class SpiritLanternRenderer extends EntityRenderer<SpiritLanternEntity> {
    protected static final Identifier TEXTURE = Identifier.of(LanternInStormClient.MOD_NAMESPACE, "textures/entity/lantern_ring.png");
    protected static final RenderLayer END_CRYSTAL;
    protected static final float SINE_45_DEGREES;
    protected final ModelPart light_ring;
    protected final ModelPart dark_ring;
    protected final ModelPart bottom;
    public static final Identifier TEXTURE_ON = Identifier.of("lanterninstorm", "textures/entity/lantern_boundary_on.png");
    public static final Identifier TEXTURE_OFF = Identifier.of("lanterninstorm", "textures/entity/lantern_boundary_off.png");

    public static ObjModel model;

    public SpiritLanternRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.shadowRadius = 0.5F;
        ModelPart modelPart = context.getPart(EntityModelLayers.END_CRYSTAL);
        this.dark_ring = modelPart.getChild("glass");
        this.light_ring = modelPart.getChild(EntityModelPartNames.CUBE);
        this.bottom = modelPart.getChild("base");
    }
//
//    public static TexturedModelData getTexturedModelData() {
//        ModelData modelData = new ModelData();
//        ModelPartData modelPartData = modelData.getRoot();
//        modelPartData.addChild("glass", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), ModelTransform.NONE);
//        modelPartData.addChild("cube", ModelPartBuilder.create().uv(32, 0).cuboid(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), ModelTransform.NONE);
//        modelPartData.addChild("base", ModelPartBuilder.create().uv(0, 16).cuboid(-6.0F, 0.0F, -6.0F, 12.0F, 4.0F, 12.0F), ModelTransform.NONE);
//        return TexturedModelData.of(modelData, 64, 32);
//    }
    protected int getBlockLight(SpiritLanternEntity glowSquidEntity, BlockPos blockPos) {
        return 15;
    }

    public void render(SpiritLanternEntity Entity, float Size, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        float h = getYOffset(Entity, g);
        float j = ((float)Entity.Age + g) * 3.0F;

        if (Entity instanceof PrivateLanternEntity || ((PublicLanternEntity) Entity).shouldRenderBoundary()) {
            if (model == null) {
                model = new ObjModel(MOD_NAMESPACE, "entity/lantern_boundary");
            }
            float size = Entity.getRadius();
            matrixStack.push();
            matrixStack.translate(0.0F, 1.0F + h, 0.0F);
            matrixStack.scale(size, size, size);
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(j));
            matrixStack.multiply((new Quaternionf()).setAngleAxis(((float) Math.PI / 3F), SINE_45_DEGREES, 0.0F, SINE_45_DEGREES));
            RenderLayer.MultiPhaseParameters compositeState = RenderLayer.MultiPhaseParameters.builder()
                    .program(RenderPhase.TRANSLUCENT_PROGRAM)
                    .transparency(RenderPhase.TRANSLUCENT_TRANSPARENCY)
                    .texture(new RenderPhase.Texture(TEXTURE_ON, false, false))
                    .cull(RenderPhase.DISABLE_CULLING)
                    .overlay(RenderPhase.ENABLE_OVERLAY_COLOR)
                    .writeMaskState(RenderPhase.COLOR_MASK)
                    .build(true);
            RenderLayer renderLayer = RenderLayer.of("Boundary", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, VertexFormat.DrawMode.TRIANGLES, 1536, true, true, compositeState);
            VertexConsumer consumer = vertexConsumerProvider.getBuffer(renderLayer);
            model.render(matrixStack, consumer, i, pack(0, 10));
            matrixStack.pop();
        }


        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(END_CRYSTAL);
        matrixStack.push();
        matrixStack.translate(0.0F,  1.0F + h, 0.0F);
        matrixStack.scale(3.0F, 3.0F, 3.0F);
        matrixStack.scale(Size, Size, Size);
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
