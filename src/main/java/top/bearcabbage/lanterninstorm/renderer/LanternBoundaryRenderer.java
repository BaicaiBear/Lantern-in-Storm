package top.bearcabbage.lanterninstorm.renderer;

import dev.onyxstudios.foml.obj.OBJLoader;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import top.bearcabbage.lanterninstorm.entity.LanternBoundaryEntity;
import top.bearcabbage.lanterninstorm.entity.SpiritLanternEntity;

import java.io.IOException;
import java.io.InputStreamReader;

import static top.bearcabbage.lanterninstorm.LanternInStormClient.MOD_NAMESPACE;

@Environment(EnvType.CLIENT)
public class LanternBoundaryRenderer extends EntityRenderer<LanternBoundaryEntity> {
    public static final Identifier TEXTURE_ON = Identifier.of("lanterninstorm", "textures/entity/lantern_boundary_on.png");
    public static final Identifier TEXTURE_OFF = Identifier.of("lanterninstorm", "textures/entity/lantern_boundary_off.png");

    public static UnbakedModel model;

    public LanternBoundaryRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    public void render(LanternBoundaryEntity Entity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        if (model == null) {
            try {
                model = OBJLoader.INSTANCE.loadModel(
                        new InputStreamReader(
                                MinecraftClient.getInstance().getResourceManager().
                                getResource(Identifier.of(MOD_NAMESPACE, "models/entity/boundary")).get().getInputStream()), MOD_NAMESPACE, MinecraftClient.getInstance().getResourceManager(), null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            assert model != null;
        }

    }

    @Override
    public Identifier getTexture(LanternBoundaryEntity entity) {
        return null;
    }
}
