package top.bearcabbage.lanterninstorm.entity;

import net.minecraft.block.AbstractFireBlock;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import top.bearcabbage.lanterninstorm.entity.entities.PrivateLantern;
import top.bearcabbage.lanterninstorm.entity.entities.PublicLantern;
import top.bearcabbage.lanterninstorm.interfaces.EntityAccessor;
import top.bearcabbage.lanterninstorm.player.Spirit;
import java.util.*;

//参考net.minecraft.entity.decoration.EndCrystalEntity
public abstract class SpiritLanternEntity extends Entity implements EntityAccessor {
    //这里是xxbc写的灯笼交互逻辑
    private final String LSid;
    private final Map<UUID, Spirit> SPIRIT = new HashMap<>();

    public String getLSid () {
        return this.LSid;
    }// 获取灯笼实体的LSid

    public Spirit getSpirit (UUID uuid) {
        return SPIRIT.get(uuid);
    }// 获取灯笼实体的灵魂

    public Object getSpiritMap() {
        return SPIRIT;
    }

    public ActionResult onRiding (PlayerEntity player) {
        if (this.startRiding(player)) {
            // 对SPIRIT中所有的Spirit调用onRide(ServerPlayerEntity player)
            if (player instanceof ServerPlayerEntity serverPlayer) {
                SPIRIT.values().forEach(spirit -> spirit.onRide(serverPlayer));
            }
            return ActionResult.FAIL;
        }
        return ActionResult.PASS;
    }// 玩家搬起灯笼

    public ActionResult onPutting () {
        this.stopRiding();
        // 对SPIRIT中所有的Spirit调用onPutdown(SpiritLanternEntity entity)
        SPIRIT.values().forEach(spirit -> spirit.onPutdown(this));
        return ActionResult.FAIL;
    }// 玩家放下灯笼

    public ActionResult onAddSpirit (ServerPlayerEntity player, int spiritMass) {

        // 获取玩家的UUID
        UUID uuid = player.getUuid();

        return ActionResult.FAIL;
    }// 玩家向灯笼添加灵魂

    //    从这里开始是HHHor写(copy)的实体代码
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
        this.LSid = this.getUuidAsString(); //测试用 在这之前要完成LSid和SPIRIT的初始化
        SpiritLanternEntityManager.loadSpiritLanternEntity(this);
        this.intersectionChecked = true;
        this.Age = this.random.nextInt(100000);
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

    @Override
    public void move(MovementType movementType, Vec3d movement) {
        this.LanternExplode(null);
        this.kill();//注释掉此行使爆炸后不消失
    }

    @Override
    public void kill() {
        //add new events here
        super.kill();
    }

    public void LanternExplode(@Nullable Entity entity) {
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
    }

    @Override
    public void preRemoved() {
        SpiritLanternEntityManager.unloadSpiritLanternEntity(this);
    }// 使用Mixin处理区块卸载时对Manager对通知
}