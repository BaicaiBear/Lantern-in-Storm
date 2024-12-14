package top.bearcabbage.lanterninstorm.interfaces;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

public interface EntityTypeAccessor {

    static <T extends Entity> EntityType<T> register(String id, EntityType.Builder<T> type){
        return null;
    };
}
