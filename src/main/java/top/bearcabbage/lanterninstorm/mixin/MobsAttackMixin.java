package top.bearcabbage.lanterninstorm.mixin;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ZombieEntity.class)
public class MixinZombieEntity extends MobEntity{
    @Inject(method = "initCustomGoals", at = @At("TAIL"))
    private void injectInitCustomGoals(CallbackInfo ci) {
        ((ZombieEntity)(Object)this).targetSelector.add(1, new ActiveTargetGoal<>((ZombieEntity)(Object)this, PlayerEntity.class, true));
    }
}

@Mixin(CreeperEntity.class)
public class MixinZombieEntity extends MobEntity{
    @Inject(method = "initCustomGoals", at = @At("TAIL"))
    private void injectInitCustomGoals(CallbackInfo ci) {
        ((ZombieEntity)(Object)this).targetSelector.add(1, new ActiveTargetGoal<>((ZombieEntity)(Object)this, PlayerEntity.class, true));
    }
}

@Mixin(SkeletonEntity.class)
public class MixinZombieEntity extends MobEntity{
    @Inject(method = "initCustomGoals", at = @At("TAIL"))
    private void injectInitCustomGoals(CallbackInfo ci) {
        ((ZombieEntity)(Object)this).targetSelector.add(1, new ActiveTargetGoal<>((ZombieEntity)(Object)this, PlayerEntity.class, true));
    }
}