package top.bearcabbage.lanterninstorm.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

/*
    玩家一个人用的私有灯笼
*/
public class PrivateLantern extends SpiritLanternEntity {
    public PrivateLantern(EntityType<? extends SpiritLanternEntity> entityType, World world) {
        super(entityType, world);
    }
}