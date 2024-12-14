package top.bearcabbage.lanterninstorm.utils;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import top.bearcabbage.lanterninstorm.interfaces.PlayerManagerAccessor;
import top.bearcabbage.lanterninstorm.player.Spirit;

import java.util.UUID;

/*
    用于对接Gson的ToJsonTree读写Spirit的数据结构
 */
public class SpiritData {
    public String ownerUUID;
    public double posX;
    public double posY;
    public double posZ;
    public int spiritMass;
    public String world;

    public SpiritData(Spirit spirit) {
        this.ownerUUID = spirit.getOwnerUUID().toString();
        this.posX = spirit.getPos().x;
        this.posY = spirit.getPos().y;
        this.posZ = spirit.getPos().z;
        this.spiritMass = spirit.getSpiritMass();
        this.world = spirit.getWorld().getValue().toString();
    }

    public Spirit toSpirit (MinecraftServer server){
        PlayerManagerAccessor playerManager = (PlayerManagerAccessor) server.getPlayerManager();
        return Spirit.of(playerManager.uuid2Player(UUID.fromString(ownerUUID)), new Vec3d(posX, posY, posZ), spiritMass, RegistryKey.of(RegistryKeys.WORLD, Identifier.of(world)));
    }

    public Vec3d getPos() {
        return new Vec3d(posX, posY, posZ);
    }
}
