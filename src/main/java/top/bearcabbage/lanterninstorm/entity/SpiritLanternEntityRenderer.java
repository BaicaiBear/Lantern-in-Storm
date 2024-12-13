package top.bearcabbage.lanterninstorm.entity;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

/*
 * 一个用来提供模型、阴影大小和纹理的渲染器
 */
public class SpiritLanternEntityRenderer extends MobEntityRenderer<SpiritLanternEntity, SpiritLanternEntityModel> {


    public SpiritLanternEntityRenderer(EntityRendererFactory.Context context, SpiritLanternEntityModel entityModel, float f) {
        super(context, entityModel, f);
    }

    @Override
    public Identifier getTexture(SpiritLanternEntity entity) {
        return Identifier.of("lanterninstorm", "textures/entity/spirit_lantern/spirit_lantern.png");
    }
}