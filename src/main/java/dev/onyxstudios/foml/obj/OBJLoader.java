package dev.onyxstudios.foml.obj;

import de.javagl.obj.Obj;
import de.javagl.obj.ObjReader;
import de.javagl.obj.ObjUtils;
import dev.onyxstudios.foml.obj.baked.OBJUnbakedModel;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelResolver;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import top.bearcabbage.lanterninstorm.LanternInStormClient;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class OBJLoader implements ModelLoadingPlugin, ModelResolver {
    public static final OBJLoader INSTANCE = new OBJLoader();


    private OBJLoader() {}


    public OBJUnbakedModel loadModel(Reader reader, String modid, ResourceManager manager, ModelTransformation transform) {
        OBJUnbakedModel model;

        try {
            Obj obj = ObjUtils.convertToRenderable(ObjReader.read(reader));
            model = new OBJUnbakedModel(ObjUtils.triangulate(obj), loadMTL(manager, modid, obj.getMtlFileNames()), transform);
        } catch (IOException e) {
            LanternInStormClient.LOGGER.error("Could not read obj model!", e);
            return null;
        }

        return model;
    }

    public Map<String, FOMLMaterial> loadMTL(ResourceManager manager, String modid, List<String> mtlNames) throws IOException {
        Map<String, FOMLMaterial> mtls = new LinkedHashMap<>();

        for (String name : mtlNames) {
            Identifier resourceId = Identifier.of(modid, "models/" + name);
            // Use 1.0.0 MTL path as a fallback
            if (manager.getResource(resourceId).isEmpty()) {
                resourceId = Identifier.of(modid, "models/block/" + name);
            }

            // Continue with normal resource loading code
            if(manager.getResource(resourceId).isPresent()) {
                Resource resource = manager.getResource(resourceId).get();

                MtlReader.read(resource.getInputStream()).forEach(mtl -> {
                    mtls.put(mtl.getName(), mtl);
                });
            } else {
                LanternInStormClient.LOGGER.warn("Warning, a model specifies an MTL File but it could not be found! Source: " + modid + ":" + name);
            }
        }

        return mtls;
    }

    @Override
    public void onInitializeModelLoader(ModelLoadingPlugin.Context context) {
        context.resolveModel().register(this);
    }

    @Override
    public @Nullable UnbakedModel resolveModel(ModelResolver.Context context) {
        if(Objects.equals(context.id(), Identifier.of(context.id().getNamespace(), context.id().getPath()))) {
            ResourceManager resourceManager = MinecraftClient.getInstance().getResourceManager();
            try (Reader reader = new InputStreamReader(resourceManager.getResource(Identifier.of(context.id().getNamespace(), "models/" + context.id().getPath())).get().getInputStream())) {
                return loadModel(reader, context.id().getNamespace(), resourceManager, null);
            } catch (IOException e) {
                LanternInStormClient.LOGGER.error("Unable to load OBJ Model, Source: " + context.id().toString(), e);
            }
        }
        return null;
    }
}
