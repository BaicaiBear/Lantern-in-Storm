package top.bearcabbage.lanterninstorm.entity;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import top.bearcabbage.lanterninstorm.LanternInStormSpiritManager;

import java.util.Map;
import java.util.UUID;

/*
    公共灯笼
*/
public class PublicLanternEntity extends SpiritLanternEntity {
    public PublicLanternEntity(EntityType<? extends SpiritLanternEntity> entityType, World world) {
        super(entityType, world);
    }

    public boolean shouldRenderBoundary() {
        Map<UUID, Integer> player_contributors = LanternInStormSpiritManager.get_spirits(this.getUuid());
        return player_contributors.getOrDefault(MinecraftClient.getInstance().player.getUuid(), 0) > 0;
    }
}
