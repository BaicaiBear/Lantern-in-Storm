package top.bearcabbage.lanterninstorm.mixin;

import com.google.common.collect.Maps;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.bearcabbage.lanterninstorm.interfaces.PlayerManagerAccessor;

import java.util.Map;
import java.util.UUID;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin implements PlayerManagerAccessor {
    @Shadow private final Map<UUID, ServerPlayerEntity> playerMap = Maps.newHashMap();

    @Override
    public ServerPlayerEntity uuid2Player(UUID uuid) {
        return (ServerPlayerEntity) this.playerMap.get(uuid);
    }

}
