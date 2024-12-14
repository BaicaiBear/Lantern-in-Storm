package top.bearcabbage.lanterninstorm.entity;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
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
import top.bearcabbage.lanterninstorm.LanternInStorm;

@Environment(EnvType.CLIENT)
public class ForSpiritLantern implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        public static final EntityType<SpiritLanternEntity> SpiritLanternEntityType = Registry.register(Registries.ENTITY_TYPE,
                Identifier.of(LanternInStorm.MOD_ID,"entity"),
                EntityType.Builder.create(SpiritLanternEntity::new,SpawnGroup.MISC).dimensions(1f,1f).build());



  /*      //向EntityType类import top.bearcabbage.lanterninstorm.entity.SpiritLanternEntity
        public static final EntityType<SpiritLanternEntity> END_CRYSTAL = EntityType.register(
                "SpiritLanternEntity",
                EntityType.Builder.<SpiritLanternEntity>create(SpiritLanternEntity::new, SpawnGroup.MISC)
                        .makeFireImmune()
                        .dimensions(2.0F, 2.0F)
                        .maxTrackingRange(64)
                        .trackingTickInterval(Integer.MAX_VALUE)
        );
        // 注册灵魂灯笼实体 注入到EntityType中间
        EntityRendererRegistry.register(SpiritLanternEntity, SpiritLanternEntityRenderer::new);
    }
}*/

public class SpiritLanternEntity extends Entity {
    public int Age;

    public SpiritLanternEntity(EntityType<? extends SpiritLanternEntity> entityType, World world) {
        super(entityType, world);
        this.intersectionChecked = true;
        this.Age = this.random.nextInt(100000);
    }//参考net.minecraft.client.render.entity.EndCrystalEntity

    public SpiritLanternEntity(World world, double x, double y, double z) {
        this(SpiritLanternEntityType, world);
        this.setPosition(x, y, z);
    }

    public SpiritLanternEntity(EntityType<?> type, World world) {
        super(type, world);
    }

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

    public String getLSid () {
        return this.getUuid().toString();
    }
}