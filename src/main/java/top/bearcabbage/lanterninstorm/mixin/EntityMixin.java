package top.bearcabbage.lanterninstorm.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.bearcabbage.lanterninstorm.interfaces.EntityAccessor;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityAccessor {

    @Shadow public abstract World getWorld();

    public void preRemoved(){

    };

    @Inject(method = "setRemoved", at = @At("HEAD"))
    public void setRemoved(Entity.RemovalReason reason, CallbackInfo ci) {
        if(!this.getWorld().isClient) {
            this.preRemoved();
        }
    }
}
