package top.bearcabbage.lanterninstorm.entity;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.ExplosionBehavior;

@Environment(EnvType.CLIENT)
public class ForSpiritLantern implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.register(SpiritLanternEntity.SPIRIT_LANTERN_ENTITY, SpiritLanternEntityRenderer::new);
    }
}

public class SpiritLanternEntity extends Entity {
    public int Age;

    public SpiritLanternEntity(EntityType<? extends SpiritLanternEntity> entityType, World world) {
        super(EntityType.SpiritLanternEntity, world);
        this.intersectionChecked = true;
        this.Age = this.random.nextInt(100000);
    }//参考net.minecraft.client.render.entity.EndCrystalEntity

    public SpiritLanternEntity(World world, double x, double y, double z) {
        this(EntityType., world);
        this.setPosition(x, y, z);
    }

    public SpiritLanternEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    // 注册灵魂灯笼实体
    public static final SpiritLanternEntity SPIRIT_LANTERN_ENTITY = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of("lanterninstorm", "spirit_lantern"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, (type, world) -> new SpiritLanternEntity(SpiritLanternEntity, world)).dimensions(EntityDimensions.fixed(1.0f, 1.0f)).build()
    );

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
    public boolean damage(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else if (source.getAttacker() instanceof EnderDragonEntity) {
            return false;
        } else {
            if (!this.isRemoved() && !this.getWorld().isClient) {
                this.remove(Entity.RemovalReason.KILLED);
                if (!source.isIn(DamageTypeTags.IS_EXPLOSION)) {
                    DamageSource damageSource = source.getAttacker() != null ? this.getDamageSources().explosion(this, source.getAttacker()) : null;
                    this.getWorld().createExplosion(this, damageSource, null, this.getX(), this.getY(), this.getZ(), 6.0F, false, World.ExplosionSourceType.BLOCK);
                }
            }
            return true;
        }
    }
    public String getLSid () {
        return this.getUuid().toString();
    }
}