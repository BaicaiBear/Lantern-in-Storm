package top.bearcabbage.lanterninstorm.entity;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@Environment(EnvType.CLIENT)
public class ForSpiritLantern implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        /*
         * 方块实体渲染器的注册，提供模型、阴影大小和纹理的渲染器。
         *
         * 实体渲染器也可以在实体基于上下文进行渲染前(EndermanEntityRenderer#render). 操作模型。
         */
        EntityRendererRegistry.INSTANCE.register(SpiritLanternEntity.SpiritLanternEntity, (dispatcher) -> {
            return new SpiritLanternEntityRenderer(dispatcher);
        });

        BlockEntityRendererRegistry.register(SpiritLanternEntity,SpiritLanternEntityRenderer::new);
    }
}


public class SpiritLanternEntity extends Entity {
    private static final TrackedData<Optional<BlockPos>> BEAM_TARGET;
    private static final TrackedData<Boolean> SHOW_BOTTOM;
    public int Age;

    public SpiritLanternEntity(SpiritLanternEntity entityType, World world) {
        super(entityType, world);
        this.intersectionChecked = true;
        this.Age = this.random.nextInt(100000);
    }//参考net.minecraft.client.render.entity.EndCrystalEntity

    public SpiritLanternEntity(World world, double x, double y, double z) {
        this(SpiritLanternEntity, world);
        this.setPosition(x, y, z);
    }

    public SpiritLanternEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    protected Entity.MoveEffect getMoveEffect() {
        return MoveEffect.NONE;
    }

    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(BEAM_TARGET, Optional.empty());
        builder.add(SHOW_BOTTOM, true);
    }

    public void tick() {
        ++this.Age;
        this.checkBlockCollision();
        this.tickPortalTeleportation();
        if (this.getWorld() instanceof ServerWorld) {
            BlockPos blockPos = this.getBlockPos();
            if (((ServerWorld)this.getWorld()).getEnderDragonFight() != null && this.getWorld().getBlockState(blockPos).isAir()) {
                this.getWorld().setBlockState(blockPos, AbstractFireBlock.getState(this.getWorld(), blockPos));
            }
        }

    }

    protected void writeCustomDataToNbt(NbtCompound nbt) {
        if (this.getBeamTarget() != null) {
            nbt.put("beam_target", NbtHelper.fromBlockPos(this.getBeamTarget()));
        }

        nbt.putBoolean("ShowBottom", this.shouldShowBottom());
    }

    protected void readCustomDataFromNbt(NbtCompound nbt) {
        NbtHelper.toBlockPos(nbt, "beam_target").ifPresent(this::setBeamTarget);
        if (nbt.contains("ShowBottom", 1)) {
            this.setShowBottom(nbt.getBoolean("ShowBottom"));
        }

    }

    public boolean canHit() {
        return true;
    }

    public boolean damage(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else if (source.getAttacker() instanceof EnderDragonEntity) {
            return false;
        } else {
            if (!this.isRemoved() && !this.getWorld().isClient) {
                this.remove(RemovalReason.KILLED);
                if (!source.isIn(DamageTypeTags.IS_EXPLOSION)) {
                    DamageSource damageSource = source.getAttacker() != null ? this.getDamageSources().explosion(this, source.getAttacker()) : null;
                    this.getWorld().createExplosion(this, damageSource, (ExplosionBehavior)null, this.getX(), this.getY(), this.getZ(), 6.0F, false, World.ExplosionSourceType.BLOCK);
                }
                this.crystalDestroyed(source);
            }

            return true;
        }
    }

    public void kill() {
        this.crystalDestroyed(this.getDamageSources().generic());
        super.kill();
    }

    private void crystalDestroyed(DamageSource source) {
        if (this.getWorld() instanceof ServerWorld) {
            EnderDragonFight enderDragonFight = ((ServerWorld)this.getWorld()).getEnderDragonFight();
            if (enderDragonFight != null) {
                enderDragonFight.crystalDestroyed(this, source);
            }
        }

    }

    public void setBeamTarget(@Nullable BlockPos beamTarget) {
        this.getDataTracker().set(BEAM_TARGET, Optional.ofNullable(beamTarget));
    }

    @Nullable
    public BlockPos getBeamTarget() {
        return (BlockPos)((Optional)this.getDataTracker().get(BEAM_TARGET)).orElse((Object)null);
    }

    public void setShowBottom(boolean showBottom) {
        this.getDataTracker().set(SHOW_BOTTOM, showBottom);
    }

    public boolean shouldShowBottom() {
        return (Boolean)this.getDataTracker().get(SHOW_BOTTOM);
    }

    public boolean shouldRender(double distance) {
        return super.shouldRender(distance) || this.getBeamTarget() != null;
    }

    public ItemStack getPickBlockStack() {
        return new ItemStack(Items.END_CRYSTAL);
    }

    static {
        BEAM_TARGET = DataTracker.registerData(net.minecraft.entity.decoration.EndCrystalEntity.class, TrackedDataHandlerRegistry.OPTIONAL_BLOCK_POS);
        SHOW_BOTTOM = DataTracker.registerData(net.minecraft.entity.decoration.EndCrystalEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    }
}
