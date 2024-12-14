package top.bearcabbage.lanterninstorm.entity.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import top.bearcabbage.lanterninstorm.entity.SpiritLanternEntity;

/*
    玩家一个人用的私有灯笼
*/
public class PrivateLantern extends SpiritLanternEntity {

    public PrivateLantern(EntityType<? extends SpiritLanternEntity> entityType, World world) {
        super(entityType, world);
    }

}
