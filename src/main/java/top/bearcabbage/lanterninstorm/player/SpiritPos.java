package top.bearcabbage.lanterninstorm.player;

import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import top.bearcabbage.lanterninstorm.entity.SpiritLanternEntity;

public class SpiritPos {
    Vec3d pos;
    int spiritMass;
    RegistryKey<World> world;
    boolean isMoving = false;
    ServerPlayerEntity ridder;

    private SpiritPos(Vec3d pos, int spiritMass, RegistryKey<World> world){
        this.pos = pos;
        this.spiritMass = spiritMass;
        this.world = world;
    }

    public static SpiritPos of(Vec3d pos, int spiritMass, RegistryKey<World> world){
        return new SpiritPos(pos, spiritMass, world);
    }

    public Vec3d getPos() {
        return isMoving ? this.ridder.getPos() : pos;
    }

    public int getSpiritMass() {
        return spiritMass;
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
