package top.bearcabbage.lanterninstorm.player;

import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import top.bearcabbage.lanterninstorm.entity.SpiritLanternEntity;

import java.util.UUID;


/*
    专门用于描述Spirit的类
    之前的玩家属性Spirit类被重命名为PlayerSpirit
 */
public class Spirit {
    UUID ownerUUID;
    Vec3d pos;
    int spiritMass;
    RegistryKey<World> world;
    boolean isMoving = false;
    ServerPlayerEntity ridder;

    private Spirit(ServerPlayerEntity player, Vec3d pos, int spiritMass, RegistryKey<World> world){
        this.ownerUUID = player.getUuid();
        this.pos = pos;
        this.spiritMass = spiritMass;
        this.world = world;
    }

    public static Spirit of(ServerPlayerEntity player, Vec3d pos, int spiritMass, RegistryKey<World> world){
        return new Spirit(player, pos, spiritMass, world);
    }

    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    public Vec3d getPos() {
        return isMoving ? this.ridder.getPos() : pos;
    }

    public int getSpiritMass() {
        return spiritMass;
    }

    public RegistryKey<World> getWorld() {
        return world;
    }

    public void onRide(ServerPlayerEntity player){
        if (isMoving){
            return;
        }
        ridder = player;
        isMoving = true;
    }

    public void onPutdown(SpiritLanternEntity entity){
        if (!isMoving || entity.getEntityWorld().isClient){
            return;
        }
        ridder = null;
        isMoving = false;
        pos = entity.getPos();
        world = entity.getEntityWorld().getRegistryKey();
    }


}
