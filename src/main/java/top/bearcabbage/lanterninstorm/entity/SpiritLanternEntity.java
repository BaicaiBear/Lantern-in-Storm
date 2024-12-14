package top.bearcabbage.lanterninstorm.entity;

import net.minecraft.block.AbstractFireBlock;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import top.bearcabbage.lanterninstorm.entity.entities.PrivateLantern;
import top.bearcabbage.lanterninstorm.entity.entities.PublicLantern;


public class SpiritLanternEntity extends Entity {

    // 这里是xxbc写的灯笼交互逻辑
    private String LSid;


    // 获取灯笼实体的LSid
    public String getLSid () {
        return this.LSid;
    }


    // 从这里开始是HHHor写的实体代码，xxbc看不懂AwA
    public int Age;
    public static final EntityType<PrivateLantern> PRIVATE_LANTERN =  Registry.register(Registries.ENTITY_TYPE, Identifier.of("lanterninstorm","private_lantern"), EntityType.Builder.<PrivateLantern>create(PrivateLantern::new, SpawnGroup.MISC)
            .makeFireImmune()
            .dimensions(2.0F, 2.0F)
            .maxTrackingRange(64)
            .trackingTickInterval(Integer.MAX_VALUE).build("PrivateLantern"));
    public static final EntityType<PublicLantern> PUBLIC_LANTERN =  Registry.register(Registries.ENTITY_TYPE, Identifier.of("lanterninstorm","public_lantern"), EntityType.Builder.<PublicLantern>create(PublicLantern::new, SpawnGroup.MISC)
            .makeFireImmune()
            .dimensions(5.0F, 5.0F)
            .maxTrackingRange(64)
            .trackingTickInterval(Integer.MAX_VALUE).build("PublicLantern"));


    public SpiritLanternEntity(EntityType<? extends SpiritLanternEntity> entityType, World world) {
        super(entityType, world);
        this.intersectionChecked = true;
        this.Age = this.random.nextInt(100000);
    }//参考net.minecraft.client.render.entity.EndCrystalEntity


    protected Entity.MoveEffect getMoveEffect() {
        return MoveEffect.NONE;
    }

    protected void initDataTracker(DataTracker.Builder builder) {
    }

    public void tick() {
        ++this.Age;
        this.checkBlockCollision();
        this.tickPortalTeleportation();
        if (this.getWorld() instanceof ServerWorld) {
            BlockPos blockPos = this.getBlockPos();
            if (((ServerWorld) this.getWorld()).getEnderDragonFight() != null && this.getWorld().getBlockState(blockPos).isAir()) {
                this.getWorld().setBlockState(blockPos, AbstractFireBlock.getState(this.getWorld(), blockPos));
            }
        }

    }

    protected void writeCustomDataToNbt(NbtCompound nbt) {
    }

    protected void readCustomDataFromNbt(NbtCompound nbt) {
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
    }



}