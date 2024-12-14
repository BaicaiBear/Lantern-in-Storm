package top.bearcabbage.lanterninstorm.entity.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import top.bearcabbage.lanterninstorm.entity.SpiritLanternEntity;

/*
    公共灯笼
*/
public class PublicLantern extends SpiritLanternEntity {

    public PublicLantern(EntityType<? extends SpiritLanternEntity> entityType, World world) {
        super(entityType, world);
    }
}
