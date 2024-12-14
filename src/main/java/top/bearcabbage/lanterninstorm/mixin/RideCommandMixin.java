package top.bearcabbage.lanterninstorm.mixin;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.server.command.RideCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(RideCommand.class)
public class RideCommandMixin {


    @Shadow private static final Dynamic2CommandExceptionType ALREADY_RIDING_EXCEPTION = new Dynamic2CommandExceptionType((rider, vehicle) -> {
        return Text.stringifiedTranslatable("commands.ride.already_riding", new Object[]{rider, vehicle});
    });
    @Shadow private static final Dynamic2CommandExceptionType GENERIC_FAILURE_EXCEPTION = new Dynamic2CommandExceptionType((rider, vehicle) -> {
        return Text.stringifiedTranslatable("commands.ride.mount.failure.generic", new Object[]{rider, vehicle});
    });
    @Shadow private static final SimpleCommandExceptionType RIDE_LOOP_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.ride.mount.failure.loop"));
    @Shadow private static final SimpleCommandExceptionType WRONG_DIMENSION_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.ride.mount.failure.wrong_dimension"));

    /**
     * @author
     * @reason
     */
    @Overwrite
    private static int executeMount(ServerCommandSource source, Entity rider, Entity vehicle) throws CommandSyntaxException {
        Entity entity = rider.getVehicle();
        if (entity != null) {
            throw ALREADY_RIDING_EXCEPTION.create(rider.getDisplayName(), entity.getDisplayName());
        } else if (rider.streamSelfAndPassengers().anyMatch((passenger) -> {
            return passenger == vehicle;
        })) {
            throw RIDE_LOOP_EXCEPTION.create();
        } else if (rider.getWorld() != vehicle.getWorld()) {
            throw WRONG_DIMENSION_EXCEPTION.create();
        } else if (!rider.startRiding(vehicle, true)) {
            throw GENERIC_FAILURE_EXCEPTION.create(rider.getDisplayName(), vehicle.getDisplayName());
        } else {
            source.sendFeedback(() -> {
                return Text.translatable("commands.ride.mount.success", new Object[]{rider.getDisplayName(), vehicle.getDisplayName()});
            }, true);
            return 1;
        }
    }
}
