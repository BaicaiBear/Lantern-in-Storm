package top.bearcabbage.lanterninstorm.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import top.bearcabbage.lanterninstorm.LanternInStormSpiritManager;

import java.util.List;

public class LanternBoundaryEntity extends Entity {

    public static final EntityType<LanternBoundaryEntity> LANTERN_BOUNDARY = Registry.register(Registries.ENTITY_TYPE, Identifier.of("lanterninstorm","lantern_boundary"), EntityType.Builder.<LanternBoundaryEntity>create(LanternBoundaryEntity::new, SpawnGroup.MISC)
            .makeFireImmune()
            .dimensions(0F, 0F)
            //.disableSummon()
            .disableSaving()
            .build("lanternboundary"));

    public LanternBoundaryEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    public void tick() {
        this.baseTick();
    }

    @Override
    public void baseTick() {
        this.setOnFire(false);
        if (this.hasVehicle()) {
            this.stopRiding();
        }
        this.setAngles(0,0);
        this.setPos(0,0,0);

        if (this.getY() < this.getWorld().getBottomY()) {
            this.discard();
        }
    }


    @Override
    public void onDamaged(DamageSource source) {
        if (source.isOf(DamageTypes.OUT_OF_WORLD)) {
            this.remove(RemovalReason.DISCARDED);
        }
    }


    @Override
    protected boolean canStartRiding(Entity entity) {
        return false;
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return false;
    }


    @Override
    public boolean updateMovementInFluid(TagKey<Fluid> tag, double speed) {
        return false;
    }

    @Override
    public boolean isSpectator() {
        return false;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {

    }


    @Override
    public int getFireTicks() {
        return -1;
    }

    @Override
    public void setFireTicks(int ticks) {
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
    public boolean isSilent() {
        return true;
    }

    @Override
    public void setSilent(boolean silent) {
    }

    @Override
    public boolean hasNoGravity() {
        return true;
    }

    @Override
    public void setNoGravity(boolean noGravity) {
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {

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
    public boolean isCustomNameVisible() {
        return false;
    }


    @Override
    public boolean shouldRender(double distance) {
        List<SpiritLanternEntity> lanternsHere = this.getWorld().getEntitiesByClass(SpiritLanternEntity.class, Box.of(this.getPos(),1,1,1), (entity) -> entity instanceof SpiritLanternEntity);
        if (lanternsHere.size()==1) {
            SpiritLanternEntity lantern = lanternsHere.getFirst();
            double d = Math.abs(LanternInStormSpiritManager.get_sum(lantern.getUuid()) * 2D + 1D);
            if (Double.isNaN(d)) {
                d = 1.0D;
            }

            d *= 64.0D * getVisibilityBoundingBox().getAverageSideLength();
            return distance < d * d;
        }
        return false;
    }
}
