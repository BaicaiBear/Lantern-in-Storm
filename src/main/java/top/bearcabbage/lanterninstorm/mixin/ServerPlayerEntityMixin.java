package top.bearcabbage.lanterninstorm.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.bearcabbage.lanterninstorm.interfaces.LSPlayerAccessor;
import top.bearcabbage.lanterninstorm.player.LSPlayer;
import top.bearcabbage.lanterninstorm.player.LSPlayerHandler;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements LSPlayerAccessor {
    public ServerPlayerEntityMixin(ServerWorld world, BlockPos blockPos, float f, GameProfile gameProfile) {
        super(world, blockPos, f, gameProfile);
    }

    @Unique
    private LSPlayer LS;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(CallbackInfo ci) {
        LS = new LSPlayer((ServerPlayerEntity) (Object) this);
    }

    @Override
    public LSPlayer getLS() {
        return LS;
    }

    @Shadow
    public abstract ServerWorld getServerWorld();
    @Inject(method = "getRespawnTarget", at = @At(value = "RETURN", ordinal = 1), cancellable = true)
    private void getRespawnTarget(boolean alive, TeleportTarget.PostDimensionTransition postDimensionTransition, CallbackInfoReturnable<TeleportTarget> cir) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        BlockPos spawnPos =  this.LS.getRtpSpawn();
        if(spawnPos == null){
            spawnPos = this.getServerWorld().getServer().getOverworld().getSpawnPos();
        }
        cir.setReturnValue(new TeleportTarget(this.getServerWorld().getServer().getOverworld(),spawnPos.toCenterPos(), Vec3d.ZERO, 0.0F, 0.0F, postDimensionTransition));
    }

    @Inject(method = "onDeath", at = @At("TAIL"))
    private void onDeath(CallbackInfo ci) {
        LSPlayerAccessor player = (LSPlayerAccessor) this;
        player.getLS().onDeath();
    }

    //CEPlayer日常任务
    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        LSPlayerHandler.onTick((ServerPlayerEntity) (Object) this);
    }
}
