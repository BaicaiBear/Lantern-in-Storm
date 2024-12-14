package top.bearcabbage.lanterninstorm.interfaces;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

public interface PlayerManagerAccessor {
    ServerPlayerEntity uuid2Player(UUID uuid);
}
