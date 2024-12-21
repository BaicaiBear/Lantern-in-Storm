package top.bearcabbage.lanterninstorm.entity;

import net.minecraft.block.AbstractFireBlock;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import top.bearcabbage.lanterninstorm.LanternInStormSpiritManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import static top.bearcabbage.lanterninstorm.LanternInStormSpiritManager.DISTANCE_PER_SPIRIT;


//参考net.minecraft.entity.decoration.EndCrystalEntity
public abstract class SpiritLanternEntity extends Entity {

    public int Age;
    public static Map<UUID, SpiritLanternEntity> lantern_list = new HashMap<>();

    public static final EntityType<PrivateLanternEntity> PRIVATE_LANTERN =  Registry.register(Registries.ENTITY_TYPE, Identifier.of("lanterninstorm","private_lantern"), EntityType.Builder.<PrivateLanternEntity>create(PrivateLanternEntity::new, SpawnGroup.MISC)
            .makeFireImmune()
            .dimensions(2.0F, 2.0F)
            .maxTrackingRange(64)
            .trackingTickInterval(Integer.MAX_VALUE).build("PrivateLantern"));
    public static final EntityType<PublicLanternEntity> PUBLIC_LANTERN =  Registry.register(Registries.ENTITY_TYPE, Identifier.of("lanterninstorm","public_lantern"), EntityType.Builder.<PublicLanternEntity>create(PublicLanternEntity::new, SpawnGroup.MISC)
            .makeFireImmune()
            .dimensions(5.0F, 5.0F)
            .maxTrackingRange(64)
            .trackingTickInterval(Integer.MAX_VALUE).build("PublicLantern"));
    public SpiritLanternEntity(EntityType<? extends SpiritLanternEntity> entityType, World world) {
        super(entityType, world);
        this.intersectionChecked = true;
        this.Age = this.random.nextInt(100000);
    }

    protected void initDataTracker(DataTracker.Builder builder) {
    }

    // 用于在服务端初始化灯笼实体
    public static void init(){ return; }

    public void tick() {
        if (this.getY() < this.getWorld().getBottomY() && !this.getWorld().isClient) {
            this.discard();
        }
        ++this.Age;
        this.checkBlockCollision();
        this.tickPortalTeleportation();
        if (this.getWorld() instanceof ServerWorld) {
            BlockPos blockPos = this.getBlockPos();
            if (((ServerWorld) this.getWorld()).getEnderDragonFight() != null && this.getWorld().getBlockState(blockPos).isAir()) {
                this.getWorld().setBlockState(blockPos, AbstractFireBlock.getState(this.getWorld(), blockPos));
            }
        }
        if(!this.getWorld().isClient){
            LanternInStormSpiritManager.lanternPosUpdate(this);
            this.getServer().getPlayerManager().getPlayerList().forEach(LanternInStormSpiritManager::sendAll);

        } else if(SpiritLanternEntity.lantern_list.getOrDefault(this.getUuid(), null) == null){
            SpiritLanternEntity.lantern_list.put(this.getUuid(), this);
        }
    }

    protected void writeCustomDataToNbt(NbtCompound nbt) {
    }

    protected void readCustomDataFromNbt(NbtCompound nbt) {
    }

    @Override
    public void move(MovementType movementType, Vec3d movement) {
        this.LanternExplode();
        this.kill();//注释掉此行使爆炸后不消失
    }

    @Override
    public void kill() {
        //add new events here
        LanternInStormSpiritManager.remove_lantern_records(super.uuid);
        super.kill();
    }

    public void LanternExplode() {
        this.getWorld()
                .createExplosion(
                        this,
                        getDamageSources().generic(),
                        null,
                        this.getX(),
                        this.getY()+1,
                        this.getZ(),
                        2F,
                        false,
                        World.ExplosionSourceType.BLOCK);
    }

    public boolean canHit() {
        return true;
    }
    @Override
    public void tickRiding() {
        this.setVelocity(Vec3d.ZERO);
        this.tick();
        if (this.hasVehicle()) {
            this.getVehicle().updatePassengerPosition(this);
        }
        if(!this.getWorld().isClient) this.getServer().getPlayerManager().getPlayerList().forEach(LanternInStormSpiritManager::sendAll);
    }

    @Override
    public void stopRiding() {
        Entity entity = this.getVehicle();
        super.stopRiding();
        if (entity != null && entity != this.getVehicle() && !this.getWorld().isClient) {
            this.onDismounted(entity);
        }
    }

    private void onDismounted(Entity vehicle) {
        Vec3d vec3d;
        if (this.isRemoved()) {
            vec3d = this.getPos();
        } else if (!vehicle.isRemoved() && !this.getWorld().getBlockState(vehicle.getBlockPos()).isIn(BlockTags.PORTALS)) {
            vec3d = new Vec3d(vehicle.getX(), vehicle.getBoundingBox().maxY, vehicle.getZ());
        } else {
            double d = Math.max(this.getY(), vehicle.getY());
            vec3d = new Vec3d(this.getX(), d, this.getZ());
        }
        this.requestTeleportAndDismount(vec3d.x, vec3d.y, vec3d.z);
    }

    public float getRadius() {
        return LanternInStormSpiritManager.get_sum(this.getUuid()) * DISTANCE_PER_SPIRIT;
    }
}