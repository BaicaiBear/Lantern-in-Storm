/*
    MIT License
    BaicaiBear/Lantern-in-Storm

    * Acknowledgement:
    * This class is inspired and partially copied from the following sources:
    * Package: dev.galacticraft.mod.client.model from Team Galacticraft (https://github.com/TeamGalacticraft/Galacticraft/)
    * Package: dev.onyxstudios.foml from OnyxStudios (https://github.com/OnyxStudios/FOML)
    * These 2 project are both licensed under the MIT License.
 */

package top.bearcabbage.lanterninstorm.utils;

import de.javagl.obj.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import top.bearcabbage.lanterninstorm.LanternInStormClient;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import static top.bearcabbage.lanterninstorm.LanternInStormClient.LOGGER;

/**
 * A Model rendered via a VBO.
 */
public class ObjModel {
    private final Obj obj;

    public ObjModel(String MODID, String path) {
        this.obj = resolveObj(Identifier.of(MODID, path));
    }

    public void render(MatrixStack modelStack, VertexConsumer consumer, int light, int overlay) {
        this.render(modelStack, consumer, light, overlay, 0xFFFFFFFF);
    }

    public void render(MatrixStack modelStack, VertexConsumer consumer, int light, int overlay, int color) {

        for (int index = 0; index < obj.getNumFaces(); index++) {
            ObjFace face = obj.getFace(index);
            renderFace( face, consumer, modelStack, light, overlay, color);
        }
    }

    protected void renderFace(ObjFace face, VertexConsumer consumer, MatrixStack matrices, int light, int overlay, int color) {
       for (int vtx = 0; vtx < face.getNumVertices(); vtx++) {
            int vertexIndex = face.getVertexIndex(vtx);
            FloatTuple pos = obj.getVertex(vertexIndex);
            int texCoordIndex = face.getTexCoordIndex(vtx);
            FloatTuple uv = obj.getTexCoord(texCoordIndex);
            int normalIndex = face.getNormalIndex(vtx);
            FloatTuple normals = obj.getNormal(normalIndex);

            consumer.vertex(matrices.peek().getPositionMatrix(), pos.getX(), pos.getY(), pos.getZ());
            consumer.color(0,100,100,120);
            consumer.texture(uv.getX(), 1 - uv.getY());
            consumer.overlay(overlay);
            consumer.light(light);
            consumer.normal(matrices.peek(), normals.getX(), normals.getY(), normals.getZ());
        }

    }

    public static Obj resolveObj(Identifier id) {
        LOGGER.info("Loading OBJ Model: " + id.toString());
        ResourceManager resourceManager = MinecraftClient.getInstance().getResourceManager();
        try {
            Reader reader = new InputStreamReader(resourceManager.getResource(Identifier.of(id.getNamespace(), "models/" + id.getPath() + ".obj")).get().getInputStream());
            Obj obj = ObjUtils.convertToRenderable(ObjReader.read(reader));
            return obj;
        } catch (IOException e) {
            LanternInStormClient.LOGGER.error("Unable to load OBJ Model, Source: " + id, e);
        }
        return null;
    }
}
