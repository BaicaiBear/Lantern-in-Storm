package top.bearcabbage.lanterninstorm.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

/*
    公共灯笼
*/
public class PublicLanternEntity extends SpiritLanternEntity {
    public PublicLanternEntity(EntityType<? extends SpiritLanternEntity> entityType, World world) {
        super(entityType, world);
    }
}
