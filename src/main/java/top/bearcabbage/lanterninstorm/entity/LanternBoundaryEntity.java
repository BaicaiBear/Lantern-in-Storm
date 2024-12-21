package top.bearcabbage.lanterninstorm.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import top.bearcabbage.lanterninstorm.LanternInStormSpiritManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static top.bearcabbage.lanterninstorm.player.Player.DISTANCE_PER_SPIRIT;
@Environment(EnvType.CLIENT)
public class LanternBoundaryEntity extends Entity {

    public static final Map<UUID, LanternBoundaryEntity> LANTERN_BOUNDARY_MAP = new HashMap<>();
    private SpiritLanternEntity lantern;
    private int radius = 0;
    private boolean own = false;

    public static final EntityType<LanternBoundaryEntity> LANTERN_BOUNDARY = Registry.register(Registries.ENTITY_TYPE, Identifier.of("lanterninstorm","lantern_boundary"), EntityType.Builder.<LanternBoundaryEntity>create(LanternBoundaryEntity::new, SpawnGroup.MISC)
            .makeFireImmune()
            .dimensions(0.1F, 0.1F)
//            .disableSummon()
            .disableSaving()
            .build("lantern_boundary"));

    public LanternBoundaryEntity(EntityType<?> type, World world) {
        super(type, world);
    }


    void setLantern(SpiritLanternEntity lantern) {
        this.lantern = lantern;
    }

    @Override
    public void baseTick() {
        this.setOnFire(false);
        if (this.getLantern() == null) {
            this.discard();
            return;
        } else {
            if (this.getWorld() != this.getLantern().getWorld()) this.setWorld(this.getLantern().getWorld());
            this.setVelocity(this.getLantern().getVelocity());
            this.refreshPositionAfterTeleport(this.getLantern().getX(), this.getLantern().getY(), this.getLantern().getZ());
        }
        if (this.hasVehicle()) {
            this.stopRiding();
        }
        this.setAngles(0,0);
        if (this.getY() < this.getWorld().getBottomY()) {
            this.discard();
        }
    }

    @Override
    public boolean canAddPassenger(Entity passenger) {
        return false;
    }

    @Override
    protected boolean canStartRiding(Entity entity) {
        return false;
    }

    public SpiritLanternEntity getLantern() {
        return lantern;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getRadius() {
        return radius;
        //return LanternInStormSpiritManager.get_sum(this.getLantern().getUuid())*DISTANCE_PER_SPIRIT;
    }

    public void setOwn(boolean own) {
        this.own = own;
    }

    public boolean isOwn() {
        return own;
    }

    @Override
    public boolean hasNoGravity() {
        return true;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {}

    @Override
    public boolean isCustomNameVisible() {
        return false;
    }

    @Override
    public boolean isFireImmune() {
        return true;
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    public boolean isSilent() {
        return true;
    }

    @Override
    public boolean isSpectator() {
        return false;
    }

    @Override
    public boolean shouldRender(double distance) {
        return true;
        //return getLantern() != null;
    }

    @Override
    public void onDamaged(DamageSource source) {
        if (source.isOf(DamageTypes.OUT_OF_WORLD)) {
            this.remove(RemovalReason.DISCARDED);
        }
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {}

    @Override
    public void setFireTicks(int ticks) {}

    @Override
    public void setNoGravity(boolean noGravity) {}

    @Override
    public void setSilent(boolean silent) {}

    @Override
    public void tick() {
        this.baseTick();
    }

    @Override
    public void tickRiding() {
        this.tick();
        if (this.hasVehicle()) {
            this.getVehicle().updatePassengerPosition(this);
        }
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

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {}

    @Override
    public int getFireTicks() {
        return -1;
    }

    @Override
    protected float getJumpVelocityMultiplier() {
        return 0;
    }

    @Override
    protected float getVelocityMultiplier() {
        return 0;
    }

    @Override
    public boolean updateMovementInFluid(TagKey<Fluid> tag, double speed) {
        return false;
    }
}