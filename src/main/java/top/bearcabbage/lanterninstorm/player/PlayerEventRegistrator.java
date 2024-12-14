package top.bearcabbage.lanterninstorm.player;

import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import top.bearcabbage.lanterninstorm.LanternInStorm;
import top.bearcabbage.lanterninstorm.entity.SpiritLanternEntity;

public abstract class PlayerEventRegistrator extends LanternInStorm {

    public static void register() {
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (player.getPassengerList().stream().anyMatch(entity1 -> entity1 instanceof SpiritLanternEntity) && player.getMainHandStack().isOf(Items.POPPED_CHORUS_FRUIT)) {
                SpiritLanternEntity lantern = (SpiritLanternEntity) player.getPassengerList().stream().filter(entity1 -> entity1 instanceof SpiritLanternEntity).findFirst().get();
                // 将灯笼放下来的操作
                player.sendMessage(Text.of("Down"));
                return lantern.onPutting();
            } else if (entity instanceof SpiritLanternEntity lantern) {
                if (player.getMainHandStack().isOf(Items.POPPED_CHORUS_FRUIT)) {
                    // 将灯笼捡起来的操作
                    player.sendMessage(Text.of("Up"));
                    return lantern.onRiding(player);
                } else if (player instanceof ServerPlayerEntity serverPlayer) {
                    // 玩家与灯笼右键交互的操作
                    player.sendMessage(Text.of("Right"));
                    return lantern.onAddSpirit(serverPlayer, 1);
                }
            }
            return ActionResult.PASS;
        });

        UseBlockCallback.EVENT.register((player, world, hand, blockHitResult) -> {
            if (player.getPassengerList().stream().anyMatch(entity1 -> entity1 instanceof SpiritLanternEntity) && player.getMainHandStack().isOf(Items.POPPED_CHORUS_FRUIT)) {
                SpiritLanternEntity spiritLanternEntity = (SpiritLanternEntity) player.getPassengerList().stream().filter(entity1 -> entity1 instanceof SpiritLanternEntity).findFirst().get();
                // 将灯笼放下来的操作
                player.sendMessage(Text.of("Down"));
                return spiritLanternEntity.onPutting();
            } else return ActionResult.PASS;
        });


        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (entity instanceof SpiritLanternEntity lantern && player instanceof ServerPlayerEntity serverPlayer && (!player.getMainHandStack().isOf(Items.POPPED_CHORUS_FRUIT))) {
                    // 玩家与灯笼左键交互的操作
                player.sendMessage(Text.of("Left"));
                return lantern.onAddSpirit(serverPlayer, -1);
            } else return ActionResult.PASS;
        });
    }
}
